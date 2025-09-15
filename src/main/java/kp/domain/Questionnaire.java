package kp.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple JavaBean domain object representing a questionnaire.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Questionnaire {
    @Id
    private long id;
    private String name;
    @Transient
    private String username;
    @Transient
    private String secret;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Inbox> inboxes = new ArrayList<>();
}
