package org.jono.medicalmodelsservice.model.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDto(
        String id,
        String documentId,
        String creator,
        String body,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String profilePicturePath,
        String fullName
) {
}
