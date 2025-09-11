package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyCrudRepository extends CrudRepository<Company, String> {
}
