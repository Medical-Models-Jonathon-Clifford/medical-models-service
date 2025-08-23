package org.jono.medicalmodelsservice.service.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CommentGraphTest {

    @Test
    public void noComments() {
        final List<Comment> emptyCommentList = Collections.emptyList();
        final List<CommentChild> emptyCommentChildList = Collections.emptyList();

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(emptyCommentList, emptyCommentChildList);

        assertThat(actualCommentNodes.size()).isEqualTo(0);
    }

    @Test
    public void oneCommentNoChildren() {
        final List<Comment> oneCommentList = createCommentList("41");
        final List<CommentChild> emptyCommentChildList = Collections.emptyList();

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(oneCommentList, emptyCommentChildList);

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        assertThat(actualCommentNodes.getFirst().getChildren().size()).isEqualTo(0);
    }

    @Test
    public void twoCommentsWithChildRelationship() {
        final List<Comment> commentList = createCommentList("41", "42");
        final var singleCommentChildList =
                List.of(new CommentChild("97", "41", "42"));

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(commentList, singleCommentChildList);

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        assertThat(actualCommentNodes.getFirst().getChildren().size()).isEqualTo(1);
        assertThat(actualCommentNodes.getFirst().getChildren().getFirst().getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("42", "Test comment 42", "97");
    }

    @Test
    public void chainOfComments() {
        final List<Comment> commentList = createCommentList("41", "43", "46", "49");
        final var commentChildList = List.of(
                new CommentChild("97", "41", "43"),
                new CommentChild("97", "43", "46"),
                new CommentChild("97", "46", "49")
        );

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(commentList, commentChildList);

        final Comment firstLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41")).getComment();
        assertThat(firstLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        final Comment secondLevel = getCommentNodeWithIdAtEachLevel(actualCommentNodes,
                                                                    List.of("41", "43")).getComment();
        assertThat(secondLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
        final Comment thirdLevel =
                getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46")).getComment();
        assertThat(thirdLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("46", "Test comment 46", "97");
        final Comment fourthLevel =
                getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "43", "46", "49")).getComment();
        assertThat(fourthLevel)
                .extracting("id", "body", "documentId")
                .containsExactly("49", "Test comment 49", "97");
    }

    @Test
    public void treeOfComments() {
        final List<Comment> commentList = createCommentList("43", "49", "50", "59", "66", "68");
        final var commentChildList = List.of(
                new CommentChild("97", "49", "50"),
                new CommentChild("97", "49", "59"),
                new CommentChild("97", "50", "66"),
                new CommentChild("97", "59", "68")
        );

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(commentList, commentChildList);

        final Comment firstLevelFirstComment =
                getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("43")).getComment();
        assertThat(firstLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
        final Comment firstLevelSecond = getCommentNodeWithIdAtEachLevel(actualCommentNodes,
                                                                         List.of("49")).getComment();
        assertThat(firstLevelSecond)
                .extracting("id", "body", "documentId")
                .containsExactly("49", "Test comment 49", "97");
        final Comment secondLevelFirstComment =
                getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "50")).getComment();
        assertThat(secondLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("50", "Test comment 50", "97");
        final Comment secondLevelSecondComment =
                getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("49", "59")).getComment();
        assertThat(secondLevelSecondComment)
                .extracting("id", "body", "documentId")
                .containsExactly("59", "Test comment 59", "97");
        final Comment thirdLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes,
                                                                               List.of("49", "50", "66")).getComment();
        assertThat(thirdLevelFirstComment)
                .extracting("id", "body", "documentId")
                .containsExactly("66", "Test comment 66", "97");
        final Comment thirdLevelSecondComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes,
                                                                                List.of("49", "59", "68")).getComment();
        assertThat(thirdLevelSecondComment)
                .extracting("id", "body", "documentId")
                .containsExactly("68", "Test comment 68", "97");
    }

    @ParameterizedTest
    @MethodSource("provideCommentChildIds")
    public void gracefullyHandlesInvalidChildComments(final String parentId, final String childId) {
        final List<Comment> commentList = createCommentList("41", "42", "43");
        final var commentChildList = List.of(
                new CommentChild("97", "41", "42"),
                new CommentChild("97", "42", "43"),
                new CommentChild("97", parentId, childId)
        );

        final List<CommentNode> actualCommentNodes = CommentGraph.buildGraph(commentList, commentChildList);

        assertThat(actualCommentNodes.size()).isEqualTo(1);
        final CommentNode firstLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41"));
        assertThat(firstLevelFirstComment.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelFirstComment.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("41", "Test comment 41", "97");
        final CommentNode firstLevelSecond = getCommentNodeWithIdAtEachLevel(actualCommentNodes, List.of("41", "42"));
        assertThat(firstLevelSecond.getChildren().size()).isEqualTo(1);
        assertThat(firstLevelSecond.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("42", "Test comment 42", "97");
        final CommentNode secondLevelFirstComment = getCommentNodeWithIdAtEachLevel(actualCommentNodes,
                                                                                    List.of("41", "42", "43"));
        assertThat(secondLevelFirstComment.getComment())
                .extracting("id", "body", "documentId")
                .containsExactly("43", "Test comment 43", "97");
    }

    private static Stream<Arguments> provideCommentChildIds() {
        return Stream.of(
                Arguments.of("50", "51"),
                Arguments.of("41", "51"),
                Arguments.of("50", "43")
        );
    }

    private CommentNode getCommentNodeWithIdAtEachLevel(final List<CommentNode> commentNodes,
            final List<String> levelIds) {
        if (levelIds.size() == 1) {
            return getCommentNodeWithId(commentNodes, levelIds.getFirst());
        } else {
            return getCommentNodeWithIdAtEachLevel(
                    getCommentNodeWithId(commentNodes, levelIds.getFirst()).getChildren(),
                    levelIds.subList(1, levelIds.size()));
        }
    }

    private CommentNode getCommentNodeWithId(final List<CommentNode> commentNodes, final String id) {
        final Optional<CommentNode> first =
                commentNodes.stream().filter(commentNode -> commentNode.getComment().getId().equals(id)).findFirst();
        if (first.isEmpty()) {
            fail("The comment with ID \"" + id + "\" was not present at this level of the comment tree.");
        }
        return first.get();
    }

    private List<Comment> createCommentList(final String... commentIds) {
        final List<Comment> commentList = new ArrayList<>();
        for (final String commentId : commentIds) {
            commentList.add(Comment.builder()
                                    .id(commentId)
                                    .documentId("97")
                                    .createdDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 23))
                                    .modifiedDate(LocalDateTime.of(2024, Month.AUGUST, 3, 4, 27))
                                    .body("Test comment " + commentId)
                                    .creator("1")
                                    .build());
        }
        return commentList;
    }
}
