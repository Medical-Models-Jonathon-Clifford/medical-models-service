package org.jono.medicalmodelsservice.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.DocumentCompanyRelationship;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentCompanyRelationshipRepository {

    private final DocumentCompanyRelationshipCrudRepository documentCompanyRelationshipCrudRepository;

    public DocumentCompanyRelationship create(final String documentId, final String companyId) {
        return this.documentCompanyRelationshipCrudRepository.save(
                new DocumentCompanyRelationship(documentId, companyId));
    }
}
