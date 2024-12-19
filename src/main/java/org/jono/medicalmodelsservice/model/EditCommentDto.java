package org.jono.medicalmodelsservice.model;

// TODO: Is Dto the right way to describe this class? Data Transfer Object?

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditCommentDto {
    private String body;
}
