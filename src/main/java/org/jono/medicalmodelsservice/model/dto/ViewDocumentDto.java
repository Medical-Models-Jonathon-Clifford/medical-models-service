package org.jono.medicalmodelsservice.model.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ViewDocumentDto(
        String id,
        String title,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate,
        String body,
        String creator,
        String creatorFullName
) {
}
