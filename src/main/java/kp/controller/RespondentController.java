package kp.controller;

import kp.domain.Inbox;
import kp.domain.Questionnaire;
import kp.domain.RespondentReply;
import kp.repository.QuestionnaireRepository;
import kp.utils.TripcodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kp.Constants.POST_REPLIES_PATH;
import static kp.Constants.READ_QUESTIONNAIRE_AS_RESPONDENT_PATH;

/**
 * Controller used by the respondents.
 */
@RestController
public class RespondentController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionnaireRepository questionnaireRepository;

    /**
     * Constructor.
     *
     * @param questionnaireRepository the {@link QuestionnaireRepository}
     */
    public RespondentController(QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
    }

    /**
     * Reads the questionnaire.
     * The respondent can read the public inbox information
     * - topic
     * - owner signature
     * - expiration date
     * The respondent is not allowed to read the replies.
     *
     * @param id the questionnaire id
     * @return the questionnaire
     */
    @GetMapping(READ_QUESTIONNAIRE_AS_RESPONDENT_PATH)
    public Questionnaire readQuestionnaireAsRespondent(@RequestParam long id) {

        final Optional<Questionnaire> questionnaireOptional = questionnaireRepository.findById(id);
        questionnaireOptional.map(quest -> {
            quest.getInboxes().forEach(inbox -> inbox.getRespondentReplies().clear());
            return quest;
        });
        logger.info("readQuestionnaireAsRespondent(): id[{}]", id);
        return questionnaireOptional.orElse(new Questionnaire());
    }

    /**
     * Posts the respondent replies.
     * The respondent can reply to an inbox.
     * Each message has:
     * - A body
     * - A timestamp
     * - A signature (or no signature if the message is anonymous)
     *
     * @param questionnaire the questionnaire
     * @return the respondent signature
     */
    @PostMapping(POST_REPLIES_PATH)
    public String postReplies(@RequestBody Questionnaire questionnaire) {

        final String respondentSignature = TripcodeUtils.createSignature(
                questionnaire.getUsername(), questionnaire.getSecret());
        questionnaireRepository.findById(questionnaire.getId()).map(quest -> {
            final List<Inbox> inboxesFromRespondent = questionnaire.getInboxes();
            quest.getInboxes().forEach(inbox ->
                    addReplyToInbox(inboxesFromRespondent, respondentSignature, inbox));
            return quest;
        }).ifPresent(questionnaireRepository::save);
        logger.info("postReplies(): id[{}], username[{}]", questionnaire.getId(), questionnaire.getUsername());
        return respondentSignature;
    }

    /**
     * Adds reply to inbox.
     *
     * @param inboxesFromRespondent the inboxes from respondent
     * @param respondentSignature   the respondent signature
     * @param inbox                 the target inbox
     */
    private void addReplyToInbox(List<Inbox> inboxesFromRespondent, String respondentSignature, Inbox inbox) {

        if (LocalDateTime.now().isAfter(inbox.getExpirationDate())) {
            // this inbox expired and cannot be responded to
            return;
        }
        if (respondentSignature.isBlank() && !inbox.isAnonymousSubmissionAllowed()) {
            // anonymous submissions is not allowed for this inbox
            return;
        }
        final Optional<List<RespondentReply>> respondentRepliesOpt = inboxesFromRespondent.stream()
                .filter(inboxFromRespondent -> inboxFromRespondent.getId() == inbox.getId())
                .map(Inbox::getRespondentReplies).findFirst();
        if (respondentRepliesOpt.isEmpty()) {
            return;
        }
        if (!respondentSignature.isBlank()) {
            respondentRepliesOpt.get().forEach(reply -> reply.setRespondentSignature(respondentSignature));
        }
        inbox.getRespondentReplies().addAll(respondentRepliesOpt.get());
    }
}
