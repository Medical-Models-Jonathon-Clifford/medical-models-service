package org.jono.medicalmodelsservice.service.document;

import lombok.Data;
import org.jono.medicalmodelsservice.model.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentNode {
    private String id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Document document;
    private List<DocumentNode> children;

    public DocumentNode(Document document) {
        this.id = document.getId();
        this.title = document.getTitle();
        this.createdDate = document.getCreatedDate();
        this.modifiedDate = document.getModifiedDate();
        this.document = document;
        this.children = new ArrayList<>();
    }
}
