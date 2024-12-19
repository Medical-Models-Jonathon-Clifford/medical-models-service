package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class CommentRepository {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public CommentRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<Long> deleteById(String id) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.delete(Comment.class).matching(query(where("id").is(id))).all();
    }

    public Mono<Long> deleteByIds(Collection<String> ids) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.delete(Comment.class).matching(query(where("id").in(ids))).all();
    }
}
