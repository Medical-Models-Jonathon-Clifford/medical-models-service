package org.jono.medicalmodelsservice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jono.medicalmodelsservice.service.NodeData;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("comment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Comment implements NodeData {
    @Id
    private String id;
    private String documentId;
    private String creator;
    private String body;
    // TODO: Use a better DateTime that includes a TimeZone
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Comment(final NewComment newComment) {
        this.documentId = newComment.getDocumentId();
        this.body = newComment.getBody();
        this.creator = newComment.getCreator();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = this.createdDate;
    }
}
