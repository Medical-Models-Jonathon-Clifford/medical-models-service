package org.jono.medicalmodelsservice.service.comment;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class IsolatedNodeDeletionStrategy implements DeletionStrategy {

    private final String targetId;

    @Override
    public CommentsToDelete execute() {
        log.info("Applying ISOLATED deletion strategy for comment ID: {}", targetId);
        return CommentsToDelete.builder()
                .commentIds(singletonList(targetId))
                .commentRelationshipIds(emptyList())
                .build();
    }
}