package org.jono.medicalmodelsservice.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class CommentRepository {

  private final CommentCrudRepository commentCrudRepository;
  private final CommentChildCrudRepository commentChildCrudRepository;

  @Autowired
  public CommentRepository(final CommentCrudRepository commentCrudRepository, final CommentChildCrudRepository commentChildCrudRepository) {
    this.commentCrudRepository = commentCrudRepository;
    this.commentChildCrudRepository = commentChildCrudRepository;
  }

  public Comment create(final NewComment newComment) {
    final var comment = new Comment(newComment);
    final Comment savedComment = this.commentCrudRepository.save(comment);
    if (newComment.getParentCommentId() != null) {
      final var newCommentChild = new CommentChild(savedComment.getDocumentId(), newComment.getParentCommentId(), savedComment.getId());
      this.commentChildCrudRepository.save(newCommentChild);
    }
    return savedComment;
  }

  public void deleteByIds(final Collection<String> ids) {
    this.commentCrudRepository.deleteAllById(ids);
  }

  public Tuple2<List<CommentChild>, List<Comment>> getById(final String documentId) {
    final List<Comment> comments = this.commentCrudRepository.findAllByDocumentId(documentId);
    final List<CommentChild> commentChildren = this.commentChildCrudRepository.findAllByDocumentId(documentId);
    return new Tuple2<>(commentChildren, comments);
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
}
