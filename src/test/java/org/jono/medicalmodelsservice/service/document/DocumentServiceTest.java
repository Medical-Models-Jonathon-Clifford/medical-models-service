package org.jono.medicalmodelsservice.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentChildRepository;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @Mock
    DocumentChildRepository documentChildRepository;

    @InjectMocks
    DocumentService documentService;

    @Nested
    class CreateDocument {

        @Test
        void shouldCreateChildRelationshipWhenParentIdSupplied() {
            when(documentRepository.create(any())).thenReturn(Document.builder().id("11").build());

            final Document document = documentService.createDocument(Optional.of("1"));

            assertThat(document).isNotNull();
            verify(documentChildRepository).create("1", "11");
        }

        @Test
        void shouldNotCreateChildRelationshipWhenParentIdIsEmpty() {
            when(documentRepository.create(any())).thenReturn(Document.builder().id("11").build());

            final Document document = documentService.createDocument(Optional.empty());

            assertThat(document).isNotNull();
            verify(documentChildRepository, times(0)).create(any(), any());
        }
    }
}
