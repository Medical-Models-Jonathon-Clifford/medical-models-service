package org.jono.medicalmodelsservice.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.service.GraphBuilder;

import java.util.List;

@Slf4j
public class CommentGraph {

    public static List<CommentNode> buildGraph(List<Comment> commentList,
                                               List<CommentChild> commentChildList) {
        return GraphBuilder.buildGraph(commentList, commentChildList, CommentNode::new, CommentNode::getChildren);
    }
}
