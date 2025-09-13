package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentCompanyRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentRelationshipRepository documentRelationshipRepository;
    private final DocumentCompanyRelationshipRepository documentCompanyRelationshipRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentRelationshipRepository documentRelationshipRepository,
            final DocumentCompanyRelationshipRepository documentCompanyRelationshipRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentRelationshipRepository = documentRelationshipRepository;
        this.documentCompanyRelationshipRepository = documentCompanyRelationshipRepository;
    }

    public Document createDocument(final Optional<String> parentId, final String companyId, final String userId) {
        final Document document = Document.draftDocument();
        document.setCreator(userId);
        final Document newDoc = documentRepository.create(document);
        parentId.ifPresent(id -> documentRelationshipRepository.create(id, newDoc.getId()));
        documentCompanyRelationshipRepository.create(newDoc.getId(), companyId);
        return newDoc;
    }

    public Optional<Document> readDocument(final String id) {
        return documentRepository.findById(id);
    }

    public Document updateDocument(final String id, final DocumentDto documentDto) {
        return documentRepository.updateById(id, documentDto);
    }

    public List<DocumentTree> getAllNavigation(final String companyId) {
        final Tuple2<List<DocumentRelationship>, List<Document>> docsAndDocChildren =
                documentRepository.getDocsAndDocRelationships(companyId);
        return DocumentForestBuilder.buildForest(docsAndDocChildren.getT2(), docsAndDocChildren.getT1());
    }
}
