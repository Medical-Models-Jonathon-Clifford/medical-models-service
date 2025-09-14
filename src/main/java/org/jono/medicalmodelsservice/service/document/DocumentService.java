package org.jono.medicalmodelsservice.service.document;

import java.util.List;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.User;
import org.jono.medicalmodelsservice.model.dto.UpdateDocumentDto;
import org.jono.medicalmodelsservice.model.dto.ViewDocumentDto;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentCompanyRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRelationshipRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.jono.medicalmodelsservice.repository.jdbc.UserRepository;
import org.jono.medicalmodelsservice.utils.DtoAdapters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentRelationshipRepository documentRelationshipRepository;
    private final DocumentCompanyRelationshipRepository documentCompanyRelationshipRepository;
    private final UserRepository userRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentRelationshipRepository documentRelationshipRepository,
            final DocumentCompanyRelationshipRepository documentCompanyRelationshipRepository,
            final UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentRelationshipRepository = documentRelationshipRepository;
        this.documentCompanyRelationshipRepository = documentCompanyRelationshipRepository;
        this.userRepository = userRepository;
    }

    public Document createDocument(final Optional<String> parentId, final String companyId, final String userId) {
        final Document document = Document.draftDocument();
        document.setCreator(userId);
        final Document newDoc = documentRepository.create(document);
        parentId.ifPresent(id -> documentRelationshipRepository.create(id, newDoc.getId()));
        documentCompanyRelationshipRepository.create(newDoc.getId(), companyId);
        return newDoc;
    }

    public Optional<ViewDocumentDto> readDocument(final String id) {
        final Optional<Document> document = documentRepository.findById(id);
        return document.map(this::toViewDocumentDto);
    }

    private ViewDocumentDto toViewDocumentDto(final Document document) {
        final Optional<User> user = findUser(document.getCreator());
        final String fullName = user
                .map(DtoAdapters::fullNameOfUser)
                .orElse(null);
        return ViewDocumentDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .createdDate(document.getCreatedDate())
                .modifiedDate(document.getModifiedDate())
                .body(document.getBody())
                .creator(document.getCreator())
                .creatorFullName(fullName)
                .build();
    }

    private Optional<User> findUser(final String userId) {
        return userRepository.findById(userId);
    }

    public Document updateDocument(final String id, final UpdateDocumentDto updateDocumentDto) {
        return documentRepository.updateById(id, updateDocumentDto);
    }

    public List<DocumentTree> getAllNavigation(final String companyId) {
        final Tuple2<List<DocumentRelationship>, List<Document>> docsAndDocChildren =
                documentRepository.getDocsAndDocRelationships(companyId);
        return DocumentForestBuilder.buildForest(docsAndDocChildren.getT2(), docsAndDocChildren.getT1());
    }
}
