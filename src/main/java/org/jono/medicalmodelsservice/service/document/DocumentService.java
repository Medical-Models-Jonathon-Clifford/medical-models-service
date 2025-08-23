package org.jono.medicalmodelsservice.service.document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentChildRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
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

        return parentId
                .map(id -> addChildAndReturnDoc(id, newDoc))
                .orElse(newDoc);
    }

    private Document addChildAndReturnDoc(final String id, final Document newDoc) {
        documentChildRepository.create(id, newDoc.getId());
        return newDoc;
    }

    public Document updateDocument(final String id, final DocumentDto documentDto) {
        if (documentDto.getState().equals(DocumentState.DRAFT)) {
            if (Objects.nonNull(documentDto.getTitle()) && Objects.nonNull(documentDto.getBody())) {
                documentDto.setState(DocumentState.ACTIVE);
            } else if (Objects.nonNull(documentDto.getTitle())) {
                documentDto.setState(DocumentState.NO_CONTENT);
            } else if (Objects.nonNull(documentDto.getBody())) {
                documentDto.setState(DocumentState.NO_TITLE);
            } else {
                throw new RuntimeException("Title and body are both null. It is unclear what was updated.");
            }
        }

        final Map<SqlIdentifier, Object> updateMap = new LinkedHashMap<>();
        updateMap.put(SqlIdentifier.unquoted("title"), documentDto.getTitle());
        updateMap.put(SqlIdentifier.unquoted("body"), documentDto.getBody());
        updateMap.put(SqlIdentifier.unquoted("state"), documentDto.getState());

        return documentRepository.updateById(id, documentDto, updateMap);
    }

    public Optional<Document> getDocumentById(final String id) {
        return documentRepository.findById(id);
    }

    public List<DocumentNode> getAllNavigation() {
        final Tuple2<List<DocumentChild>, List<Document>> docsAndDocChildren =
                documentRepository.getDocsAndDocChildren();
        return DocumentGraph.buildGraph(docsAndDocChildren.getT2(), docsAndDocChildren.getT1());
    }
}
