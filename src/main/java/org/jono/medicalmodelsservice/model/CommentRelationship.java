package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jono.medicalmodelsservice.service.NodeRelationship;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("comment_relationship")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRelationship implements NodeRelationship {
    @Id
    private String id;
    private String documentId;
    private String parentCommentId;
    private String childCommentId;

    public CommentRelationship(final String documentId, final String parentCommentId, final String childCommentId) {
        this.documentId = documentId;
        this.parentCommentId = parentCommentId;
        this.childCommentId = childCommentId;
    }

    @Override
    public String getParentId() {
        return parentCommentId;
    }

    @Override
    public String getChildId() {
        return childCommentId;
    }
}
