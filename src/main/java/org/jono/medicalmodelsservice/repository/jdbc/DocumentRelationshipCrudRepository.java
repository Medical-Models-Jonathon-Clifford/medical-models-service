package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRelationshipCrudRepository extends CrudRepository<DocumentRelationship, String> {
}
