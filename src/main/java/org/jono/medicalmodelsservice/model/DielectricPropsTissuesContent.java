package org.jono.medicalmodelsservice.model;

import lombok.Getter;

public class DielectricPropsTissuesContent implements Content {
    private String type;
    @Getter
    private String tissue;

    @Override
    public String getType() {
        return type;
    }
}
