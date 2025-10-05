package org.jono.medicalmodelsservice.model;

public record NewComment(String documentId, String body, String creator, String parentCommentId) {
}
