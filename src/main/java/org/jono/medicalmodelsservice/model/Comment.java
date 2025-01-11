package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Comment {
    @Id
    private String id;
    private String documentId;
    private String creator;
    private String body;
    // TODO: Use a better DateTime that includes a TimeZone
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Comment(NewComment newComment) {
        this.documentId = newComment.getDocumentId();
        this.body = newComment.getBody();
        this.creator = newComment.getCreator();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = this.createdDate;
    }
}
