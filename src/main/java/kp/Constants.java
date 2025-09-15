package kp;

/**
 * The constants.
 */
@SuppressWarnings("doclint:missing")
public final class Constants {
    private static final String ROOT = "/";
    public static final String CREATE_QUESTIONNAIRE_PATH =
            ROOT + "create_questionnaire";
    public static final String READ_QUESTIONNAIRE_AS_QUESTIONER_PATH =
            ROOT + "read_questionnaire_as_questioner_path";
    public static final String READ_QUESTIONNAIRE_AS_RESPONDENT_PATH =
            ROOT + "read_questionnaire_as_respondent_path";
    public static final String POST_REPLIES_PATH =
            ROOT + "post_replies";

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
