package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.jono.medicalmodelsservice.service.NodeRelationship;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("comment_child")
@Data
public class CommentChild implements NodeRelationship {
    @Id
    private String id;
    private String documentId;
    private String commentId;
    private String childCommentId;

    public CommentChild(final String documentId, final String commentId, final String childCommentId) {
        this.documentId = documentId;
        this.commentId = commentId;
        this.childCommentId = childCommentId;
    }

    @Override
    public String getParentId() {
        return commentId;
    }

    @Override
    public String getChildId() {
        return childCommentId;
    }
}
