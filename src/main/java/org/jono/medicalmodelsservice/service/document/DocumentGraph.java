package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.service.GraphBuilder;

public class DocumentGraph {

    public static List<DocumentNode> buildGraph(final List<Document> documentList,
            final List<DocumentRelationship> documentRelationshipList) {
        return GraphBuilder.buildGraph(documentList, documentRelationshipList, DocumentNode::new,
                                       DocumentNode::getChildren);
    }
}
