package org.jono.medicalmodelsservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.ViewUserDetailsDto;
import org.jono.medicalmodelsservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public User handleUserPost(@RequestBody final User user,
            final JwtAuthenticationToken authentication) {
        log.info("New user {} created by {}", user.toString(), authentication.getName());
        final List<String> roles = castToStringList(authentication.getToken().getClaims().get("roles"));
        if (!roles.contains("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Only users with ROLE_ADMIN can create users.");
        }
        return userService.createUser(user);
    }

    private List<String> castToStringList(final Object object) {
        List<String> roles = null;

        if (object instanceof List<?>) {
            roles = ((List<?>) object).stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .toList();
        }
        return roles;
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<User> handleUserGet(@PathVariable final String id) {
        return userService.getById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{id}/details",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<ViewUserDetailsDto> getUserDetails(@PathVariable final String id) {
        return userService.getUserDetailsById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
