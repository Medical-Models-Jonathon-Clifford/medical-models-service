package org.jono.medicalmodelsservice.repository.jdbc;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.model.NewComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentRepository {

    private final CommentCrudRepository commentCrudRepository;
    private final CommentRelationshipRepository commentRelationshipRepository;

    @Autowired
    public CommentRepository(final CommentCrudRepository commentCrudRepository,
            final CommentRelationshipRepository commentRelationshipRepository) {
        this.commentCrudRepository = commentCrudRepository;
        this.commentRelationshipRepository = commentRelationshipRepository;
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
}
