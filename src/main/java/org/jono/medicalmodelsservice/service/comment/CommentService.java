package org.jono.medicalmodelsservice.service.comment;

import static org.jono.medicalmodelsservice.utils.DtoAdapters.commentToDto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.CommentTreeDto;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;

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
        final var comments = commentRepository.findAllByDocumentId(documentId);
        final var commentRelationships = this.commentRelationshipRepository.findAllByDocumentId(documentId);
        final List<CommentTree> commentTrees = CommentForestBuilder.buildForest(comments, commentRelationships);
        return commentTreeListToDtoList(commentTrees);
    }

    record CommentTreeAndUser(CommentTree commentTree, Optional<User> user) {
    }

    private List<CommentTreeDto> commentTreeListToDtoList(final List<CommentTree> commentTrees) {
        return commentTrees.stream()
                .map(ct -> new CommentTreeAndUser(ct, findUser(ct.getComment().getCreator())))
                .map((CommentTreeAndUser commentTree) -> commentTreeToDto(commentTree.commentTree(),
                                                                          commentTree.user())).toList();
    }

    private CommentTreeDto commentTreeToDto(final CommentTree commentTree, final Optional<User> user) {
        if (commentTree.getChildren().isEmpty()) {
            return new CommentTreeDto(
                    commentToDto(commentTree.getComment(), user),
                    List.of()
            );
        }
        return new CommentTreeDto(
                commentToDto(commentTree.getComment(), user),
                commentTreeListToDtoList(commentTree.getChildren())
        );
    }

    private Optional<User> findUser(final String userId) {
        return userRepository.findById(userId);
    }

    public Optional<Comment> updateComment(final String id, final EditCommentDto editCommentDto) {
        final Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("body"), editCommentDto.body());
        updateMap.put(SqlIdentifier.unquoted("modified_date"), LocalDateTime.now());
        return commentRepository.updateById(id, updateMap);
    }

    public void deleteComment(final String id) {
        final var commentsToDelete = commentInvestigator.findCommentsToDelete(id);
        commentRelationshipRepository.deleteAllById(commentsToDelete.commentRelationshipIds());
        commentRepository.deleteAllById(commentsToDelete.commentIds());
    }
}
