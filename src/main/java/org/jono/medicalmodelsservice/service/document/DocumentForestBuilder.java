package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.service.ForestBuilder;

public class DocumentForestBuilder {

    public static List<DocumentTree> buildForest(final List<Document> documentList,
            final List<DocumentRelationship> documentRelationshipList) {
        return ForestBuilder.buildForest(documentList, documentRelationshipList, DocumentTree::new,
                                         DocumentTree::getChildren);
    }
}
