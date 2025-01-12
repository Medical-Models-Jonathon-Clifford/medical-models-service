package org.jono.medicalmodelsservice.service.document;

import org.jono.medicalmodelsservice.model.Document;
import org.jono.medicalmodelsservice.model.DocumentChild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class DocumentGraphTest {

    @Test
    public void shouldReturnEmptyListWhenNoDocuments() {
        List<Document> emptyDocumentList = Collections.emptyList();
        List<DocumentChild> emptyDocumentChildList = Collections.emptyList();

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(emptyDocumentList, emptyDocumentChildList);

        assertThat(actualDocumentNodes.size()).isEqualTo(0);
    }

    @Test
    public void oneDocumentNoChildren() {
        List<Document> emptyDocumentList = createDocumentList("41");
        List<DocumentChild> emptyDocumentChildList = Collections.emptyList();

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(emptyDocumentList, emptyDocumentChildList);

        assertThat(actualDocumentNodes.size()).isEqualTo(1);
        assertThat(actualDocumentNodes.getFirst())
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        assertThat(actualDocumentNodes.getFirst().getChildren().size()).isEqualTo(0);
    }

    @Test
    public void twoDocumentsWithChildRelationship() {
        List<Document> documentList = createDocumentList("41", "42");
        var singleDocumentChildList = List.of(new DocumentChild("41", "42"));

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, singleDocumentChildList);

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
        List<Document> documentList = createDocumentList("41", "43", "46", "49");
        List<DocumentChild> documentChildList = List.of(
                new DocumentChild("41", "43"),
                new DocumentChild("43", "46"),
                new DocumentChild("46", "49")
        );

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentChildList);

        DocumentNode firstLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41"));
        assertThat(firstLevel)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        DocumentNode secondLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "43"));
        assertThat(secondLevel)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        DocumentNode thirdLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "43", "46"));
        assertThat(thirdLevel)
                .extracting("id", "title")
                .containsExactly("46", "Test document 46");
        DocumentNode fourthLevel = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "43", "46", "49"));
        assertThat(fourthLevel)
                .extracting("id", "title")
                .containsExactly("49", "Test document 49");
    }

    @Test
    public void treeOfDocuments() {
        List<Document> documentList = createDocumentList("43", "49", "50", "59", "66", "68");
        var documentChildList = List.of(
                new DocumentChild("49", "50"),
                new DocumentChild("49", "59"),
                new DocumentChild("50", "66"),
                new DocumentChild("59", "68")
        );

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentChildList);

        DocumentNode firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("43"));
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("43", "Test document 43");
        DocumentNode firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49"));
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("49", "Test document 49");
        DocumentNode secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49", "50"));
        assertThat(secondLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("50", "Test document 50");
        DocumentNode secondLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49", "59"));
        assertThat(secondLevelSecondDocument)
                .extracting("id", "title")
                .containsExactly("59", "Test document 59");
        DocumentNode thirdLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49", "50", "66"));
        assertThat(thirdLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("66", "Test document 66");
        DocumentNode thirdLevelSecondDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("49", "59", "68"));
        assertThat(thirdLevelSecondDocument)
                .extracting("id", "title")
                .containsExactly("68", "Test document 68");
    }

    @ParameterizedTest
    @MethodSource("provideDocumentChildIds")
    public void gracefullyHandlesInvalidDocumentChildren(String parentId, String childId) {
        List<Document> documentList = createDocumentList("41", "42", "43");
        var documentChildList = List.of(
                new DocumentChild("41", "42"),
                new DocumentChild("42", "43"),
                new DocumentChild(parentId, childId)
        );

        List<DocumentNode> actualDocumentNodes = DocumentGraph.buildGraph(documentList, documentChildList);

        assertThat(actualDocumentNodes.size()).isEqualTo(1);
        DocumentNode firstLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41"));
        assertThat(firstLevelFirstDocument.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelFirstDocument)
                .extracting("id", "title")
                .containsExactly("41", "Test document 41");
        DocumentNode firstLevelSecond = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "42"));
        assertThat(firstLevelSecond.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelSecond)
                .extracting("id", "title")
                .containsExactly("42", "Test document 42");
        DocumentNode secondLevelFirstDocument = getDocumentNodeWithIdAtEachLevel(actualDocumentNodes, List.of("41", "42", "43"));
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

    private DocumentNode getDocumentNodeWithIdAtEachLevel(List<DocumentNode> documentNodes, List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getDocumentNodeWithId(documentNodes, levelIds.getFirst());
        } else {
            return getDocumentNodeWithIdAtEachLevel(getDocumentNodeWithId(documentNodes, levelIds.getFirst()).getChildren(), levelIds.subList(1, levelIds.size()));
        }
    }

    private DocumentNode getDocumentNodeWithId(List<DocumentNode> documentNodes, String id) {
        Optional<DocumentNode> first = documentNodes.stream().filter(documentNode -> documentNode.getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            fail("The document with ID \"" + id + "\" was not present at this level of the document tree.");
        }
        return first.get();
    }

    private List<Document> createDocumentList(String... documentIds) {
        List<Document> documentList = new ArrayList<>();
        for (String documentId : documentIds) {
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