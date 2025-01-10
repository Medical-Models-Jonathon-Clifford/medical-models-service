package org.jono.medicalmodelsservice.repository;

import io.r2dbc.spi.ConnectionFactory;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.usecases.comment.CommentGraph;
import org.jono.medicalmodelsservice.usecases.comment.CommentNode;
import org.jono.medicalmodelsservice.usecases.comment.CommentNodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class CommentRepository {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public CommentRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Mono<Comment> create(NewComment newComment) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        Comment comment = new Comment(newComment);
        Mono<Comment> commentMono = template.insert(Comment.class).using(comment);
        Optional<String> parentCommentIdOptional = Optional.ofNullable(newComment.getParentCommentId());

        return parentCommentIdOptional
                .map(parentCommentId -> commentMono
                        .flatMap(comment1 -> {
                            CommentChild commentChildReal = new CommentChild(comment1.getDocumentId(), parentCommentId, comment1.getId());
                            return template.insert(CommentChild.class)
                                    .using(commentChildReal)
                                    .thenReturn(comment1);
                        }))
                .orElse(commentMono);
    }

    public Mono<Long> deleteById(String id) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.delete(Comment.class).matching(query(where("id").is(id))).all();
    }

    public Mono<Long> deleteByIds(Collection<String> ids) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return template.delete(Comment.class).matching(query(where("id").in(ids))).all();
    }

    public Mono<List<CommentNode>> getById(String documentId) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return template
                .select(CommentChild.class).matching(query(where("document_id").is(documentId)))
                .all()
                .collectList()
                .zipWith(template.select(Comment.class).matching(query(where("document_id").is(documentId))).all().map(CommentNodeData::new).collectList())
                .map(tuple -> new CommentGraph(tuple.getT2(), tuple.getT1()).getTopLevelComments());
    }

    public Mono<Comment> updateById(String id, Map<SqlIdentifier, Object> updateMap) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

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
