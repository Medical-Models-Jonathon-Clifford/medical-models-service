package org.jono.medicalmodelsservice.service;

import java.util.Optional;
import org.jono.medicalmodelsservice.model.dto.ViewUserDetailsDto;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.jono.medicalmodelsservice.utils.DtoAdapters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<ViewUserDetailsDto> getUserDetailsById(final String id) {
        return userRepository.findById(id).map(DtoAdapters::userToViewDto);
    }
}
