package kp.repository;

import kp.domain.RespondentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The repository interface for managing {@link RespondentReply} entities.
 * This extends the {@link JpaRepository}.
 */
@RepositoryRestResource
public interface RespondentReplyRepository extends JpaRepository<RespondentReply, Long> {
}
