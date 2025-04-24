package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.DocumentChild;
import org.springframework.data.repository.CrudRepository;

public interface DocumentChildCrudRepository extends CrudRepository<DocumentChild, String> {
}
