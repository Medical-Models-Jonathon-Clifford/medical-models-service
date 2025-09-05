package org.jono.medicalmodelsservice.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.springframework.stereotype.Component;

/**
 * CommentInvestigator handles the deletion of comments while maintaining the integrity
 * of the comment tree structure. Comments can be in one of four positions:
 *
 * <p>1. Root node: Has children but no parents
 * 2. Internal node: Has both parents and children
 * 3. Leaf node: Has parents but no children
 * 4. Isolated node: Has neither parents nor children
 *
 * <p>Each position requires a different deletion strategy to maintain tree integrity.
 *
 * <p>Illustration of Root, Internal and Leaf node types.
 * [0] -> [1] -> [2] -> [3]
 * ^      ^      ^      ^
 * root   internal      leaf
 *
 * <p>Illustration of Isolated node type.
 * [0]
 * ^
 * isolated
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentInvestigator {

    private final CommentRelationshipRepository commentRelationshipRepository;

    CommentsToDelete findCommentsToDelete(final String targetId) {
        final var deletionStrategyFactory = new DeletionStrategyFactory(commentRelationshipRepository, targetId);
        return deletionStrategyFactory.findCommentsToDelete();
    }
}
