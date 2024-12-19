package org.jono.medicalmodelsservice.usecases;

import lombok.Getter;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.model.DocumentChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO: Deduplicate with DocumentGraph
public class CommentGraph {
    @Getter
    private final List<CommentNode> topLevelComments;
    private final Map<String, CommentNode> allCommentNodes;
    private final Set<String> added;
    private final List<CommentNodeData> commentList;

    public CommentGraph(List<CommentNodeData> commentList, List<CommentChild> commentChildList) {
        System.out.println(commentList);
        System.out.println(commentChildList);

        this.topLevelComments = new ArrayList<>();
        this.allCommentNodes = new HashMap<>();
        this.added = new HashSet<>();
        this.commentList = commentList;
        Map<String, CommentNodeData> docMap = listToMap(commentList);
        for (CommentChild commentChild : commentChildList) {
            CommentNodeData parentComment = docMap.get(commentChild.getCommentId());
            CommentNodeData childComment = docMap.get(commentChild.getChildCommentId());
            if (Objects.nonNull(parentComment) && Objects.nonNull(childComment)) {
                added.add(parentComment.getId());
                added.add(childComment.getId());
                addDocumentNode(parentComment, childComment);
            }
        }
        addRemainingDocuments();
    }

    private Map<String, CommentNodeData> listToMap(List<CommentNodeData> commentList) {
        Map<String, CommentNodeData> commentMap = new HashMap<>();
        for (CommentNodeData comment : commentList) {
            commentMap.put(comment.getId(), comment);
        }
        return commentMap;
    }

    private void addDocumentNode(CommentNodeData parentComment, CommentNodeData childComment) {
        if (allCommentNodes.containsKey(parentComment.getId())) {
            CommentNode childNode = new CommentNode(childComment);
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
        for (CommentNodeData comment : commentList) {
            if (!added.contains(comment.getId())) {
                CommentNode commentNode = new CommentNode(comment);
                topLevelComments.add(commentNode);
            }
        }
    }
}
