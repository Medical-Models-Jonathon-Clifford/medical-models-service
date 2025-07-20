package org.jono.medicalmodelsservice.service.comment;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.service.GraphBuilder;

@Slf4j
public class CommentGraph {

  public static List<CommentNode> buildGraph(final List<Comment> commentList,
                                             final List<CommentChild> commentChildList) {
    return GraphBuilder.buildGraph(commentList, commentChildList, CommentNode::new, CommentNode::getChildren);
  }
}
