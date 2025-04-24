package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, String> {
}
