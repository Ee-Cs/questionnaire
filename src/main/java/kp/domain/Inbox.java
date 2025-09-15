package kp.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JavaBean domain object representing an inbox.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Inbox {
    @Id
    private long id;
    @Getter
    private String topic;
    @Getter
    private String ownerSignature;
    private LocalDateTime expirationDate;
    private boolean anonymousSubmissionAllowed = false;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RespondentReply> respondentReplies = new ArrayList<>();
}
