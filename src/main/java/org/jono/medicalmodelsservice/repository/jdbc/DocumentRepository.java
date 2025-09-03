package org.jono.medicalmodelsservice.repository.jdbc;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DocumentRepository {

    private final DocumentCrudRepository documentCrudRepository;
    private final DocumentRelationshipCrudRepository documentRelationshipCrudRepository;

    @Autowired
    public DocumentRepository(
            final DocumentCrudRepository documentCrudRepository,
            final DocumentRelationshipCrudRepository documentRelationshipCrudRepository
    ) {
        this.documentCrudRepository = documentCrudRepository;
        this.documentRelationshipCrudRepository = documentRelationshipCrudRepository;
    }

    public Document create(final Document document) {
        return documentCrudRepository.save(document);
    }

    public Optional<Document> findById(final String id) {
        return documentCrudRepository.findById(id);
    }

    public Document updateById(final String id, final DocumentDto documentDto) {
        final Document existingDocument = documentCrudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document with id " + id + " not found"));

        if (documentDto.getTitle() != null) {
            existingDocument.setTitle(documentDto.getTitle());
        }
        if (documentDto.getBody() != null) {
            existingDocument.setBody(documentDto.getBody());
        }
        if (documentDto.getState() != null) {
            existingDocument.setState(documentDto.getState());
        }
        return documentCrudRepository.save(existingDocument);
    }

    public Tuple2<List<DocumentRelationship>, List<Document>> getDocsAndDocRelationships() {
        final List<DocumentRelationship> documentRelationships = ImmutableList.copyOf(
                documentRelationshipCrudRepository.findAll());
        final List<Document> documents = ImmutableList.copyOf(documentCrudRepository.findAll());
        return new Tuple2<>(documentRelationships, documents);
    }
}
