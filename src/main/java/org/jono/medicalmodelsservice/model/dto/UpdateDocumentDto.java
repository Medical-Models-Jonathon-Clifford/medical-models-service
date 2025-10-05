package org.jono.medicalmodelsservice.model.dto;

import org.jono.medicalmodelsservice.model.DocumentState;

public record UpdateDocumentDto(String title, String body, DocumentState state) {
}
