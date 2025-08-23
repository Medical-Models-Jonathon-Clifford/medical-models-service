package org.jono.medicalmodelsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories(
        basePackages = "org.jono.medicalmodelsservice.repository.jdbc"
)
public class MedicalModelsServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MedicalModelsServiceApplication.class, args);
    }

}
