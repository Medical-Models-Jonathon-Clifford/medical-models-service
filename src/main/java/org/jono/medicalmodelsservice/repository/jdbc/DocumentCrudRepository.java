package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface DocumentCrudRepository extends CrudRepository<Document, String> {
    @Query("SELECT d.* FROM document d "
            + "JOIN document_company_relationship dcr ON d.id = dcr.document_id "
            + "WHERE dcr.company_id = :companyId")
    List<Document> findByCompanyId(String companyId);

}
