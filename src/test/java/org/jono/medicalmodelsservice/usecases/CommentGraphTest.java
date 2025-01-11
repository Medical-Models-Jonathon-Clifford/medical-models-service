package org.jono.medicalmodelsservice.usecases;

import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.jono.medicalmodelsservice.service.comment.CommentGraph;
import org.jono.medicalmodelsservice.service.comment.CommentNode;
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

class CommentGraphTest {

    @Test
    public void noComments() {
        List<Comment> emptyCommentNodeDataList = Collections.emptyList();
        List<CommentChild> emptyCommentChildList = Collections.emptyList();

        var commentGraph = new CommentGraph(emptyCommentNodeDataList, emptyCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        assertThat(actualCommentNodes.size()).isEqualTo(0);
    }

    @Test
    public void oneCommentNoChildren() {
        List<Comment> emptyCommentNodeDataList = createCommentNodeDataList("41");
        List<CommentChild> emptyCommentChildList = Collections.emptyList();

        var commentGraph = new CommentGraph(emptyCommentNodeDataList, emptyCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        assertThat(actualCommentNodes.getFirst().getChildComments().size()).isEqualTo(0);
    }

    @Test
    public void twoCommentsWithChildRelationship() {
        List<Comment> commentNodeDataList = createCommentNodeDataList("41", "42");
        var singleCommentChildList = List.of(new CommentChild("97", "41", "42"));

        var commentGraph = new CommentGraph(commentNodeDataList, singleCommentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        assertThat(actualCommentNodes.getFirst().getChildComments().size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getChildComments().getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("42", "Test comment 42", "97");
    }

    @Test
    public void chainOfComments() {
        List<Comment> commentNodeDataList = createCommentNodeDataList("41", "43", "46", "49");
        var commentChildList = List.of(
                new CommentChild("97", "41", "43"),
                new CommentChild("97", "43", "46"),
                new CommentChild("97", "46", "49")
        );

        var commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        Comment firstLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41")).getComment();
        assertThat(firstLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        Comment secondLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "43")).getComment();
        assertThat(secondLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
        Comment thirdLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46")).getComment();
        assertThat(thirdLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("46", "Test comment 46", "97");
        Comment fourthLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46", "49")).getComment();
        assertThat(fourthLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("49", "Test comment 49", "97");
    }

    @Test
    public void treeOfComments() {
        List<Comment> commentNodeDataList = createCommentNodeDataList("43", "49", "50", "59", "66", "68");
        var commentChildList = List.of(
                new CommentChild("97", "49", "50"),
                new CommentChild("97", "49", "59"),
                new CommentChild("97", "50", "66"),
                new CommentChild("97", "59", "68")
        );

        var commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        Comment firstLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("43")).getComment();
        assertThat(firstLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
        Comment firstLevelSecond = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49")).getComment();
        assertThat(firstLevelSecond)
                .extracting("id", "body", "documentId")
                .containsExactly("49", "Test comment 49", "97");
        Comment secondLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "50")).getComment();
        assertThat(secondLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("50", "Test comment 50", "97");
        Comment secondLevelSecondComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "59")).getComment();
        assertThat(secondLevelSecondComment)
                .extracting("id", "body", "documentId")
                .containsExactly("59", "Test comment 59", "97");
        Comment thirdLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "50", "66")).getComment();
        assertThat(thirdLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("66", "Test comment 66", "97");
        Comment thirdLevelSecondComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "59", "68")).getComment();
        assertThat(thirdLevelSecondComment)
                .extracting("id", "body", "documentId")
                .containsExactly("68", "Test comment 68", "97");
    }

    @ParameterizedTest
    @MethodSource("provideChildCommentIds")
    public void gracefullyHandlesInvalidChildComments(String parentId, String childId) {
        List<Comment> commentNodeDataList = createCommentNodeDataList("41", "42", "43");
        var commentChildList = List.of(
                new CommentChild("97", "41", "42"),
                new CommentChild("97", "42", "43"),
                new CommentChild("97", parentId, childId)
        );

        var commentGraph = new CommentGraph(commentNodeDataList, commentChildList);
        List<CommentNode> actualCommentNodes = commentGraph.getTopLevelComments();

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        CommentNode firstLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41"));
        assertThat(firstLevelFirstComment.getChildComments().size()).isEqualTo(1);
        assertThat(firstLevelFirstComment.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        CommentNode firstLevelSecond = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "42"));
        assertThat(firstLevelSecond.getChildComments().size()).isEqualTo(1);
        assertThat(firstLevelSecond.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("42", "Test comment 42", "97");
        CommentNode secondLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "42", "43"));
        assertThat(secondLevelFirstComment.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
    }

    private static Stream<Arguments> provideChildCommentIds() {
        return Stream.of(
                Arguments.of("50", "51"),
                Arguments.of("41", "51"),
                Arguments.of("50", "43")
        );
    }

    private CommentNode getCommentNodeWithIdAtEachLevel(List<CommentNode> commentNodes, List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getCommentNodeWithId(commentNodes, levelIds.getFirst());
        } else {
            return getCommentNodeWithIdAtEachLevel(getCommentNodeWithId(commentNodes, levelIds.getFirst()).getChildComments(), levelIds.subList(1, levelIds.size()));
        }
    }

    private CommentNode getCommentNodeWithId(List<CommentNode> commentNodes, String id) {
        Optional<CommentNode> first = commentNodes.stream().filter(commentNode -> commentNode.getComment().getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            fail("The comment with ID \"" + id + "\" was not present at this level of the comment tree.");
        }
        return first.get();
    }

    private List<Comment> createCommentNodeDataList(String... commentIds) {
        List<Comment> commentNodeDataList = new ArrayList<>();
        for (String commentId : commentIds) {
            commentNodeDataList.add(Comment.builder()
                    .id(commentId)
                    .documentId("97")
                    .createdDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23))
                    .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 27))
                    .body("Test comment " + commentId)
                    .creator("1")
                    .build());
        }
        return commentNodeDataList;
    }
}