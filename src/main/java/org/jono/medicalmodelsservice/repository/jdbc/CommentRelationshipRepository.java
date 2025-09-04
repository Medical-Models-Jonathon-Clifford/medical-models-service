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

    public CommentRelationship save(final CommentRelationship commentRelationship) {
        return this.commentRelationshipCrudRepository.save(commentRelationship);
    }

    public List<CommentRelationship> findAllByDocumentId(final String documentId) {
        return this.commentRelationshipCrudRepository.findAllByDocumentId(documentId);
    }

    public List<CommentRelationship> findByParentCommentId(final String parentCommentId) {
        return this.commentRelationshipCrudRepository.findAllByParentCommentId(parentCommentId);
    }

    public List<CommentRelationship> findByChildCommentId(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findAllByChildCommentId(childCommentId);
    }

    public List<CommentRelationship> findDescendantRelationships(final String parentCommentId) {
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipCrudRepository.findAllByParentCommentId(parentCommentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentRelationshipCrudRepository.findAllByParentCommentId(
                            commentRelationship.getChildCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }

    public CommentRelationship findLeafNodesParentConnection(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findFirstByChildCommentId(childCommentId);
    }

    public List<CommentRelationship> findAncestorRelationships(final String commentId) {
        final List<CommentRelationship> commentRelationships =
                this.commentRelationshipCrudRepository.findAllByChildCommentId(commentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentRelationshipCrudRepository.findAllByChildCommentId(
                            commentRelationship.getParentCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }

    public void deleteAllById(final Collection<String> ids) {
        this.commentRelationshipCrudRepository.deleteAllById(ids);
    }
}
