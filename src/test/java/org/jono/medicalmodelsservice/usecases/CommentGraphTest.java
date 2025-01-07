package org.jono.medicalmodelsservice.usecases;

import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

class CommentGraphTest {

    @Test
    public void noComments() {
        List<CommentNodeData> emptyCommentNodeDataList = Collections.emptyList();
        List<CommentChild> emptyCommentChildList = Collections.emptyList();
        CommentGraph commentGraph = new CommentGraph(emptyCommentNodeDataList, emptyCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();
        assertThat(actualCommentNodes.size(), is(0));
    }

    @Test
    public void oneCommentNoChildren() {
        List<CommentNodeData> emptyCommentNodeDataList = List.of(testCommentNodeData1());
        List<CommentChild> emptyCommentChildList = Collections.emptyList();
        CommentGraph commentGraph = new CommentGraph(emptyCommentNodeDataList, emptyCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();
        assertThat(actualCommentNodes.size(), is(1));
        assertThat(actualCommentNodes.getFirst().getComment().getId(), is("41"));
        assertThat(actualCommentNodes.getFirst().getComment().getBody(), is("Test comment 1"));
        assertThat(actualCommentNodes.getFirst().getComment().getDocumentId(), is("97"));
        assertThat(actualCommentNodes.getFirst().getChildComments().size(), is(0));
    }

    @Test
    public void twoCommentsWithChildRelationship() {
        List<CommentNodeData> emptyCommentNodeDataList = Arrays.asList(testCommentNodeData1(), testCommentNodeData2());
        List<CommentChild> emptyCommentChildList = List.of(new CommentChild("97", "41", "42"));
        CommentGraph commentGraph = new CommentGraph(emptyCommentNodeDataList, emptyCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();
        assertThat(actualCommentNodes.size(), is(1));
        assertThat(actualCommentNodes.getFirst().getComment().getId(), is("41"));
        assertThat(actualCommentNodes.getFirst().getComment().getBody(), is("Test comment 1"));
        assertThat(actualCommentNodes.getFirst().getComment().getDocumentId(), is("97"));
        assertThat(actualCommentNodes.getFirst().getChildComments().size(), is(1));
        assertThat(actualCommentNodes.getFirst().getChildComments().getFirst().getComment().getId(), is("42"));
        assertThat(actualCommentNodes.getFirst().getChildComments().getFirst().getComment().getBody(), is("Test comment 2"));
        assertThat(actualCommentNodes.getFirst().getChildComments().getFirst().getComment().getDocumentId(), is("97"));
    }

    @Test
    public void chainOfComments() {
        List<CommentNodeData> commentNodeDataList = createCommentNodeDataList("41", "43", "46", "49");
        List<CommentChild> commentChildList = List.of(
                new CommentChild("97", "41", "43"),
                new CommentChild("97", "43", "46"),
                new CommentChild("97", "46", "49")
        );
        CommentGraph commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        CommentNodeData firstLevel = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41")).getComment();
        assertThat(firstLevel.getId(), is("41"));
        assertThat(firstLevel.getBody(), is("Test comment 41"));
        assertThat(firstLevel.getDocumentId(), is("97"));

        CommentNodeData secondLevel = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41", "43")).getComment();
        assertThat(secondLevel.getId(), is("43"));
        assertThat(secondLevel.getBody(), is("Test comment 43"));
        assertThat(secondLevel.getDocumentId(), is("97"));

        CommentNodeData thirdLevel = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46")).getComment();
        assertThat(thirdLevel.getId(), is("46"));
        assertThat(thirdLevel.getBody(), is("Test comment 46"));
        assertThat(thirdLevel.getDocumentId(), is("97"));

        CommentNodeData fourthLevel = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46", "49")).getComment();
        assertThat(fourthLevel.getId(), is("49"));
        assertThat(fourthLevel.getBody(), is("Test comment 49"));
        assertThat(fourthLevel.getDocumentId(), is("97"));
    }

    @Test
    public void treeOfComments() {
        List<CommentNodeData> commentNodeDataList = createCommentNodeDataList("43", "49", "50", "59", "66", "68");
        List<CommentChild> commentChildList = List.of(
                new CommentChild("97", "49", "50"),
                new CommentChild("97", "49", "59"),
                new CommentChild("97", "50", "66"),
                new CommentChild("97", "59", "68")
        );
        CommentGraph commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        CommentNodeData firstLevelFirstComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("43")).getComment();
        assertThat(firstLevelFirstComment.getId(), is("43"));
        assertThat(firstLevelFirstComment.getBody(), is("Test comment 43"));
        assertThat(firstLevelFirstComment.getDocumentId(), is("97"));

        CommentNodeData firstLevelSecond = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("49")).getComment();
        assertThat(firstLevelSecond.getId(), is("49"));
        assertThat(firstLevelSecond.getBody(), is("Test comment 49"));
        assertThat(firstLevelSecond.getDocumentId(), is("97"));

        CommentNodeData secondLevelFirstComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("49", "50")).getComment();
        assertThat(secondLevelFirstComment.getId(), is("50"));
        assertThat(secondLevelFirstComment.getBody(), is("Test comment 50"));
        assertThat(secondLevelFirstComment.getDocumentId(), is("97"));

        CommentNodeData secondLevelSecondComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("49", "59")).getComment();
        assertThat(secondLevelSecondComment.getId(), is("59"));
        assertThat(secondLevelSecondComment.getBody(), is("Test comment 59"));
        assertThat(secondLevelSecondComment.getDocumentId(), is("97"));

