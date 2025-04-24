package org.jono.medicalmodelsservice.repository.jdbc;

import org.springframework.data.repository.CrudRepository;
import org.jono.medicalmodelsservice.model.Document;

public interface DocumentCrudRepository extends CrudRepository<Document, String> {
}
