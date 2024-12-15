package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class JoinReturnType {
    private String id;
    private String title;
    private DocumentState state;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String documentId;
    private String childId;
}
