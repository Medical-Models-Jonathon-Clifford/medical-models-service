package org.jono.medicalmodelsservice.service.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipCrudRepository;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CommentInvestigatorTest {

    private static final String DOC_ID = "1";

    private CommentRelationshipCrudRepository crudRepository;
    private CommentInvestigator commentInvestigator;

    @BeforeEach
    void setUp() {
        crudRepository = Mockito.mock(CommentRelationshipCrudRepository.class);
        final var repository = new CommentRelationshipRepository(crudRepository);
        commentInvestigator = new CommentInvestigator(repository);
    }

    @Nested
    class RootNode {

        @Test
        void shouldDeleteSingleRelationship() {
            when(crudRepository.findAllByChildCommentId("101")).thenReturn(Collections.emptyList());
            when(crudRepository.findAllByParentCommentId("101")).thenReturn(
                    List.of(new CommentRelationship("1", DOC_ID, "101", "102")));
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("101");
            assertThat(commentsToDelete.commentIds()).containsExactly("101", "102");
            assertThat(commentsToDelete.commentRelationshipIds()).containsExactly("1");
        }

        @Test
        void shouldDeleteTwoRelationships() {
            when(crudRepository.findAllByChildCommentId("101")).thenReturn(Collections.emptyList());
            when(crudRepository.findAllByParentCommentId("101")).thenReturn(
                    List.of(new CommentRelationship("1", DOC_ID, "101", "102")));
            when(crudRepository.findAllByParentCommentId("102")).thenReturn(
                    List.of(new CommentRelationship("2", DOC_ID, "102", "103")));
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("101");
            assertThat(commentsToDelete.commentIds()).containsExactly("101", "102", "103");
            assertThat(commentsToDelete.commentRelationshipIds()).containsExactlyInAnyOrder("1", "2");
        }
    }

    @Nested
    class InternalNode {

        @Test
        void shouldDeleteInternalNode() {
            when(crudRepository.findAllByChildCommentId("102")).thenReturn(
                    List.of(new CommentRelationship("2", DOC_ID, "101", "102")));
            when(crudRepository.findAllByParentCommentId("102")).thenReturn(
                    List.of(new CommentRelationship("1", DOC_ID, "102", "103")));
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("102");
            assertThat(commentsToDelete.commentIds()).containsExactlyInAnyOrder("102", "103");
            assertThat(commentsToDelete.commentRelationshipIds()).containsExactlyInAnyOrder("1", "2");
        }
    }

    @Nested
    class LeafNode {

        @Test
        void shouldDeleteLeafNode() {
            when(crudRepository.findAllByChildCommentId("102")).thenReturn(
                    List.of(new CommentRelationship("1", DOC_ID, "101", "102")));
            when(crudRepository.findAllByChildCommentId("103")).thenReturn(
                    List.of(new CommentRelationship("2", DOC_ID, "102", "103")));
            when(crudRepository.findFirstByChildCommentId("103")).thenReturn(
                    new CommentRelationship("2", DOC_ID, "102", "103"));
            when(crudRepository.findAllByParentCommentId("103")).thenReturn(Collections.emptyList());
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("103");
            assertThat(commentsToDelete.commentIds()).containsExactly("103");
            assertThat(commentsToDelete.commentRelationshipIds()).containsExactly("2");
        }
    }

    @Nested
    class IsolatedNode {

        @Test
        void shouldDeleteIsolatedNode() {
            when(crudRepository.findAllByChildCommentId("101")).thenReturn(Collections.emptyList());
            when(crudRepository.findAllByParentCommentId("101")).thenReturn(Collections.emptyList());
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("101");
            assertThat(commentsToDelete.commentIds()).containsExactly("101");
            assertThat(commentsToDelete.commentRelationshipIds()).isEmpty();
        }
    }
}
