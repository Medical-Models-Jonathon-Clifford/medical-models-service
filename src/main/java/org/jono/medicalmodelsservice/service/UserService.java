package org.jono.medicalmodelsservice.service;

import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(final User user) {
    return userRepository.create(user);
  }

  public Optional<User> getById(final String id) {
    return userRepository.findById(id);
  }
}
