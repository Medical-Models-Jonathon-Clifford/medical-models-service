package org.jono.medicalmodelsservice.service;

import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> createUser(User user) {
        return userRepository.create(user);
    }

    public Mono<User> getById(String id) {
        return userRepository.findById(id);
    }
}
