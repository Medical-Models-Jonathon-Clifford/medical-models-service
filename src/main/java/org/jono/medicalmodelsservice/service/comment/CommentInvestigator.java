package org.jono.medicalmodelsservice.service.comment;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.jono.medicalmodelsservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentInvestigator {

    private final CommentRelationshipRepository commentRelationshipRepository;

    @Autowired
    CommentInvestigator(final CommentRelationshipRepository commentRelationshipRepository) {
        this.commentRelationshipRepository = commentRelationshipRepository;
    }

    CommentsToDelete findCommentsToDelete(final String idToDelete,
            final List<CommentRelationship> commentRelationshipsByCommentId,
            final List<CommentRelationship> commentRelationshipsByChildCommentId) {
        return new NodeFinder(idToDelete, commentRelationshipsByCommentId,
                              commentRelationshipsByChildCommentId).findNodesToDelete();
    }

    @RequiredArgsConstructor
    class NodeFinder {
        private final String idToDelete;
        private final List<CommentRelationship> commentRelationshipsByCommentId;
        private final List<CommentRelationship> commentRelationshipsByChildCommentId;

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
            final List<CommentRelationship> commentRelationships = ListUtils.deduplicate(
                    commentRelationshipRepository.findCommentRelationshipsByCommentId(idToDelete));
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
                    .commentRelationshipIds(justIds(commentRelationshipData.getAllToDelete()))
                    .build();
        }

        private CommentsToDelete deleteLeafNode() {
            log.info("leaf node condition");
            final CommentRelationship commentRelationshipServlet =
                    commentRelationshipRepository.findLeafNodesParentConnection(
                            idToDelete);
            return CommentsToDelete.builder()
                    .commentIds(Collections.singletonList(idToDelete))
                    .commentRelationshipIds(Collections.singletonList(commentRelationshipServlet.getId()))
                    .build();
        }

        private CommentsToDelete deleteIsolatedNode() {
            log.info("isolated node condition");
            return CommentsToDelete.builder()
                    .commentIds(List.of(idToDelete))
                    .commentRelationshipIds(new ArrayList<>())
                    .build();
        }

        private CommentRelationshipData findCommentRelationshipsByParentCommentIdOrChildCommentId() {
            final List<CommentRelationship> commentRelationshipsByCommentId = ListUtils.deduplicate(
                    commentRelationshipRepository.findCommentRelationshipsByCommentId(idToDelete));
            final List<CommentRelationship> commentRelationshipsByChildCommentId = ListUtils.deduplicate(
                    commentRelationshipRepository.findCommentRelationshipsByChildCommentId(idToDelete));
            return CommentRelationshipData.builder()
                    .targetAndDescendants(commentRelationshipsByCommentId)
                    .ancestors(commentRelationshipsByChildCommentId)
                    .targetCommentId(idToDelete)
                    .build();
        }

        private List<String> justIds(final List<CommentRelationship> commentRelationshipList) {
            return commentRelationshipList.stream().map(CommentRelationship::getId).collect(toList());
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
            allCommentIds.add(idToDelete);
            return ListUtils.deduplicate(allCommentIds);
        }
    }

    @Builder
    private static class CommentRelationshipData {
        private List<CommentRelationship> ancestors;
        private List<CommentRelationship> targetAndDescendants;
        private String targetCommentId;

        public List<CommentRelationship> getAllToDelete() {
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
            return commentRelationship.getChildCommentId().equals(targetCommentId);
        }
    }
}
