package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.List;
import org.jono.medicalmodelsservice.model.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyCrudRepository extends CrudRepository<Company, String> {
    List<Company> findByNameIsLikeIgnoreCase(String name);

    List<Company> findByLocationStateEqualsIgnoreCase(String state);

    List<Company> findByNameIsLikeIgnoreCaseAndLocationStateEqualsIgnoreCase(String state, String name);
}
