package org.jono.medicalmodelsservice.service.document;

import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.service.GraphBuilder;

import java.util.List;

public class DocumentGraph {

    public static List<DocumentNode> buildGraph(List<Document> documentList,
                                                List<DocumentChild> documentChildList) {
        return GraphBuilder.buildGraph(documentList, documentChildList, DocumentNode::new, DocumentNode::getChildren);
    }
}
