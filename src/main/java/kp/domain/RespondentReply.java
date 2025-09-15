package kp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Simple JavaBean domain object representing a respondent reply.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class RespondentReply {
    @Id
    private long id;// a unique, non-sequential ID
    private String response;
    private String respondentSignature = "";
    private LocalDateTime replyDate;
}
