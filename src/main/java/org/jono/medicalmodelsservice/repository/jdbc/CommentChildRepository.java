package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentChildRepository {
    private final CommentChildCrudRepository commentChildCrudRepository;

    @Autowired
    public CommentChildRepository(final CommentChildCrudRepository commentChildCrudRepository) {
        this.commentChildCrudRepository = commentChildCrudRepository;
    }

    public List<CommentRelationship> findByCommentId(final String commentId) {
        return this.commentChildCrudRepository.findAllByParentCommentId(commentId);
    }

    public List<CommentRelationship> findCommentChildrenByCommentId(final String commentId) {
        final List<CommentRelationship> commentRelationships = this.commentChildCrudRepository.findAllByParentCommentId(
                commentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentChildCrudRepository.findAllByParentCommentId(
                    commentRelationship.getChildCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }

    public List<CommentRelationship> findListByChildCommentId(final String childCommentId) {
        return this.commentChildCrudRepository.findAllByChildCommentId(childCommentId);
    }

    public CommentRelationship findLeafNodesParentConnection(final String childCommentId) {
        return this.commentChildCrudRepository.findFirstByChildCommentId(childCommentId);
    }

    public void deleteByIds(final Collection<String> ids) {
        this.commentChildCrudRepository.deleteAllById(ids);
    }

    public List<CommentRelationship> findCommentChildrenByChildCommentId(final String commentId) {
        final List<CommentRelationship> commentRelationships = this.commentChildCrudRepository.findAllByChildCommentId(
                commentId);
        final List<CommentRelationship> allCommentRelationships = new ArrayList<>(commentRelationships);
        for (final CommentRelationship commentRelationship : commentRelationships) {
            final List<CommentRelationship> nextCommentRelationships =
                    this.commentChildCrudRepository.findAllByChildCommentId(
                    commentRelationship.getParentCommentId());
            allCommentRelationships.addAll(nextCommentRelationships);
        }
        return allCommentRelationships;
    }
}
