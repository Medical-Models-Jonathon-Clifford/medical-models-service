package org.jono.medicalmodelsservice.service.comment;

import lombok.Data;
import org.jono.medicalmodelsservice.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentNode {
    private Comment comment;
    private List<CommentNode> childComments;

    public CommentNode(Comment comment) {
        this.comment = comment;
        this.childComments = new ArrayList<>();
    }
}
