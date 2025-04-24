package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jono.medicalmodelsservice.service.NodeData;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("document")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Document implements NodeData {
    private static final String INITIAL_STATE = "Draft";

    @Id
    private String id;
    private String title;
    // TODO: Use a better DateTime that includes a TimeZone
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String body;
    private String creator;
    private DocumentState state;

    public Document(NewDocument newDocument) {
        this.creator = newDocument.getCreatorId();
        this.state = DocumentState.DRAFT;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = this.createdDate;
    }

    public static Document draftDocument() {
        return Document.builder().state(DocumentState.DRAFT).build();
    }
}
