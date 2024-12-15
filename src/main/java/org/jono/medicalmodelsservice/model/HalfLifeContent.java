package org.jono.medicalmodelsservice.model;

import lombok.Getter;

public class HalfLifeContent implements Content {
    private String type;
    @Getter
    private String drug;
    @Getter
    private int dosage;

    @Override
    public String getType() {
        return type;
    }
}
