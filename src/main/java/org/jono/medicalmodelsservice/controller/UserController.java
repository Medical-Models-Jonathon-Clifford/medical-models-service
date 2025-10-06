package org.jono.medicalmodelsservice.controller;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.dto.ViewUserDetailsDto;
import org.jono.medicalmodelsservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/{id}/details",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<ViewUserDetailsDto> getUserDetails(@PathVariable final String id) {
        return userService.getUserDetailsById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
