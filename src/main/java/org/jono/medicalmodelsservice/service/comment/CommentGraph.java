package org.jono.medicalmodelsservice.service.comment;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO: Deduplicate with DocumentGraph
@Slf4j
public class CommentGraph {
    @Getter
    private final List<CommentNode> topLevelComments;
    private final Map<String, CommentNode> allCommentNodes;
    private final Set<String> added;
    private final List<Comment> commentList;

    public CommentGraph(List<Comment> commentList, List<CommentChild> commentChildList) {
        log.info(commentList.toString());
        log.info(commentChildList.toString());

        this.topLevelComments = new ArrayList<>();
        this.allCommentNodes = new HashMap<>();
        this.added = new HashSet<>();
        this.commentList = commentList;
        Map<String, Comment> docMap = listToMap(commentList);
        for (CommentChild commentChild : commentChildList) {
            Comment parentComment = docMap.get(commentChild.getCommentId());
            Comment childComment = docMap.get(commentChild.getChildCommentId());
            if (Objects.nonNull(parentComment) && Objects.nonNull(childComment)) {
                added.add(parentComment.getId());
                added.add(childComment.getId());
                addDocumentNode(parentComment, childComment);
            } else {
                log.warn("There is a CommentChild with a parent or child not in the comment list. It will be ignored. commentId: {}, childCommentId: {}, parentComment: {}, childComment: {}",
                        commentChild.getCommentId(), commentChild.getChildCommentId(), parentComment, childComment);
            }
        }
        addRemainingDocuments();
    }

    private Map<String, Comment> listToMap(List<Comment> commentList) {
        Map<String, Comment> commentMap = new HashMap<>();
        for (Comment comment : commentList) {
            commentMap.put(comment.getId(), comment);
        }
        return commentMap;
    }

    private void addDocumentNode(Comment parentComment, Comment childComment) {
        if (allCommentNodes.containsKey(parentComment.getId())) {
            var childNode = new CommentNode(childComment);
            allCommentNodes.get(parentComment.getId()).getChildComments().add(childNode);
            allCommentNodes.put(childComment.getId(), childNode);
        } else {
            CommentNode parentNode = new CommentNode(parentComment);
            CommentNode childNode = new CommentNode(childComment);
            parentNode.getChildComments().add(childNode);
            allCommentNodes.put(parentComment.getId(), parentNode);
            allCommentNodes.put(childComment.getId(), childNode);
            topLevelComments.add(parentNode);
        }
    }

    private void addRemainingDocuments() {
        for (Comment comment : commentList) {
            if (!added.contains(comment.getId())) {
                CommentNode commentNode = new CommentNode(comment);
                topLevelComments.add(commentNode);
            }
        }
    }
}
