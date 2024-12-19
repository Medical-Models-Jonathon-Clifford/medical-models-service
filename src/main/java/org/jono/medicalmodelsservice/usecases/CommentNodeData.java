package org.jono.medicalmodelsservice.usecases;

import lombok.Data;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.Document;

import java.time.LocalDateTime;

@Data
public class CommentNodeData {
    private String id;
    private String document_id;
    private String body;
    private String creator;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public CommentNodeData(Comment comment) {
        this.id = comment.getId();
        this.document_id = comment.getDocumentId();
        this.body = comment.getBody();
        this.creator = comment.getCreator();
        this.createdDate = comment.getCreatedDate();
        this.modifiedDate = comment.getModifiedDate();
    }
}
