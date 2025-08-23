package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentCrudRepository extends CrudRepository<Comment, String> {
    List<Comment> findAllByDocumentId(String documentId);
}
