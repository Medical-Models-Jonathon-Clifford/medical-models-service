package org.jono.medicalmodelsservice.service.comment;

import static org.jono.medicalmodelsservice.utils.CommentRelationshipUtils.collectAllCommentIds;
import static org.jono.medicalmodelsservice.utils.CommentRelationshipUtils.extractIds;
import static org.jono.medicalmodelsservice.utils.ListUtils.deduplicate;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;

@Slf4j
@RequiredArgsConstructor
class RootNodeDeletionStrategy implements DeletionStrategy {

    private final CommentRelationshipRepository repository;
    private final String targetId;

    @Override
    public CommentsToDelete execute() {
        log.info("Applying ROOT deletion strategy for comment ID: {}", targetId);
        final List<CommentRelationship> commentRelationships = findSubtree(targetId);
        return CommentsToDelete.builder()
                .commentIds(collectAllCommentIds(commentRelationships, targetId))
                .commentRelationshipIds(extractIds(commentRelationships))
                .build();
    }

    private List<CommentRelationship> findSubtree(final String targetId) {
        return deduplicate(repository.findSubtreeRelationships(targetId));
    }
}
