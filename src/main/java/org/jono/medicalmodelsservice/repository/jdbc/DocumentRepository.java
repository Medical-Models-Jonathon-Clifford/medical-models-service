package org.jono.medicalmodelsservice.repository.jdbc;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class DocumentRepository {

  private final DocumentCrudRepository documentCrudRepository;
  private final DocumentChildCrudRepository documentChildCrudRepository;

  @Autowired
  public DocumentRepository(
      DocumentCrudRepository documentCrudRepository,
      DocumentChildCrudRepository documentChildCrudRepository
  ) {
    this.documentCrudRepository = documentCrudRepository;
    this.documentChildCrudRepository = documentChildCrudRepository;
  }

  public Document create(final Document document) {
    return documentCrudRepository.save(document);
  }

  public Optional<Document> findById(final String id) {
    return documentCrudRepository.findById(id);
  }

  public Document updateById(final String id, final DocumentDto documentDto, final Map<SqlIdentifier, Object> updateMap) {
    // Fetch the existing document by ID
    final Document existingDocument = documentCrudRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Document with id " + id + " not found"));

    // Apply updates from documentDto or updateMap to the existingDocument
    // Assuming updateMap contains key-value pairs of fields to update
    updateMap.forEach((key, value) -> {
      // Reflectively set fields or use setter methods.
      // Example: Assuming `key` corresponds to existingDocument's fields
      if (Objects.isNull(value)) {
        log.info("Field in update map is null: {}", key);
        return;
      }
      switch (key.toString()) {
        case "title":
          existingDocument.setTitle(value.toString());
          break;
        case "body":
          existingDocument.setBody(value.toString());
          break;
        case "state":
          existingDocument.setState((DocumentState) value);
          break;
        // Add cases for other fields in the Document model
        default:
          log.info("Found another field: {}", key);
      }
    });

    // Update other standard fields from DocumentDto
    if (documentDto.getTitle() != null) {
      existingDocument.setTitle(documentDto.getTitle());
    }
    if (documentDto.getBody() != null) {
      existingDocument.setBody(documentDto.getBody());
    }
    if (documentDto.getState() != null) {
      existingDocument.setState(documentDto.getState());
    }

    // Save the updated document back to the repository
    return documentCrudRepository.save(existingDocument);
  }

  public Tuple2<List<DocumentChild>, List<Document>> getDocsAndDocChildren() {
    final List<DocumentChild> documentChildren = ImmutableList.copyOf(documentChildCrudRepository.findAll());
    final List<Document> documents = ImmutableList.copyOf(documentCrudRepository.findAll());
    return new Tuple2<>(documentChildren, documents);
  }
}
