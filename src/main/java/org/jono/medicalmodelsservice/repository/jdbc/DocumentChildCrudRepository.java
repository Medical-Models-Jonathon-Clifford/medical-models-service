package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.springframework.data.repository.CrudRepository;

public interface DocumentChildCrudRepository extends CrudRepository<DocumentRelationship, String> {
}
