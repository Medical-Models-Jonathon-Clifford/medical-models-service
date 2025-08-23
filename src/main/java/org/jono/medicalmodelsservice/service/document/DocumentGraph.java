package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.service.GraphBuilder;

public class DocumentGraph {

    public static List<DocumentNode> buildGraph(final List<Document> documentList,
            final List<DocumentChild> documentChildList) {
        return GraphBuilder.buildGraph(documentList, documentChildList, DocumentNode::new, DocumentNode::getChildren);
    }
}
