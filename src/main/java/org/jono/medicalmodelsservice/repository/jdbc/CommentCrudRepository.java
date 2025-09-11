package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.Comment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentCrudRepository extends CrudRepository<Comment, String> {
    List<Comment> findAllByDocumentId(String documentId);

    @Query("SELECT COUNT(c.id) FROM comment c "
            + "JOIN document_company_relationship dcr ON c.document_id = dcr.document_id "
            + "WHERE dcr.company_id = :companyId")
    long countByCompanyId(String companyId);
}
