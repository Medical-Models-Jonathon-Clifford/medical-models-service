package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentCompanyRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentCompanyRelationshipRepository {

    private final DocumentCompanyRelationshipCrudRepository documentCompanyRelationshipCrudRepository;

    @Autowired
    public DocumentCompanyRelationshipRepository(
            final DocumentCompanyRelationshipCrudRepository documentCompanyRelationshipCrudRepository) {
        this.documentCompanyRelationshipCrudRepository = documentCompanyRelationshipCrudRepository;
    }

    public DocumentCompanyRelationship create(final String documentId, final String companyId) {
        return this.documentCompanyRelationshipCrudRepository.save(
                new DocumentCompanyRelationship(documentId, companyId));
    }
}
