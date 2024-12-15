package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class IntermediateType {
    private String id;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private DocumentState state;
    private List<String> childIds;
}
