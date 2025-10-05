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
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.CommentDto;
import org.jono.medicalmodelsservice.model.dto.CommentTreeDto;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.jono.medicalmodelsservice.utils.DtoAdapters;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRelationshipRepository commentRelationshipRepository;
    private final CommentInvestigator commentInvestigator;
    private final UserRepository userRepository;

    public Comment createComment(final NewComment newComment) {
        return commentRepository.create(newComment);
    }

    public List<CommentTreeDto> getComments(final String documentId) {
        final List<Comment> comments = commentRepository.findAllByDocumentId(documentId);
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipRepository.findAllByDocumentId(documentId);
        final List<CommentTree> commentTrees = CommentForestBuilder.buildForest(comments, commentRelationships);
        return commentTreeListToDtoList(commentTrees);
    }

    private List<CommentTreeDto> commentTreeListToDtoList(final List<CommentTree> commentTrees) {
        return commentTrees.stream().map(this::commentTreeToDto).toList();
    }

    private CommentTreeDto commentTreeToDto(final CommentTree commentTree) {
        if (commentTree.getChildren().isEmpty()) {
            return new CommentTreeDto(
                    commentToDto(commentTree.getComment()),
                    List.of()
            );
        }
        return new CommentTreeDto(
                commentToDto(commentTree.getComment()),
                commentTreeListToDtoList(commentTree.getChildren())
        );
    }

    private CommentDto commentToDto(final Comment comment) {
        final Optional<User> user = findUser(comment.getCreator());
        final String profilePicture = user
                .map(u -> String.format("/users/picture/%s.webp", u.getUsername()))
                .orElse(null);
        final String fullName = user
                .map(DtoAdapters::fullNameOfUser)
                .orElse(null);
        return CommentDto.builder()
                .id(comment.getId())
                .documentId(comment.getDocumentId())
                .creator(comment.getCreator())
                .body(comment.getBody())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .profilePicturePath(profilePicture)
                .fullName(fullName)
                .build();
    }

    private Optional<User> findUser(final String userId) {
        return userRepository.findById(userId);
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
