package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentCrudRepository extends CrudRepository<Document, String> {
}
