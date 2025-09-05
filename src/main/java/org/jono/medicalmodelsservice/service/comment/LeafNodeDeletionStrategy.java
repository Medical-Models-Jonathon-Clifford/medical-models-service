package org.jono.medicalmodelsservice.service.comment;

import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;

@Slf4j
@RequiredArgsConstructor
class LeafNodeDeletionStrategy implements DeletionStrategy {

    private final CommentRelationshipRepository repository;
    private final String targetId;

    @Override
    public CommentsToDelete execute() {
        log.info("Applying LEAF deletion strategy for comment ID: {}", targetId);
        final CommentRelationship relationshipToParent = repository.findRelationshipToParent(targetId);
        return CommentsToDelete.builder()
                .commentIds(singletonList(targetId))
                .commentRelationshipIds(singletonList(relationshipToParent.getId()))
                .build();
    }
}
