package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentCrudRepository extends CrudRepository<Comment, String>  {
    List<Comment> findAllByDocumentId(String documentId);
}
