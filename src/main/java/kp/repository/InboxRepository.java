package kp.repository;

import kp.domain.Inbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * The repository interface for managing {@link Inbox} entities.
 * This extends the {@link JpaRepository}.
 *
 * @see org.springframework.data.repository.PagingAndSortingRepository
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.data.repository.Repository
 */
@RepositoryRestResource
public interface InboxRepository extends JpaRepository<Inbox, Long> {

    /**
     * Retrieves a list of {@link Inbox} entities by the topic.
     *
     * @param word the word in the topic of the {@link Inbox}
     * @return a list of {@link Inbox} entities
     */
    List<Inbox> findByTopicContaining(@Param("word") String word);

    /**
     * Counts the number of {@link Inbox} entities by the topic.
     *
     * @param word the word in the topic of the {@link Inbox}
     * @return the number of {@link Inbox} entities
     */
    long countByTopicContaining(@Param("word") String word);


    /**
     * Counts the number of {@link Inbox} entities by the anonymous submission allowed flag.
     *
     * @param anonymousSubmissionAllowed the anonymous submission allowed flag
     * @return the number of {@link Inbox} entities
     */
    long countByAnonymousSubmissionAllowed(@Param("anonymousSubmissionAllowed") boolean anonymousSubmissionAllowed);

    /**
     * Retrieves a {@link Slice} of {@link Inbox} entities ordered by topic.
     *
     * @param pageable the pagination information
     * @return a {@link Slice} of {@link Inbox} entities
     */
    Slice<Inbox> findAllByOrderByTopic(Pageable pageable);

}
