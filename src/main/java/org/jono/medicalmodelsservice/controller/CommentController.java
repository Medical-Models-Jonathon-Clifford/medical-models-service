package org.jono.medicalmodelsservice.controller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.EditCommentDto;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.usecases.CommentGraph;
import org.jono.medicalmodelsservice.usecases.CommentNodeData;
import org.jono.medicalmodelsservice.usecases.CommentNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Slf4j
@RestController
public class CommentController {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public CommentController(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @CrossOrigin
    @PostMapping(path = "/comment",
            produces = "application/json")
    @ResponseBody
    public Mono<Comment> handleCommentPost(@RequestBody NewComment newComment) {
        log.info("documentId");
        log.info(newComment.getDocumentId());
        log.info("parentCommentId");
        log.info(newComment.getParentCommentId());
        log.info("body");
        log.info(newComment.getBody());
        log.info("creator");
        log.info(newComment.getCreator());

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

    // TODO: Need to figure out a better pattern than using documentId and commentId in the same spot in different endpoints. Ideally it would be commentId. Can I put documentId in the request body then?
    @CrossOrigin
    @GetMapping(path = "/comments/{documentId}",
            produces = "application/json")
    @ResponseBody
    public Mono<List<CommentNode>> getCommentsForDocumentId(@PathVariable String documentId) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return template
                .select(CommentChild.class).matching(query(where("document_id").is(documentId)))
                .all()
                .collectList()
                .zipWith(template.select(Comment.class).matching(query(where("document_id").is(documentId))).all().map(CommentNodeData::new).collectList())
                .map(tuple -> new CommentGraph(tuple.getT2(), tuple.getT1()).getTopLevelComments());
    }

    @CrossOrigin
    @PutMapping(path = "/comments/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Comment> handleCommentsPut(@PathVariable String id,
                                           @RequestBody EditCommentDto editCommentDto) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.getBody());

        return template.update(Comment.class)
                .matching(query(where("id").is(id)))
                .apply(Update.from(updateMap))
                .then(
                        template.select(Comment.class)
                                .matching(query(where("id").is(id)))
                                .one()
                );
    }

    @CrossOrigin
    @DeleteMapping(path = "/comments/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Long> deleteComment(@PathVariable String id) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return findAllChildCommentIds(id).collectList().map(this::deduplicate)
                .flatMap(commentChildList -> {
                    log.info("commentChildList: {}", commentChildList);
                    return template.delete(CommentChild.class).matching(query(where("id").in(justIds(commentChildList)))).all().map(affectedRows -> {
                        log.info("affectedRows: {}", affectedRows);
                        return commentChildList;
                    });
                })
                .flatMap(commentChildList -> template.delete(Comment.class).matching(query(where("id").in(allCommentIds(commentChildList)))).all());
    }

    private Flux<CommentChild> findAllChildCommentIds(String commentId) {
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

        return template.select(CommentChild.class).matching(query(where("comment_id").is(commentId))).all().expand(commentId1 -> findAllChildCommentIds(commentId1.getChildCommentId()));
    }

    private List<CommentChild> deduplicate(List<CommentChild> withDuplicates) {
        Set<CommentChild> set = new HashSet<>(withDuplicates);
        return new ArrayList<>(set);
    }

    private List<String> justIds(List<CommentChild> commentChildList) {
        return commentChildList.stream().map(CommentChild::getId).collect(toList());
    }

    private List<String> allCommentIds(List<CommentChild> commentChildList) {
        List<String> parentCommentIds = commentChildList.stream().map(CommentChild::getCommentId).toList();
        List<String> childCommentIds = commentChildList.stream().map(CommentChild::getChildCommentId).toList();
        List<String> allCommentIds = new ArrayList<>(parentCommentIds);
        allCommentIds.addAll(childCommentIds);
        Set<String> set = new HashSet<>(allCommentIds);
        return new ArrayList<>(set);
    }
}
