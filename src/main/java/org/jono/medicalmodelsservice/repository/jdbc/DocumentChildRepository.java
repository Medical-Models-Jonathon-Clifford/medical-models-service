package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentChildRepository {

    private final DocumentChildCrudRepository documentChildCrudRepository;

    @Autowired
    public DocumentChildRepository(final DocumentChildCrudRepository documentChildCrudRepository) {
        this.documentChildCrudRepository = documentChildCrudRepository;
    }

    public DocumentRelationship create(final String parentId, final String docId) {
        return this.documentChildCrudRepository.save(new DocumentRelationship(parentId, docId));
    }
}
