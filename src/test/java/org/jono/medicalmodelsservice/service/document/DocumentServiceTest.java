package org.jono.medicalmodelsservice.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentChildRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @Mock
    DocumentChildRepository documentChildRepository;

    @Nested
    class CreateDocument {

        @Test
        void happyPath() {
            when(documentRepository.create(any())).thenReturn(new Document());
            final var documentService = new DocumentService(documentRepository, documentChildRepository);
            final Document document = documentService.createDocument(Optional.of("1"));
            assertThat(document).isNotNull();
        }
    }
}
