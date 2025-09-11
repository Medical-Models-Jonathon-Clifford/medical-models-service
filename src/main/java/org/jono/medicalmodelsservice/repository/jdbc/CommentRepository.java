package org.jono.medicalmodelsservice.repository.jdbc;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.model.DailyResourceCount;
import org.jono.medicalmodelsservice.model.NewComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentRepository {

    private final CommentCrudRepository commentCrudRepository;
    private final CommentRelationshipRepository commentRelationshipRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(final CommentCrudRepository commentCrudRepository,
            final CommentRelationshipRepository commentRelationshipRepository, final JdbcTemplate jdbcTemplate) {
        this.commentCrudRepository = commentCrudRepository;
        this.commentRelationshipRepository = commentRelationshipRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        return this.commentCrudRepository.count();
    }

    public Comment create(final NewComment newComment) {
        final var comment = new Comment(newComment);
        final Comment savedComment = this.commentCrudRepository.save(comment);
        if (newComment.getParentCommentId() != null) {
            final var newCommentRelationship = new CommentRelationship(savedComment.getDocumentId(),
                                                                       newComment.getParentCommentId(),
                                                                       savedComment.getId());
            this.commentRelationshipRepository.save(newCommentRelationship);
        }
        return savedComment;
    }

    public List<Comment> findAllByDocumentId(final String documentId) {
        return this.commentCrudRepository.findAllByDocumentId(documentId);
    }

    public Optional<Comment> updateById(final String id, final Map<SqlIdentifier, Object> updateMap) {
        final Optional<Comment> currentComment = this.commentCrudRepository.findById(id);
        if (currentComment.isEmpty()) {
            return currentComment;
        }
        updateMap.forEach((key, value) -> {
            switch (key.toString()) {
                case "body":
                    currentComment.get().setBody(value.toString());
                    break;
                case "modified_date":
                    currentComment.get().setModifiedDate((LocalDateTime) value);
                    break;
                default:
                    log.info("Field in update map not expected: {}", key);
            }
        });
        return Optional.of(this.commentCrudRepository.save(currentComment.get()));
    }

    public void deleteAllById(final Collection<String> ids) {
        this.commentCrudRepository.deleteAllById(ids);
    }

    public List<DailyResourceCount> getCommentGrowthData() {
        final String sql = """
                     WITH RECURSIVE dates AS (
                         SELECT CAST(MIN(created_date) AS DATE) as date
                         FROM comment
                     
                         UNION ALL
                     
                         SELECT DATE_ADD(date, INTERVAL 1 DAY)
                         FROM dates
                         WHERE date < CURRENT_DATE()
                     ),
                     daily_counts AS (
                         SELECT 
                             CAST(created_date AS DATE) as date,
                             COUNT(*) as daily_count
                         FROM comment
                         GROUP BY CAST(created_date AS DATE)
                     )
                     SELECT 
                         d.date,
                         COALESCE(dc.daily_count, 0) as new_users,
                         SUM(COALESCE(dc.daily_count, 0)) OVER (ORDER BY d.date) as total_users
                     FROM dates d
                     LEFT JOIN daily_counts dc ON d.date = dc.date
                     ORDER BY d.date
                     """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            log.debug("Mapping row: {} {} {}",
                      rs.getDate("date"),
                      rs.getLong("new_users"),
                      rs.getLong("total_users"));

            return new DailyResourceCount(
                    rs.getDate("date").toLocalDate(),
                    rs.getLong("new_users"),
                    rs.getLong("total_users")
            );
        });
    }
}
