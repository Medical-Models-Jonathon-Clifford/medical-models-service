package org.jono.medicalmodelsservice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jono.medicalmodelsservice.service.NodeData;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("document")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Document implements NodeData {

    @Id
    private String id;
    private String title;
    // TODO: Use a better DateTime that includes a TimeZone
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String body;
    private String creator;
    private DocumentState state;

    public static Document draftDocument() {
        return Document.builder().state(DocumentState.DRAFT).build();
    }
}
