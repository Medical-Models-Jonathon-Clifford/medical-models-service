package org.jono.medicalmodelsservice.service.comment;

import lombok.RequiredArgsConstructor;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;

@RequiredArgsConstructor
public class DeletionStrategyFactory {
    private final CommentRelationshipRepository repository;
    private final String targetId;

    CommentsToDelete findCommentsToDelete() {
        final NodeType nodeType = determineNodeType();
        final DeletionStrategy strategy = createStrategy(nodeType);
        return strategy.execute();
    }

    private NodeType determineNodeType() {
        final boolean hasChildren = !repository.findOutgoingRelationships(targetId).isEmpty();
        final boolean hasParents = !repository.findIncomingRelationships(targetId).isEmpty();
        if (hasChildren && hasParents) {
            return NodeType.INTERNAL;
        } else if (hasChildren) {
            return NodeType.ROOT;
        } else if (hasParents) {
            return NodeType.LEAF;
        }
        return NodeType.ISOLATED;
    }

    private DeletionStrategy createStrategy(final NodeType nodeType) {
        return switch (nodeType) {
            case ROOT -> new RootNodeDeletionStrategy(repository, targetId);
            case INTERNAL -> new InternalNodeDeletionStrategy(repository, targetId);
            case LEAF -> new LeafNodeDeletionStrategy(repository, targetId);
            case ISOLATED -> new IsolatedNodeDeletionStrategy(targetId);
        };
    }
}
