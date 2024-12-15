package org.jono.medicalmodelsservice.usecases;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentNode {
    private NavTreeDocInfo document;
    private List<DocumentNode> childDocs;

    public DocumentNode(NavTreeDocInfo document) {
        this.document = document;
        this.childDocs = new ArrayList<>();
    }
}
