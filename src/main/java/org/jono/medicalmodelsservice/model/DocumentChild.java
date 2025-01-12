package org.jono.medicalmodelsservice.model;

import lombok.Data;
import org.jono.medicalmodelsservice.service.NodeRelationship;
import org.springframework.data.annotation.Id;

@Data
public class DocumentChild implements NodeRelationship {
    @Id
    private String id;
    private String documentId;
    private String childId;

    public DocumentChild(String documentId, String childId) {
        this.documentId = documentId;
        this.childId = childId;
    }

    @Override
    public String getParentId() {
        return documentId;
    }

    @Override
    public String getChildId() {
        return childId;
    }
}
