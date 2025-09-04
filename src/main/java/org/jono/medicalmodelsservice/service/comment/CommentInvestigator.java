package org.jono.medicalmodelsservice.service.comment;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentInvestigator {

    private final CommentRelationshipRepository commentRelationshipRepository;

    CommentsToDelete findCommentsToDelete(final String targetId) {
        return new NodeFinder(targetId).findNodesToDelete();
    }

    class NodeFinder {

        private final String targetId;
        private final List<CommentRelationship> commentRelationshipsByCommentId;
        private final List<CommentRelationship> commentRelationshipsByChildCommentId;

        NodeFinder(final String targetId) {
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
            final List<CommentRelationship> commentRelationships = deduplicate(
                    commentRelationshipRepository.findCommentRelationshipsByParentCommentId(targetId));
            return CommentsToDelete.builder()
                    .commentIds(allCommentIds(commentRelationships))
                    .commentRelationshipIds(justIds(commentRelationships))
                    .build();
        }

        private CommentsToDelete deleteInternalNode() {
            log.info("internal node condition");
            final CommentRelationshipData commentRelationshipData =
                    findCommentRelationshipsByParentCommentIdOrChildCommentId();
            return CommentsToDelete.builder()
                    .commentIds(allCommentIds(commentRelationshipData.targetAndDescendants))
                    .commentRelationshipIds(justIds(commentRelationshipData.getAllCommentRelationshipsToDelete()))
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

        private CommentRelationshipData findCommentRelationshipsByParentCommentIdOrChildCommentId() {
            return CommentRelationshipData.builder()
                    .targetAndDescendants(findTargetAndDescendants())
                    .ancestors(findAncestors())
                    .targetId(targetId)
                    .build();
        }

        private List<CommentRelationship> findTargetAndDescendants() {
            return deduplicate(commentRelationshipRepository.findCommentRelationshipsByParentCommentId(targetId));
        }

        private List<CommentRelationship> findAncestors() {
            return deduplicate(commentRelationshipRepository.findCommentRelationshipsByChildCommentId(targetId));
        }

        private List<String> justIds(final List<CommentRelationship> commentRelationshipList) {
            return commentRelationshipList.stream().map(CommentRelationship::getId).toList();
        }

        private List<String> allCommentIds(final List<CommentRelationship> commentRelationshipList) {
            final List<String> parentCommentIds = commentRelationshipList.stream()
                    .map(CommentRelationship::getParentCommentId)
                    .toList();
            final List<String> childCommentIds = commentRelationshipList.stream()
                    .map(CommentRelationship::getChildCommentId)
                    .toList();
            final List<String> allCommentIds = new ArrayList<>();
            allCommentIds.addAll(parentCommentIds);
            allCommentIds.addAll(childCommentIds);
            allCommentIds.add(targetId);
            return deduplicate(allCommentIds);
        }
    }

    @Builder
    private static class CommentRelationshipData {
        private List<CommentRelationship> ancestors;
        private List<CommentRelationship> targetAndDescendants;
        private String targetId;

        public List<CommentRelationship> getAllCommentRelationshipsToDelete() {
            final List<CommentRelationship> all = new ArrayList<>(targetAndDescendants);
            connectionToParent().ifPresent(all::add);
            return all;
        }

        private Optional<CommentRelationship> connectionToParent() {
            final List<CommentRelationship> list = ancestors.stream().filter(this::targetIsChild).toList();
            if (list.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(list.getFirst());
        }

        private boolean targetIsChild(final CommentRelationship commentRelationship) {
            return commentRelationship.getChildCommentId().equals(targetId);
        }
    }
}
