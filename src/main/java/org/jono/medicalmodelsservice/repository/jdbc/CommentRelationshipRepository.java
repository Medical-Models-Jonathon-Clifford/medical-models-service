package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentRelationshipRepository {
    private final CommentRelationshipCrudRepository commentRelationshipCrudRepository;

    @Autowired
    public CommentRelationshipRepository(final CommentRelationshipCrudRepository commentRelationshipCrudRepository) {
        this.commentRelationshipCrudRepository = commentRelationshipCrudRepository;
    }

    public List<CommentRelationship> findByCommentId(final String commentId) {
        return this.commentRelationshipCrudRepository.findAllByParentCommentId(commentId);
    }

    public List<CommentRelationship> findCommentRelationshipsByCommentId(final String commentId) {
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipCrudRepository.findAllByParentCommentId(
                commentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentRelationshipCrudRepository.findAllByParentCommentId(
                            commentRelationship.getChildCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }

    public List<CommentRelationship> findListByChildCommentId(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findAllByChildCommentId(childCommentId);
    }

    public CommentRelationship findLeafNodesParentConnection(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findFirstByChildCommentId(childCommentId);
    }

    public void deleteByIds(final Collection<String> ids) {
        this.commentRelationshipCrudRepository.deleteAllById(ids);
    }

    public List<CommentRelationship> findCommentRelationshipsByChildCommentId(final String commentId) {
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipCrudRepository.findAllByChildCommentId(
                commentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentRelationshipCrudRepository.findAllByChildCommentId(
                            commentRelationship.getParentCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }
}
