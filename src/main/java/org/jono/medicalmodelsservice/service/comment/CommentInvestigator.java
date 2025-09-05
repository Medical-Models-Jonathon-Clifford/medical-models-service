package org.jono.medicalmodelsservice.service.comment;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jono.medicalmodelsservice.service.comment.CommentInvestigatorUtils.collectAllCommentIds;
import static org.jono.medicalmodelsservice.service.comment.CommentInvestigatorUtils.extractIds;
import static org.jono.medicalmodelsservice.utils.ListUtils.deduplicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentInvestigator {

    private final CommentRelationshipRepository commentRelationshipRepository;

    CommentsToDelete findCommentsToDelete(final String targetId) {
        return new CommentTreeTraversal(targetId).findNodesToDelete();
    }

    class CommentTreeTraversal {

        private final String targetId;
        private final List<CommentRelationship> commentRelationshipsByCommentId;
        private final List<CommentRelationship> commentRelationshipsByChildCommentId;

        CommentTreeTraversal(final String targetId) {
            this.targetId = targetId;
            this.commentRelationshipsByCommentId = commentRelationshipRepository.findByParentCommentId(targetId);
            this.commentRelationshipsByChildCommentId = commentRelationshipRepository.findByChildCommentId(targetId);
        }

        public CommentsToDelete findNodesToDelete() {
            if (this.isRootNode()) {
                return deleteRootNode();
            } else if (this.isInternalNode()) {
                return deleteInternalNode();
            } else if (this.isLeafNode()) {
                return deleteLeafNode();
            } else {
                return deleteIsolatedNode();
            }
        }

        private boolean isRootNode() {
            return !commentRelationshipsByCommentId.isEmpty() && commentRelationshipsByChildCommentId.isEmpty();
        }

        private boolean isInternalNode() {
            return !commentRelationshipsByCommentId.isEmpty() && !commentRelationshipsByChildCommentId.isEmpty();
        }

        private boolean isLeafNode() {
            return commentRelationshipsByCommentId.isEmpty() && !commentRelationshipsByChildCommentId.isEmpty();
        }

        private CommentsToDelete deleteRootNode() {
            log.info("root node condition");
            final List<CommentRelationship> commentRelationships = findSubtree();
            return CommentsToDelete.builder()
                    .commentIds(collectAllCommentIds(commentRelationships, targetId))
                    .commentRelationshipIds(extractIds(commentRelationships))
                    .build();
        }

        private CommentsToDelete deleteInternalNode() {
            log.info("internal node condition");
            final CommentTreeSection commentTreeSection =
                    collectRelatedComments();
            return CommentsToDelete.builder()
                    .commentIds(collectAllCommentIds(commentTreeSection.subtree, targetId))
                    .commentRelationshipIds(extractIds(commentTreeSection.getAllCommentRelationshipsToDelete()))
                    .build();
        }

        private CommentsToDelete deleteLeafNode() {
            log.info("leaf node condition");
            final CommentRelationship commentRelationship =
                    commentRelationshipRepository.findLeafNodesParentConnection(targetId);
            return CommentsToDelete.builder()
                    .commentIds(singletonList(targetId))
                    .commentRelationshipIds(singletonList(commentRelationship.getId()))
                    .build();
        }

        private CommentsToDelete deleteIsolatedNode() {
            log.info("isolated node condition");
            return CommentsToDelete.builder()
                    .commentIds(singletonList(targetId))
                    .commentRelationshipIds(emptyList())
                    .build();
        }

        private CommentTreeSection collectRelatedComments() {
            return CommentTreeSection.builder()
                    .subtree(findSubtree())
                    .ancestors(findAncestors())
                    .targetId(targetId)
                    .build();
        }

        private List<CommentRelationship> findSubtree() {
            return deduplicate(commentRelationshipRepository.findSubtreeRelationships(targetId));
        }

        private List<CommentRelationship> findAncestors() {
            return deduplicate(commentRelationshipRepository.findAncestorRelationships(targetId));
        }
    }

    @Builder
    private static class CommentTreeSection {
        private List<CommentRelationship> ancestors;
        private List<CommentRelationship> subtree;
        private String targetId;

        public List<CommentRelationship> getAllCommentRelationshipsToDelete() {
            final List<CommentRelationship> all = new ArrayList<>(subtree);
            connectionToParent().ifPresent(all::add);
            return all;
        }

        private Optional<CommentRelationship> connectionToParent() {
            final List<CommentRelationship> list = ancestors.stream()
                    .filter(commentRelationship -> commentRelationship.getChildCommentId().equals(targetId))
                    .toList();
            if (list.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(list.getFirst());
        }
    }
}
