package org.jono.medicalmodelsservice.service.comment;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.repository.jdbc.CommentChildRepository;
import org.jono.medicalmodelsservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class CommentInvestigator {

    private final CommentChildRepository commentChildRepository;

    @Autowired
    CommentInvestigator(final CommentChildRepository commentChildRepository) {
        this.commentChildRepository = commentChildRepository;
    }

    CommentsToDelete findNodesToDelete(final String id, final Tuple2<List<CommentRelationship>,
            List<CommentRelationship>> tuple2) {
        if (this.isRootNode(tuple2)) {
            return deleteRootNode(id);
        } else if (this.isInternalNode(tuple2)) {
            return deleteInternalNode(id);
        } else if (this.isLeafNode(tuple2)) {
            return deleteLeafNode(id);
        } else {
            return deleteIsolatedNode(id);
        }
    }

    private CommentsToDelete deleteIsolatedNode(final String id) {
        log.info("isolated node condition");
        return CommentsToDelete.builder()
                .childCommentIds(new ArrayList<>())
                .commentIds(List.of(id))
                .build();
    }

    private CommentsToDelete deleteRootNode(final String id) {
        log.info("root node condition");
        final List<CommentRelationship> commentRelationships = ListUtils.deduplicate(
                commentChildRepository.findCommentChildrenByCommentId(id));
        return CommentsToDelete.builder()
                .childCommentIds(justIds(commentRelationships))
                .commentIds(allCommentIds(commentRelationships, id))
                .build();
    }

    private CommentsToDelete deleteInternalNode(final String id) {
        log.info("internal node condition");
        final CommentChildData commentChildDataServlet = findCommentChildrenByCommentIdOrChildCommentId(id);
        return CommentsToDelete.builder()
                .childCommentIds(justIds(commentChildDataServlet.getAllToDelete()))
                .commentIds(allCommentIds(commentChildDataServlet.targetAndDescendants, id))
                .build();
    }

    private CommentsToDelete deleteLeafNode(final String id) {
        log.info("leaf node condition");
        final CommentRelationship commentRelationshipServlet = commentChildRepository.findLeafNodesParentConnection(id);
        return CommentsToDelete.builder()
                .childCommentIds(Collections.singletonList(commentRelationshipServlet.getId()))
                .commentIds(Collections.singletonList(id))
                .build();
    }

    private boolean isRootNode(final Tuple2<List<CommentRelationship>, List<CommentRelationship>> tuple) {
        return !tuple.getT1().isEmpty() && tuple.getT2().isEmpty();
    }

    private boolean isInternalNode(final Tuple2<List<CommentRelationship>, List<CommentRelationship>> tuple) {
        return !tuple.getT1().isEmpty() && !tuple.getT2().isEmpty();
    }

    private boolean isLeafNode(final Tuple2<List<CommentRelationship>, List<CommentRelationship>> tuple) {
        return tuple.getT1().isEmpty() && !tuple.getT2().isEmpty();
    }

    private CommentChildData findCommentChildrenByCommentIdOrChildCommentId(final String commentId) {
        final List<CommentRelationship> commentChildrenByCommentId = ListUtils.deduplicate(
                commentChildRepository.findCommentChildrenByCommentId(commentId));
        final List<CommentRelationship> commentChildrenByChildCommentId = ListUtils.deduplicate(
                commentChildRepository.findCommentChildrenByChildCommentId(commentId));
        return CommentChildData.builder()
                .targetAndDescendants(commentChildrenByCommentId)
                .ancestors(commentChildrenByChildCommentId)
                .targetCommentId(commentId)
                .build();
    }

    private List<String> justIds(final List<CommentRelationship> commentRelationshipList) {
        return commentRelationshipList.stream().map(CommentRelationship::getId).collect(toList());
    }

    private List<String> allCommentIds(final List<CommentRelationship> commentRelationshipList,
            final String targetCommentId) {
        final List<String> parentCommentIds = commentRelationshipList.stream().map(
                CommentRelationship::getParentCommentId).toList();
        final List<String> childCommentIds = commentRelationshipList.stream().map(
                CommentRelationship::getChildCommentId).toList();
        final List<String> allCommentIds = new ArrayList<>(parentCommentIds);
        allCommentIds.addAll(childCommentIds);
        allCommentIds.add(targetCommentId);
        return ListUtils.deduplicate(allCommentIds);
    }

    @Builder
    private static class CommentChildData {
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
