package org.jono.medicalmodelsservice.usecases.document;

import lombok.Getter;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DocumentGraph {
    @Getter
    private final List<DocumentNode> topLevelDocs;
    private final Map<String, DocumentNode> allDocumentNodes;
    private final Set<String> added;
    private final List<Document> documentList;

    public DocumentGraph(List<Document> documentList, List<DocumentChild> documentChildList) {
        System.out.println(documentList);
        System.out.println(documentChildList);

        this.topLevelDocs = new ArrayList<>();
        this.allDocumentNodes = new HashMap<>();
        this.added = new HashSet<>();
        this.documentList = documentList;
        Map<String, Document> docMap = listToMap(documentList);
        for (DocumentChild documentChild : documentChildList) {
            Document parentDoc = docMap.get(documentChild.getDocumentId());
            Document childDoc = docMap.get(documentChild.getChildId());
            if (Objects.nonNull(parentDoc) && Objects.nonNull(childDoc)) {
                added.add(parentDoc.getId());
                added.add(childDoc.getId());
                addDocumentNode(parentDoc, childDoc);
            }
        }
        addRemainingDocuments();
    }

    private Map<String, Document> listToMap(List<Document> documentList) {
        Map<String, Document> docMap = new HashMap<>();
        for (Document doc : documentList) {
            docMap.put(doc.getId(), doc);
        }
        return docMap;
    }

    private void addDocumentNode(Document parentDoc, Document childDoc) {
        if (allDocumentNodes.containsKey(parentDoc.getId())) {
            allDocumentNodes.get(parentDoc.getId()).getChildDocs().add(new DocumentNode(childDoc));
        } else {
            DocumentNode parentNode = new DocumentNode(parentDoc);
            DocumentNode childNode = new DocumentNode(childDoc);
            parentNode.getChildDocs().add(childNode);
            allDocumentNodes.put(parentDoc.getId(), parentNode);
            topLevelDocs.add(parentNode);
        }
    }

    private void addRemainingDocuments() {
        for (Document doc : documentList) {
            if (!added.contains(doc.getId())) {
                DocumentNode docNode = new DocumentNode(doc);
                topLevelDocs.add(docNode);
            }
        }
    }
}
