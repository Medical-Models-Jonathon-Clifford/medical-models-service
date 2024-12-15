package org.jono.medicalmodelsservice.usecases;

import lombok.Data;
import org.jono.medicalmodelsservice.model.Document;

import java.time.LocalDateTime;

@Data
public class NavTreeDocInfo {
    private String id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public NavTreeDocInfo(Document document) {
        this.id = document.getId();
        this.title = document.getTitle();
        this.createdDate = document.getCreatedDate();
        this.modifiedDate = document.getModifiedDate();
    }
}
