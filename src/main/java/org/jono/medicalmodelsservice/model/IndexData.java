package org.jono.medicalmodelsservice.model;

import lombok.Data;

@Data
public class IndexData {
    private String firstName;
    private String lastName;

    public IndexData(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("IndexData{first name='%s', last name='%s'}", firstName, lastName);
    }
}
