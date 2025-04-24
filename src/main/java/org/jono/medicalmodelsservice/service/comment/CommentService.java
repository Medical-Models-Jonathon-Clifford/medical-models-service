package org.jono.medicalmodelsservice.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.repository.jdbc.CommentChildRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentChildRepository commentChildRepository;
    private final CommentInvestigator commentInvestigator;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          CommentChildRepository commentChildRepository,
                          CommentInvestigator commentInvestigator
    ) {
        this.commentRepository = commentRepository;
        this.commentChildRepository = commentChildRepository;
        this.commentInvestigator = commentInvestigator;
    }

    public Comment createComment(NewComment newComment) {
        return commentRepository.create(newComment);
    }

    public List<CommentNode> getComments(String documentId) {
        Tuple2<List<CommentChild>, List<Comment>> tuple = commentRepository.getById(documentId);
        return CommentGraph.buildGraph(tuple.getT2(), tuple.getT1());
    }

    public Optional<Comment> updateComment(String id, EditCommentDto editCommentDto) {
        Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.getBody());
        updateMap.put(SqlIdentifier.unquoted("modified_date"), LocalDateTime.now());
        return commentRepository.updateById(id, updateMap);
    }

    public void deleteComment(String id) {
        List<CommentChild> commentChildrenByCommentId = commentChildRepository.findByCommentId(id);
        List<CommentChild> commentChildrenByChildCommentId = commentChildRepository.findListByChildCommentId(id);
        Tuple2<List<CommentChild>, List<CommentChild>> servletTuple2 = new Tuple2<>(commentChildrenByCommentId, commentChildrenByChildCommentId);
        CommentsToDelete commentsToDeleteServlet = commentInvestigator.findNodesToDelete(id, servletTuple2);
        commentChildRepository.deleteByIds(commentsToDeleteServlet.getChildCommentIds());
        commentRepository.deleteByIds(commentsToDeleteServlet.getCommentIds());
    }
}
