package org.jono.medicalmodelsservice.model;

import lombok.Getter;

public class TextContent implements Content {
    private String type;
    @Getter
    private String text;

    @Override
    public String getType() {
        return this.type;
    }
}
