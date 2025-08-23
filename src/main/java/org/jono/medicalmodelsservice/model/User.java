package org.jono.medicalmodelsservice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user")
@AllArgsConstructor
@Data
public class User {
    @Id
    private String id;
    private String email;
    private String profilePicture;
    private String name;
    private LocalDateTime createdDate;
    private String password;
    private String state;
}
