package org.jono.medicalmodelsservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jono.medicalmodelsservice.model.DocumentState;

@AllArgsConstructor
@Data
public class DocumentDto {
    private String title;
    private String body;
    private DocumentState state;
}
