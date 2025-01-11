package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.NewComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class CommentRepository {

    private final R2dbcEntityTemplate template;

    @Autowired
    public CommentRepository(ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    public Mono<Comment> create(NewComment newComment) {
        var comment = new Comment(newComment);
        Mono<Comment> commentMono = template.insert(Comment.class).using(comment);
        Optional<String> parentCommentIdOptional = Optional.ofNullable(newComment.getParentCommentId());

        return parentCommentIdOptional
                .map(parentCommentId -> commentMono
                        .flatMap(comment1 -> {
                            var commentChildReal = new CommentChild(comment1.getDocumentId(), parentCommentId, comment1.getId());
                            return template.insert(CommentChild.class)
                                    .using(commentChildReal)
                                    .thenReturn(comment1);
                        }))
                .orElse(commentMono);
    }

    public Mono<Long> deleteByIds(Collection<String> ids) {
        return template.delete(Comment.class).matching(query(where("id").in(ids))).all();
    }

    public Mono<Tuple2<List<CommentChild>, List<Comment>>> getById(String documentId) {
        return template
                .select(CommentChild.class).matching(query(where("document_id").is(documentId)))
                .all()
                .collectList()
                .zipWith(template.select(Comment.class).matching(query(where("document_id").is(documentId))).all().collectList());
    }

    public Mono<Comment> updateById(String id, Map<SqlIdentifier, Object> updateMap) {
        return template.update(Comment.class)
                .matching(query(where("id").is(id)))
                .apply(Update.from(updateMap))
                .then(
                        template.select(Comment.class)
                                .matching(query(where("id").is(id)))
                                .one()
                );
    }
}
