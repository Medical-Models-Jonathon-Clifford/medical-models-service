package org.jono.medicalmodelsservice.model;

// TODO: Is Dto the right way to describe this class? Data Transfer Object?

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
