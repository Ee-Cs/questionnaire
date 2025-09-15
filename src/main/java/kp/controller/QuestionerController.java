package kp.controller;

import kp.domain.Inbox;
import kp.domain.Questionnaire;
import kp.domain.ReportDTO;
import kp.repository.QuestionnaireRepository;
import kp.utils.TripcodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

import static kp.Constants.CREATE_QUESTIONNAIRE_PATH;
import static kp.Constants.READ_QUESTIONNAIRE_AS_QUESTIONER_PATH;

/**
 * Controller used by the questioners.
 */
@RestController
public class QuestionerController {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionnaireRepository questionnaireRepository;

    /**
     * Constructor.
     *
     * @param questionnaireRepository the {@link QuestionnaireRepository}
     */
    public QuestionerController(QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
    }

    /**
     * Creates the questionnaire.
     * The questioner can create the questionnaire with several Inboxes.
     *
     * @param questionnaire the questionnaire
     * @return the owner signature
     */
    @PostMapping(CREATE_QUESTIONNAIRE_PATH)
    public String createQuestionnaire(@RequestBody Questionnaire questionnaire) {

        final String ownerSignature = TripcodeUtils.createSignature(
                questionnaire.getUsername(), questionnaire.getSecret());
        final List<Inbox> inboxes = questionnaire.getInboxes();
        if (inboxes != null && !inboxes.isEmpty()) {
            inboxes.forEach(inbox -> inbox.setOwnerSignature(ownerSignature));
        }
        questionnaireRepository.save(questionnaire);
        logger.info("createQuestionnaire(): id[{}], name[{}], username[{}]",
                questionnaire.getId(), questionnaire.getName(), questionnaire.getUsername());
        return ownerSignature;
    }

    /**
     * The questioner (the inbox owner) can read inbox replies
     * after providing credentials matching inbox's signature
     *
     * @param ownerSignature the owner signature
     * @param id             the questionnaire id
     * @return the questionnaire
     */
    @GetMapping(READ_QUESTIONNAIRE_AS_QUESTIONER_PATH)
    public Questionnaire readQuestionnaireAsQuestioner(@RequestParam long id,
                                                       @RequestParam String ownerSignature) {

        if (Objects.isNull(ownerSignature) || ownerSignature.isBlank()) {
            logger.warn("readQuestionnaireAsQuestioner(): ownerSignature is absent, id[{}]", id);
            return new Questionnaire();
        }
        final Questionnaire questionnaire = questionnaireRepository.findById(id)
                .map(quest -> clearRepliesFromInbox(quest, ownerSignature))
                .orElse(new Questionnaire());
        logger.info("readQuestionnaireAsQuestioner(): id[{}],\n\t ownerSignature[{}]", id, ownerSignature);
        return questionnaire;
    }

    /**
     * Creates the report with selected topics and responses.
     *
     * @param ownerName      the owner name
     * @param respondentName the respondent name
     * @return the report
     */
    @GetMapping("/report")
    public List<ReportDTO> report(@RequestParam String ownerName,
                                  @RequestParam String respondentName) {
        final List<ReportDTO> report;
        if(respondentName != null && !respondentName.isBlank()) {
            report = questionnaireRepository.findByCustomQuery(ownerName, respondentName);
        } else {
            report = questionnaireRepository.findByCustomQuery(ownerName);
        }
        logger.info("report(): ownerName[{}], respondentName[{}]", ownerName, respondentName);
        return report;
    }

    /**
     * Clear replies from inbox.
     *
     * @param questionnaire  the questionnaire from repository
     * @param ownerSignature the inbox owner signature
     */
    private Questionnaire clearRepliesFromInbox(Questionnaire questionnaire, String ownerSignature) {

        final List<Inbox> inboxes = questionnaire.getInboxes().stream()
                .filter(inbox -> ownerSignature.equals(inbox.getOwnerSignature()))
                .toList();
        questionnaire.setInboxes(inboxes);
        return questionnaire;
    }
}