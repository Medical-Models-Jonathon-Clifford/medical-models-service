package org.jono.medicalmodelsservice.usecases.comment;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentNode {
    private CommentNodeData comment;
    private List<CommentNode> childComments;

    public CommentNode(CommentNodeData comment) {
        this.comment = comment;
        this.childComments = new ArrayList<>();
    }
}
