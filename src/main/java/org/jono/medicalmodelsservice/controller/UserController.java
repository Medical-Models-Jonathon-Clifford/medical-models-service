package org.jono.medicalmodelsservice.controller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
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

    private final ConnectionFactory connectionFactory;

    @Autowired
    public UserController(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public Mono<User> handleUserPost(@RequestBody User user) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return template.insert(User.class)
                .using(user);
    }

    @GetMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<User> handleUserGet(@PathVariable String id) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.select(User.class)
                .matching(Query.query(Criteria.where("id").is(id)))
                .one();
    }
}
