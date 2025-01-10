package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DocumentDto {
    private String id;
    private String title;
    private String body;
    private DocumentState state;
}
