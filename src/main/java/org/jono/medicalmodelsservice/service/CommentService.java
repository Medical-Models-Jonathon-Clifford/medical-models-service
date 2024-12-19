package org.jono.medicalmodelsservice.service;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.repository.CommentChildRepository;
import org.jono.medicalmodelsservice.repository.CommentRepository;
import org.jono.medicalmodelsservice.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentChildRepository commentChildRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentChildRepository commentChildRepository) {
        this.commentRepository = commentRepository;
        this.commentChildRepository = commentChildRepository;
    }

    public Mono<Long> deleteComment(String id) {
        Mono<Tuple2<List<CommentChild>, List<CommentChild>>> tuple2 = commentChildRepository.findByCommentId(id)
                .zipWith(commentChildRepository.findListByChildCommentId(id));

        return tuple2
                .filter(this::isRootNode).flatMap(_ -> deleteRootNode(id))
                .switchIfEmpty(tuple2.filter(this::isInternalNode).flatMap(_ -> deleteInternalNode(id)))
                .switchIfEmpty(tuple2.filter(this::isLeafNode).flatMap(_ -> deleteLeafNode(id)))
                .switchIfEmpty(tuple2.flatMap(_ -> deleteNodeWithNoChildren(id)));
    }

    private Mono<Long> deleteRootNode(String id) {
        log.info("root node condition");
        return commentChildRepository.findCommentChildrenByCommentId(id).collectList().map(ListUtils::deduplicate)
                .flatMap(commentChildList -> commentChildRepository.deleteByIds(justIds(commentChildList)).map(_ -> commentChildList))
                .map(commentChildList -> {
                    return allCommentIds(commentChildList, id);
                })
                .flatMap(commentRepository::deleteByIds);
    }

    private Mono<Long> deleteInternalNode(String id) {
        log.info("internal node condition");
        return findCommentChildrenByCommentIdOrChildCommentId(id)
                .flatMap(commentChildData -> commentChildRepository.deleteByIds(justIds(commentChildData.getAllToDelete())).map(_ -> commentChildData))
                .map(commentChildData -> allCommentIds(commentChildData.targetAndDescendants, id))
                .flatMap(commentRepository::deleteByIds);
    }

    private Mono<Long> deleteLeafNode(String id) {
        log.info("leaf node condition");
        return commentChildRepository.findLeafNodesParentConnection(id)
                .map(CommentChild::getId)
                .flatMap(commentChildRepository::deleteById)
                .flatMap(_ -> commentRepository.deleteById(id));
    }

    private Mono<Long> deleteNodeWithNoChildren(String id) {
        log.info("no children condition");
        return commentRepository.deleteById(id);
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
