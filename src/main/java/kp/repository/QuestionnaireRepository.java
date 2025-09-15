package kp.repository;

import kp.domain.Questionnaire;
import kp.domain.ReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * The repository interface for managing {@link Questionnaire} entities.
 * This extends the {@link JpaRepository}.
 */
@RepositoryRestResource
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    @Query("""
                SELECT new kp.domain.ReportDTO(
                    q.id,
                    q.name,
                    i.id,
                    i.topic,
                    SUBSTRING(i.ownerSignature, 1, LOCATE('=', i.ownerSignature) - 1),
                    r.id,
                    r.response,
                    SUBSTRING(r.respondentSignature, 1, LOCATE('=', r.respondentSignature) - 1)
                )
                FROM
                    Questionnaire q
                    JOIN q.inboxes i
                    JOIN i.respondentReplies r
                WHERE
                    i.ownerSignature LIKE CONCAT(:ownerName, '=%') AND
                    r.respondentSignature LIKE CONCAT(:respondentName, '=%')
            """)
    List<ReportDTO> findByCustomQuery(
            @Param("ownerName") String ownerName,
            @Param("respondentName") String respondentName);
    @Query("""
                SELECT new kp.domain.ReportDTO(
                    q.id,
                    q.name,
                    i.id,
                    i.topic,
                    SUBSTRING(i.ownerSignature, 1, LOCATE('=', i.ownerSignature) - 1),
                    r.id,
                    r.response,
                    ''
                )
                FROM
                    Questionnaire q
                    JOIN q.inboxes i
                    JOIN i.respondentReplies r
                WHERE
                    i.ownerSignature LIKE CONCAT(:ownerName, '=%') AND
                    r.respondentSignature = ''
            """)
    List<ReportDTO> findByCustomQuery(
            @Param("ownerName") String ownerName);
}