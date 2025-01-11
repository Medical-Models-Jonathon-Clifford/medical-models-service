package org.jono.medicalmodelsservice.service.comment;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.repository.CommentChildRepository;
import org.jono.medicalmodelsservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
class CommentInvestigator {

    private final CommentChildRepository commentChildRepository;

    @Autowired
    CommentInvestigator(CommentChildRepository commentChildRepository) {
        this.commentChildRepository = commentChildRepository;
    }

    Mono<CommentsToDelete> findNodesToDelete(String id, Mono<Tuple2<List<CommentChild>, List<CommentChild>>> tuple2) {
        return tuple2
                .filter(this::isRootNode).flatMap(_ -> deleteRootNode(id))
                .switchIfEmpty(tuple2.filter(this::isInternalNode).flatMap(_ -> deleteInternalNode(id)))
                .switchIfEmpty(tuple2.filter(this::isLeafNode).flatMap(_ -> deleteLeafNode(id)))
                .switchIfEmpty(tuple2.map(_ -> deleteIsolatedNode(id)));
    }

    private CommentsToDelete deleteIsolatedNode(String id) {
        log.info("isolated node condition");
        return CommentsToDelete.builder()
                .childCommentIds(new ArrayList<>())
                .commentIds(List.of(id))
                .build();
    }

    private Mono<CommentsToDelete> deleteRootNode(String id) {
        log.info("root node condition");
        return commentChildRepository.findCommentChildrenByCommentId(id).collectList().map(ListUtils::deduplicate)
                .map(commentChildList -> CommentsToDelete.builder()
                           .childCommentIds(justIds(commentChildList))
                        .commentIds(allCommentIds(commentChildList, id))
                        .build());
    }

    private Mono<CommentsToDelete> deleteInternalNode(String id) {
        log.info("internal node condition");
        return findCommentChildrenByCommentIdOrChildCommentId(id)
                .map(commentChildData -> CommentsToDelete.builder()
                        .childCommentIds(justIds(commentChildData.getAllToDelete()))
                        .commentIds(allCommentIds(commentChildData.targetAndDescendants, id))
                        .build()
                );
    }

    private Mono<CommentsToDelete> deleteLeafNode(String id) {
        log.info("leaf node condition");
        return commentChildRepository.findLeafNodesParentConnection(id)
                .map(CommentChild::getId)
                .map(id1 -> CommentsToDelete.builder()
                        .childCommentIds(Collections.singletonList(id1))
                        .commentIds(Collections.singletonList(id))
                        .build());
    }

    private boolean isRootNode(Tuple2<List<CommentChild>, List<CommentChild>> tuple) {
        return !tuple.getT1().isEmpty() && tuple.getT2().isEmpty();
    }

    private boolean isInternalNode(Tuple2<List<CommentChild>, List<CommentChild>> tuple) {
        return !tuple.getT1().isEmpty() && !tuple.getT2().isEmpty();
    }

    private boolean isLeafNode(Tuple2<List<CommentChild>, List<CommentChild>> tuple) {
        return tuple.getT1().isEmpty() && !tuple.getT2().isEmpty();
    }

    private Mono<CommentChildData> findCommentChildrenByCommentIdOrChildCommentId(String commentId) {
        Mono<List<CommentChild>> childrenByCommentId = commentChildRepository.findCommentChildrenByCommentId(commentId).collectList().map(ListUtils::deduplicate);
        Mono<List<CommentChild>> childrenByChildCommentId = commentChildRepository.findCommentChildrenByChildCommentId(commentId).collectList().map(ListUtils::deduplicate);

        return childrenByCommentId.zipWith(childrenByChildCommentId)
                .map(tuple -> CommentChildData.builder()
                        .targetAndDescendants(tuple.getT1())
                        .ancestors(tuple.getT2())
                        .targetCommentId(commentId)
                        .build());
    }

    private List<String> justIds(List<CommentChild> commentChildList) {
        return commentChildList.stream().map(CommentChild::getId).collect(toList());
    }

    private List<String> allCommentIds(List<CommentChild> commentChildList, String targetCommentId) {
        List<String> parentCommentIds = commentChildList.stream().map(CommentChild::getCommentId).toList();
        List<String> childCommentIds = commentChildList.stream().map(CommentChild::getChildCommentId).toList();
        List<String> allCommentIds = new ArrayList<>(parentCommentIds);
        allCommentIds.addAll(childCommentIds);
        allCommentIds.add(targetCommentId);
        return ListUtils.deduplicate(allCommentIds);
    }

    @Builder
    private static class CommentChildData {
        private List<CommentChild> ancestors;
        private List<CommentChild> targetAndDescendants;
        private String targetCommentId;

        public List<CommentChild> getAllToDelete() {
            List<CommentChild> all = new ArrayList<>(targetAndDescendants);
            all.add(connectionToParent());
            return all;
        }

        private CommentChild connectionToParent() {
            return ancestors.stream().filter(this::targetIsChild).toList().getFirst();
        }

        private boolean targetIsChild(CommentChild commentChild) {
            return commentChild.getChildCommentId().equals(targetCommentId);
        }
    }
}
