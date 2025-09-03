package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentChildRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChildRepository documentChildRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentChildRepository documentChildRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentChildRepository = documentChildRepository;
    }

    public Document createDocument(final Optional<String> parentId) {
        final Document document = Document.draftDocument();
        final Document newDoc = documentRepository.create(document);
        parentId.ifPresent(id -> documentChildRepository.create(id, newDoc.getId()));
        return newDoc;
    }

    public Optional<Document> readDocument(final String id) {
        return documentRepository.findById(id);
    }

    public Document updateDocument(final String id, final DocumentDto documentDto) {
        return documentRepository.updateById(id, documentDto);
    }

    public List<DocumentNode> getAllNavigation() {
        final Tuple2<List<DocumentRelationship>, List<Document>> docsAndDocChildren =
                documentRepository.getDocsAndDocChildren();
        return DocumentGraph.buildGraph(docsAndDocChildren.getT2(), docsAndDocChildren.getT1());
    }
}
