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
  public CommentService(final CommentRepository commentRepository,
                        final CommentChildRepository commentChildRepository,
                        final CommentInvestigator commentInvestigator
  ) {
    this.commentRepository = commentRepository;
    this.commentChildRepository = commentChildRepository;
    this.commentInvestigator = commentInvestigator;
  }

  public Comment createComment(final NewComment newComment) {
    return commentRepository.create(newComment);
  }

  public List<CommentNode> getComments(final String documentId) {
    final Tuple2<List<CommentChild>, List<Comment>> tuple = commentRepository.getById(documentId);
    return CommentGraph.buildGraph(tuple.getT2(), tuple.getT1());
  }

  public Optional<Comment> updateComment(final String id, final EditCommentDto editCommentDto) {
    final Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
    updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.getBody());
    updateMap.put(SqlIdentifier.unquoted("modified_date"), LocalDateTime.now());
    return commentRepository.updateById(id, updateMap);
  }

  public void deleteComment(final String id) {
    final List<CommentChild> commentChildrenByCommentId = commentChildRepository.findByCommentId(id);
    final List<CommentChild> commentChildrenByChildCommentId = commentChildRepository.findListByChildCommentId(id);
    final Tuple2<List<CommentChild>, List<CommentChild>> servletTuple2 = new Tuple2<>(commentChildrenByCommentId, commentChildrenByChildCommentId);
    final CommentsToDelete commentsToDeleteServlet = commentInvestigator.findNodesToDelete(id, servletTuple2);
    commentChildRepository.deleteByIds(commentsToDeleteServlet.getChildCommentIds());
    commentRepository.deleteByIds(commentsToDeleteServlet.getCommentIds());
  }
}
