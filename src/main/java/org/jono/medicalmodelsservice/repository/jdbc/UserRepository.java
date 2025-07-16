package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
  private final UserCrudRepository userCrudRepository;

  @Autowired
  public UserRepository(final UserCrudRepository userCrudRepository) {
    this.userCrudRepository = userCrudRepository;
  }

  public User create(final User user) {
    return this.userCrudRepository.save(user);
  }

  public Optional<User> findById(final String id) {
    return this.userCrudRepository.findById(id);
  }
}
