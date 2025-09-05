package org.jono.medicalmodelsservice.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentRelationship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentForestBuilderTest {

    @Test
    public void shouldReturnEmptyListWhenNoDocuments() {
        final List<Document> emptyDocumentList = Collections.emptyList();
        final List<DocumentRelationship> emptyDocumentRelationshipList = Collections.emptyList();

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(emptyDocumentList,
                                                                                         emptyDocumentRelationshipList);

        assertThat(actualDocumentTrees.size()).isEqualTo(0);
    }

    @Test
    public void oneDocumentNoChildren() {
        final List<Document> emptyDocumentList = createDocumentList("41");
        final List<DocumentRelationship> emptyDocumentRelationshipList = Collections.emptyList();

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(emptyDocumentList,
                                                                                         emptyDocumentRelationshipList);

        assertThat(actualDocumentTrees.size()).isEqualTo(1);
        assertThat(actualDocumentTrees.getFirst())
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        assertThat(actualDocumentTrees.getFirst().getChildren().size()).isEqualTo(0);
    }

    @Test
    public void twoDocumentsWithChildRelationship() {
        final List<Document> documentList = createDocumentList("41", "42");
        final var singleDocumentChildList = List.of(new DocumentRelationship("41", "42"));

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(documentList,
                                                                                         singleDocumentChildList);

        assertThat(actualDocumentTrees.size()).isEqualTo(1);
        assertThat(actualDocumentTrees.getFirst())
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        assertThat(actualDocumentTrees.getFirst().getChildren().size()).isEqualTo(1);
        assertThat(actualDocumentTrees.getFirst().getChildren().getFirst())
                .extracting("id", "title")
                .containsExactly("42", "Test document 42");
    }

    @Test
    public void chainOfDocuments() {
        final List<Document> documentList = createDocumentList("41", "43", "46", "49");
        final List<DocumentRelationship> documentRelationshipList = List.of(
                new DocumentRelationship("41", "43"),
                new DocumentRelationship("43", "46"),
                new DocumentRelationship("46", "49")
        );

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(documentList,
                                                                                         documentRelationshipList);

        final DocumentTree firstLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees, List.of("41"));
        assertThat(firstLevel)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        final DocumentTree secondLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees, List.of("41", "43"));
        assertThat(secondLevel)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        final DocumentTree thirdLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                         List.of("41", "43", "46"));
        assertThat(thirdLevel)
                .extracting("id", "title")
                .containsExactly("46", "Test document 46");
        final DocumentTree fourthLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees, List.of("41", "43", "46",
                                                                                                       "49"));
        assertThat(fourthLevel)
                .extracting("id", "title")
                .containsExactly("49", "Test document 49");
    }

    @Test
    public void treeOfDocuments() {
        final List<Document> documentList = createDocumentList("43", "49", "50", "59", "66", "68");
        final var documentChildList = List.of(
                new DocumentRelationship("49", "50"),
                new DocumentRelationship("49", "59"),
                new DocumentRelationship("50", "66"),
                new DocumentRelationship("59", "68")
        );

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(documentList,
                                                                                         documentChildList);

        final DocumentTree firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                      List.of("43"));
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        final DocumentTree firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees, List.of("49"));
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("49", "Test document 49");
        final DocumentTree secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                       List.of("49", "50"));
        assertThat(secondLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("50", "Test document 50");
        final DocumentTree secondLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                        List.of("49", "59"));
        assertThat(secondLevelSecondDocument)
                .extracting("id", "title")
                .containsExactly("59", "Test document 59");
        final DocumentTree thirdLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                      List.of("49", "50", "66"));
        assertThat(thirdLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("66", "Test document 66");
        final DocumentTree thirdLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                       List.of("49", "59", "68"));
        assertThat(thirdLevelSecondDocument)
                .extracting("id", "title")
                .containsExactly("68", "Test document 68");
    }

    @ParameterizedTest
    @MethodSource("provideDocumentChildIds")
    public void gracefullyHandlesInvalidDocumentChildren(final String parentId, final String childId) {
        final List<Document> documentList = createDocumentList("41", "42", "43");
        final var documentChildList = List.of(
                new DocumentRelationship("41", "42"),
                new DocumentRelationship("42", "43"),
                new DocumentRelationship(parentId, childId)
        );

        final List<DocumentTree> actualDocumentTrees = DocumentForestBuilder.buildForest(documentList,
                                                                                         documentChildList);

        assertThat(actualDocumentTrees.size()).isEqualTo(1);
        final DocumentTree firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                      List.of("41"));
        assertThat(firstLevelFirstDocument.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        final DocumentTree firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                               List.of("41", "42"));
        assertThat(firstLevelSecond.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("42", "Test document 42");
        final DocumentTree secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentTrees,
                                                                                       List.of("41",
                                                                                               "42",
                                                                                               "43"));
        assertThat(secondLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
    }

    private static Stream<Arguments> provideDocumentChildIds() {
        return Stream.of(
                Arguments.of("50", "51"),
                Arguments.of("41", "51"),
                Arguments.of("50", "43")
        );
    }

    private DocumentTree getDocumentNodeWithIdAtEachLevel(final List<DocumentTree> documentTrees,
            final List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getDocumentNodeWithId(documentTrees, levelIds.getFirst());
        } else {
            return getDocumentNodeWithIdAtEachLevel(
                    getDocumentNodeWithId(documentTrees, levelIds.getFirst()).getChildren(),
                    levelIds.subList(1, levelIds.size()));
        }
    }

    private DocumentTree getDocumentNodeWithId(final List<DocumentTree> documentTrees, final String id) {
        final Optional<DocumentTree> first =
                documentTrees.stream().filter(documentNode -> documentNode.getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            fail("The document with ID \"" + id + "\" was not present at this level of the document tree.");
        }
        return first.get();
    }

    private List<Document> createDocumentList(final String... documentIds) {
        final List<Document> documentList = new ArrayList<>();
        for (final String documentId : documentIds) {
            documentList.add(Document.builder()
                                     .id(documentId)
                                     .createdDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23))
                                     .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 27))
                                     .title("Test document " + documentId)
                                     .build());
        }
        return documentList;
    }
}