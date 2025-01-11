package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewComment {
    private String documentId;
    private String body;
    private String creator;
    private String parentCommentId;
}