        CommentNodeData thirdLevelFirstComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("49", "50", "66")).getComment();
        assertThat(thirdLevelFirstComment.getId(), is("66"));
        assertThat(thirdLevelFirstComment.getBody(), is("Test comment 66"));
        assertThat(thirdLevelFirstComment.getDocumentId(), is("97"));

        CommentNodeData thirdLevelSecondComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("49", "59", "68")).getComment();
        assertThat(thirdLevelSecondComment.getId(), is("68"));
        assertThat(thirdLevelSecondComment.getBody(), is("Test comment 68"));
        assertThat(thirdLevelSecondComment.getDocumentId(), is("97"));
    }

    @ParameterizedTest
    @MethodSource("provideChildCommentIds")
    public void gracefullyHandlesInvalidChildComments(String parentId, String childId) {
        List<CommentNodeData> commentNodeDataList = createCommentNodeDataList("41", "42", "43");
        List<CommentChild> commentChildList = List.of(
                new CommentChild("97", "41", "42"),
                new CommentChild("97", "42", "43"),
                new CommentChild("97", parentId, childId)
        );
        CommentGraph commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        assertThat(actualCommentNodes.size(), is(1));

        CommentNode firstLevelFirstComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41"));
        assertThat(firstLevelFirstComment.getChildComments().size(), is(1));
        CommentNodeData firstLevelFirstCommentData = firstLevelFirstComment.getComment();
        assertThat(firstLevelFirstCommentData.getId(), is("41"));
        assertThat(firstLevelFirstCommentData.getBody(), is("Test comment 41"));
        assertThat(firstLevelFirstCommentData.getDocumentId(), is("97"));

        CommentNode firstLevelSecond = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41", "42"));
        assertThat(firstLevelSecond.getChildComments().size(), is(1));
        CommentNodeData firstLevelSecondData = firstLevelSecond.getComment();
        assertThat(firstLevelSecondData.getId(), is("42"));
        assertThat(firstLevelSecondData.getBody(), is("Test comment 42"));
        assertThat(firstLevelSecondData.getDocumentId(), is("97"));

        CommentNodeData secondLevelFirstComment = getCommentNodeDataFromChildWithIdAtEachLevel(actualCommentNodes, List.of("41", "42", "43")).getComment();
        assertThat(secondLevelFirstComment.getId(), is("43"));
        assertThat(secondLevelFirstComment.getBody(), is("Test comment 43"));
        assertThat(secondLevelFirstComment.getDocumentId(), is("97"));
    }

    private static Stream<Arguments> provideChildCommentIds() {
        return Stream.of(
                Arguments.of("50", "51"),
                Arguments.of("41", "51"),
                Arguments.of("50", "43")
        );
    }

    private CommentNode getCommentNodeDataFromChildWithIdAtEachLevel(List<CommentNode> commentNodes, List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getCommentNodeDataWithId(commentNodes, levelIds.getFirst());
        } else {
            return getCommentNodeDataFromChildWithIdAtEachLevel(getCommentNodeDataWithId(commentNodes, levelIds.getFirst()).getChildComments(), levelIds.subList(1, levelIds.size()));
        }
    }

    private CommentNode getCommentNodeDataWithId(List<CommentNode> commentNodes, String id) {
        Optional<CommentNode> first = commentNodes.stream().filter(commentNode -> commentNode.getComment().getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            fail("The comment with ID \"" + id + "\" was not present at this level of the comment tree.");
        }
        return first.get();
    }

    private List<CommentNodeData> createCommentNodeDataList(String... commentIds) {
        List<CommentNodeData> commentNodeDataList = new ArrayList<>();
        String creatorString = "1";
        for (String commentId : commentIds) {
            commentNodeDataList.add(new CommentNodeData(Comment.builder()
                    .id(commentId)
                    .documentId("97")
                    .createdDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23))
                    .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 27))
                    .body("Test comment " + commentId)
                    .creator(creatorString)
                    .build()));
            creatorString = creatorString.equals("1") ? "2" : "1";
        }
        return commentNodeDataList;
    }

    private CommentNodeData testCommentNodeData1() {
        return new CommentNodeData(Comment.builder()
                .id("41")
                .documentId("97")
                .createdDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23))
                .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 27))
                .body("Test comment 1")
                .creator("1")
                .build());
    }


    private CommentNodeData testCommentNodeData2() {
        return new CommentNodeData(Comment.builder()
                .id("42")
                .documentId("97")
                .createdDate(LocalDateTime.of(2024, Month.AUGUST, 4, 5, 41))
                .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 4, 5, 52))
                .body("Test comment 2")
                .creator("2")
                .build());
    }

}