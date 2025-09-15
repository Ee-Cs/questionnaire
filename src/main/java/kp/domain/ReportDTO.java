package kp.domain;

/**
 * A DTO for report.
 *
 */
public record ReportDTO(
        Long questionnaireId,
        String name,
        Long inboxId,
        String topic,
        String ownerName,
        Long respondentReplyId,
        String response,
        String respondentName) {
}