package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepository {
    private final R2dbcEntityTemplate template;

    @Autowired
    public UserRepository(ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    public Mono<User> create(User user) {
        return template.insert(User.class)
                .using(user);
    }

    public Mono<User> findById(String id) {
        return template.select(User.class)
                .matching(Query.query(Criteria.where("id").is(id)))
                .one();
    }
}
