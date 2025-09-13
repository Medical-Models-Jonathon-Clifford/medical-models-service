package org.jono.medicalmodelsservice.model.dto;

import java.util.List;

public record CommentTreeDto(
        CommentDto comment,
        List<CommentTreeDto> children
) {}
