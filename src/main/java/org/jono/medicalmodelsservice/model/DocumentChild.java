package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DocumentChild {
    @Id
    private String id;
    private String documentId;
    private String childId;
}
