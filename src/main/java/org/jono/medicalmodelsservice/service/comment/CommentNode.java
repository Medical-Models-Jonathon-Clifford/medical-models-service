package org.jono.medicalmodelsservice.service.comment;

import lombok.Data;
import org.jono.medicalmodelsservice.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentNode {
  private Comment comment;
  private List<CommentNode> children;

  public CommentNode(final Comment comment) {
    this.comment = comment;
    this.children = new ArrayList<>();
  }
}
