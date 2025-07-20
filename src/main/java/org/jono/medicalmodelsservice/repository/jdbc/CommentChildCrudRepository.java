package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.springframework.data.repository.CrudRepository;

public interface CommentChildCrudRepository extends CrudRepository<CommentChild, String> {
  List<CommentChild> findAllByDocumentId(String documentId);

  List<CommentChild> findAllByCommentId(String commentId);

  List<CommentChild> findAllByChildCommentId(String childCommentId);

  CommentChild findFirstByChildCommentId(String childCommentId);
}
