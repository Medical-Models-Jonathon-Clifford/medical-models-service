package org.jono.medicalmodelsservice.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.jono.medicalmodelsservice.model.DocumentState;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.model.dto.DocumentDto;
import org.jono.medicalmodelsservice.repository.jdbc.DocumentRelationshipRepository;
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
    DocumentRelationshipRepository documentRelationshipRepository;

    @InjectMocks
    DocumentService documentService;

    @Nested
    class CreateDocument {

        @Test
        void shouldCreateChildRelationshipWhenParentIdSupplied() {
            when(documentRepository.create(any())).thenReturn(Document.builder().id("11").build());

            final Document document = documentService.createDocument(Optional.of("1"));

            assertThat(document).isNotNull();
            verify(documentRelationshipRepository).create("1", "11");
        }

        @Test
        void shouldNotCreateChildRelationshipWhenParentIdIsEmpty() {
            when(documentRepository.create(any())).thenReturn(Document.builder().id("11").build());

            final Document document = documentService.createDocument(Optional.empty());

            assertThat(document).isNotNull();
            verify(documentRelationshipRepository, times(0)).create(any(), any());
        }
    }

    @Nested
    class ReadDocument {

        @Test
        void shouldCallReadByIdOnDocRepo() {
            when(documentRepository.findById(any())).thenReturn(Optional.of(new Document()));

            final Optional<Document> document = documentService.readDocument("1");

            assertThat(document).isNotNull();
            verify(documentRepository).findById("1");
        }
    }

    @Nested
    class UpdateDocument {

        @Test
        void shouldCallUpdateByIdOnDocRepo() {
            when(documentRepository.updateById(any(), any())).thenReturn(new Document());

            final DocumentDto testDocumentDto = new DocumentDto("test title", "test body", DocumentState.ACTIVE);
            final Document document =
                    documentService.updateDocument("1", testDocumentDto);

            assertThat(document).isNotNull();
            verify(documentRepository).updateById("1", testDocumentDto);
        }
    }

    @Nested
    class GetAllNavigation {

        @Test
        void shouldCallGetAllOnDocRepo() {
            when(documentRepository.getDocsAndDocRelationships())
                    .thenReturn(
                            new Tuple2<>(
                                    List.of(new DocumentRelationship("1", "11")),
                                    List.of(Document.builder().id("1").build(), Document.builder().id("11").build())
                            )
                    );

            final List<DocumentTree> actual = documentService.getAllNavigation();

            assertThat(actual).isNotNull();
            assertThat(actual).hasSize(1);
            assertThat(actual.getFirst().getDocument().getId()).isEqualTo("1");
            assertThat(actual.getFirst().getChildren()).hasSize(1);
            assertThat(actual.getFirst().getChildren().getFirst().getDocument().getId()).isEqualTo("11");
        }
    }
}
