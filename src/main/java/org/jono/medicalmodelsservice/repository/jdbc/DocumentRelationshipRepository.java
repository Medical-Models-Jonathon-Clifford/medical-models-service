package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRelationshipRepository {

    private final DocumentRelationshipCrudRepository documentRelationshipCrudRepository;

    @Autowired
    public DocumentRelationshipRepository(final DocumentRelationshipCrudRepository documentRelationshipCrudRepository) {
        this.documentRelationshipCrudRepository = documentRelationshipCrudRepository;
    }

    public DocumentRelationship create(final String parentId, final String docId) {
        return this.documentRelationshipCrudRepository.save(new DocumentRelationship(parentId, docId));
    }
}
