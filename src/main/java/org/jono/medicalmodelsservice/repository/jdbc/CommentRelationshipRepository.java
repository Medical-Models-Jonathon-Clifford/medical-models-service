package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public List<CommentRelationship> findOutgoingRelationships(final String parentCommentId) {
        return this.commentRelationshipCrudRepository.findAllByParentCommentId(parentCommentId);
    }

    public List<CommentRelationship> findIncomingRelationships(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findAllByChildCommentId(childCommentId);
    }

    public CommentRelationship findRelationshipToParent(final String childCommentId) {
        return this.commentRelationshipCrudRepository.findFirstByChildCommentId(childCommentId);
    }

    public List<CommentRelationship> findSubtreeRelationships(final String parentCommentId) {
        final Deque<String> parentQueue = new ArrayDeque<>();
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>();
        final Set<String> visited = new HashSet<>();

        parentQueue.offer(parentCommentId);

        while (!parentQueue.isEmpty()) {
            final String currentId = parentQueue.poll();
            final List<CommentRelationship> relationships =
                    this.commentRelationshipCrudRepository.findAllByParentCommentId(currentId);

            for (final CommentRelationship relationship : relationships) {
                if (!visited.contains(relationship.getChildCommentId())) {
                    visited.add(relationship.getChildCommentId());
                    parentQueue.offer(relationship.getChildCommentId());
                    allCommentRelationships.add(relationship);
                }
            }
        }

        return allCommentRelationships;
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
