package org.jono.medicalmodelsservice.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentRelationshipRepository {

    private final DocumentRelationshipCrudRepository documentRelationshipCrudRepository;

    public DocumentRelationship create(final String parentId, final String docId) {
        return this.documentRelationshipCrudRepository.save(new DocumentRelationship(parentId, docId));
    }
}
