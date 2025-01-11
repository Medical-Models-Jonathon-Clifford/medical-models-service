package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class CommentChildRepository {
    private final R2dbcEntityTemplate template;

    @Autowired
    public CommentChildRepository(ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    public Mono<List<CommentChild>> findByCommentId(String commentId) {
        return template.select(CommentChild.class).matching(query(where("comment_id").is(commentId))).all().collectList();
    }

    public Flux<CommentChild> findCommentChildrenByCommentId(String commentId) {
        return template.select(CommentChild.class).matching(query(where("comment_id").is(commentId))).all().expand(commentId1 -> findCommentChildrenByCommentId(commentId1.getChildCommentId()));
    }

    public Mono<List<CommentChild>> findListByChildCommentId(String childCommentId) {
        return template.select(CommentChild.class).matching(query(where("child_comment_id").is(childCommentId))).all().collectList();
    }

    public Mono<CommentChild> findLeafNodesParentConnection(String childCommentId) {
        return template.select(CommentChild.class).matching(query(where("child_comment_id").is(childCommentId))).one();
    }

    public Mono<Long> deleteByIds(Collection<String> ids) {
        return template.delete(CommentChild.class).matching(query(where("id").in(ids))).all();
    }

    public Flux<CommentChild> findCommentChildrenByChildCommentId(String commentId) {
        return template.select(CommentChild.class).matching(query(where("child_comment_id").is(commentId))).all().expand(commentChild -> findCommentChildrenByChildCommentId(commentChild.getCommentId()));
    }
}
