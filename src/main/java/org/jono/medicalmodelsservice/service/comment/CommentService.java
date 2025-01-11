package org.jono.medicalmodelsservice.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.repository.CommentChildRepository;
import org.jono.medicalmodelsservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentChildRepository commentChildRepository;
    private final CommentInvestigator commentInvestigator;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          CommentChildRepository commentChildRepository,
                          CommentInvestigator commentInvestigator) {
        this.commentRepository = commentRepository;
        this.commentChildRepository = commentChildRepository;
        this.commentInvestigator = commentInvestigator;
    }

    public Mono<Comment> createComment(NewComment newComment) {
        return commentRepository.create(newComment);
    }

    public Mono<List<CommentNode>> getComments(String documentId) {
        return commentRepository
                .getById(documentId)
                .map(tuple ->
                        new CommentGraph(tuple.getT2(), tuple.getT1()).getTopLevelComments());
    }

    public Mono<Comment> updateComment(String id, EditCommentDto editCommentDto) {
        Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.getBody());
        updateMap.put(SqlIdentifier.unquoted("modified_date"), LocalDateTime.now());
        return commentRepository.updateById(id, updateMap);
    }

    public Mono<Long> deleteComment(String id) {
        Mono<Tuple2<List<CommentChild>, List<CommentChild>>> tuple2 = commentChildRepository.findByCommentId(id)
                .zipWith(commentChildRepository.findListByChildCommentId(id));

        return commentInvestigator.findNodesToDelete(id, tuple2)
                .flatMap(commentsToDelete ->
                        commentChildRepository.deleteByIds(commentsToDelete.getChildCommentIds())
                                .map(_ -> commentsToDelete.getCommentIds())
                )
                .flatMap(commentRepository::deleteByIds);
    }
}
