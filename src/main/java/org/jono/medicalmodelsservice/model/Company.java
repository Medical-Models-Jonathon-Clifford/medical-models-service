package org.jono.medicalmodelsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("company")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Company {
    @Id
    private String id;
    private String name;
    private String logoFilename;
    private String locationState;
}
