package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CommentChild {
    @Id
    private String id;
    private String documentId;
    private String commentId;
    private String childCommentId;

    public CommentChild(String documentId, String commentId, String childCommentId) {
        this.documentId = documentId;
        this.commentId = commentId;
        this.childCommentId = childCommentId;
    }
}
