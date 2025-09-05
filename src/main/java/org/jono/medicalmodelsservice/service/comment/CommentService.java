package org.jono.medicalmodelsservice.service.comment;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRelationshipRepository commentRelationshipRepository;
    private final CommentInvestigator commentInvestigator;

    public Comment createComment(final NewComment newComment) {
        return commentRepository.create(newComment);
    }

    public List<CommentTree> getComments(final String documentId) {
        final List<Comment> comments = commentRepository.findAllByDocumentId(documentId);
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipRepository.findAllByDocumentId(documentId);
        return CommentForestBuilder.buildForest(comments, commentRelationships);
    }

    public Optional<Comment> updateComment(final String id, final EditCommentDto editCommentDto) {
        final Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.getBody());
        updateMap.put(SqlIdentifier.unquoted("modified_date"), LocalDateTime.now());
        return commentRepository.updateById(id, updateMap);
    }

    public void deleteComment(final String id) {
        final var commentsToDelete = commentInvestigator.findCommentsToDelete(id);
        commentRelationshipRepository.deleteAllById(commentsToDelete.commentRelationshipIds());
        commentRepository.deleteAllById(commentsToDelete.commentIds());
    }
}
