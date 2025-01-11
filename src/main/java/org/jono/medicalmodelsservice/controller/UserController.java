package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public Mono<User> handleUserPost(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<User> handleUserGet(@PathVariable String id) {
        return userService.getById(id);
    }
}
