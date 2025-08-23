package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.Optional;
import org.jono.medicalmodelsservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
