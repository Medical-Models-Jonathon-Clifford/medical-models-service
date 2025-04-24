package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.CommentChild;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentChildCrudRepository extends CrudRepository<CommentChild, String> {
    List<CommentChild> findAllByDocumentId(String documentId);

    List<CommentChild> findAllByCommentId(String commentId);

    List<CommentChild> findAllByChildCommentId(String childCommentId);

    CommentChild findFirstByChildCommentId(String childCommentId);
}
