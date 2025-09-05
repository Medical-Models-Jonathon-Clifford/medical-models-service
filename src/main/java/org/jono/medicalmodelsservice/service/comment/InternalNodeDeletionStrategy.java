package org.jono.medicalmodelsservice.service.comment;

import static org.jono.medicalmodelsservice.utils.CommentRelationshipUtils.collectAllCommentIds;
import static org.jono.medicalmodelsservice.utils.CommentRelationshipUtils.extractIds;
import static org.jono.medicalmodelsservice.utils.ListUtils.deduplicate;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;

@Slf4j
class InternalNodeDeletionStrategy implements DeletionStrategy {

    private final CommentRelationshipRepository repository;
    private final String targetId;

    InternalNodeDeletionStrategy(final CommentRelationshipRepository repository, final String targetId) {
        this.repository = repository;
        this.targetId = targetId;
    }

    @Override
    public CommentsToDelete execute() {
        log.info("Applying INTERNAL deletion strategy for comment ID: {}", targetId);
        return CommentsToDelete.builder()
                .commentIds(collectAllCommentIds(findSubtree(), targetId))
                .commentRelationshipIds(extractIds(collectRelationshipsToDelete()))
                .build();
    }

    public List<CommentRelationship> collectRelationshipsToDelete() {
        final List<CommentRelationship> all = findSubtree();
        getRelationshipToParent().ifPresent(all::add);
        return all;
    }

    private Optional<CommentRelationship> getRelationshipToParent() {
        return findAncestors()
                .stream()
                .filter(commentRelationship -> commentRelationship.getChildCommentId().equals(targetId))
                .findFirst();
    }

    private List<CommentRelationship> findAncestors() {
        return deduplicate(repository.findAncestorRelationships(targetId));
    }

    private List<CommentRelationship> findSubtree() {
        return deduplicate(repository.findSubtreeRelationships(targetId));
    }
}