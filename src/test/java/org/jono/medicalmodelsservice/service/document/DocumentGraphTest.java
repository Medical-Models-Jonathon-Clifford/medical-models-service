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

class DocumentGraphTest {

    @Test
    public void shouldReturnEmptyListWhenNoDocuments() {
        final List<Document> emptyDocumentList = Collections.emptyList();
        final List<DocumentRelationship> emptyDocumentRelationshipList = Collections.emptyList();

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(emptyDocumentList,
                                                                                emptyDocumentRelationshipList);

        assertThat(actualDocumentNodes.size()).isEqualTo(0);
    }

    @Test
    public void oneDocumentNoChildren() {
        final List<Document> emptyDocumentList = createDocumentList("41");
        final List<DocumentRelationship> emptyDocumentRelationshipList = Collections.emptyList();

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(emptyDocumentList,
                                                                                emptyDocumentRelationshipList);

        assertThat(actualDocumentNodes.size()).isEqualTo(1);
        assertThat(actualDocumentNodes.getFirst())
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        assertThat(actualDocumentNodes.getFirst().getChildren().size()).isEqualTo(0);
    }

    @Test
    public void twoDocumentsWithChildRelationship() {
        final List<Document> documentList = createDocumentList("41", "42");
        final var singleDocumentChildList = List.of(new DocumentRelationship("41", "42"));

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, singleDocumentChildList);

        assertThat(actualDocumentNodes.size()).isEqualTo(1);
        assertThat(actualDocumentNodes.getFirst())
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        assertThat(actualDocumentNodes.getFirst().getChildren().size()).isEqualTo(1);
        assertThat(actualDocumentNodes.getFirst().getChildren().getFirst())
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

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentRelationshipList);

        final DocumentNode firstLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41"));
        assertThat(firstLevel)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        final DocumentNode secondLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "43"));
        assertThat(secondLevel)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        final DocumentNode thirdLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                         List.of("41", "43", "46"));
        assertThat(thirdLevel)
                .extracting("id", "title")
                .containsExactly("46", "Test document 46");
        final DocumentNode fourthLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "43", "46",
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

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentChildList);

        final DocumentNode firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                                      List.of("43"));
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        final DocumentNode firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49"));
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("49", "Test document 49");
        final DocumentNode secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                                       List.of("49", "50"));
        assertThat(secondLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("50", "Test document 50");
        final DocumentNode secondLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                                        List.of("49", "59"));
        assertThat(secondLevelSecondDocument)
                .extracting("id", "title")
                .containsExactly("59", "Test document 59");
        final DocumentNode thirdLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                                      List.of("49", "50", "66"));
        assertThat(thirdLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("66", "Test document 66");
        final DocumentNode thirdLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
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

        final List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentChildList);

        assertThat(actualDocumentNodes.size()).isEqualTo(1);
        final DocumentNode firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                                      List.of("41"));
        assertThat(firstLevelFirstDocument.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        final DocumentNode firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
                                                                               List.of("41", "42"));
        assertThat(firstLevelSecond.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("42", "Test document 42");
        final DocumentNode secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes,
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

    private DocumentNode getDocumentNodeWithIdAtEachLevel(final List<DocumentNode> documentNodes,
            final List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getDocumentNodeWithId(documentNodes, levelIds.getFirst());
        } else {
            return getDocumentNodeWithIdAtEachLevel(
                    getDocumentNodeWithId(documentNodes, levelIds.getFirst()).getChildren(),
                    levelIds.subList(1, levelIds.size()));
        }
    }

    private DocumentNode getDocumentNodeWithId(final List<DocumentNode> documentNodes, final String id) {
        final Optional<DocumentNode> first =
                documentNodes.stream().filter(documentNode -> documentNode.getId().equals(id)).findFirst();
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