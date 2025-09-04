package org.jono.medicalmodelsservice.service.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.repository.jdbc.CommentRelationshipRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CommentInvestigatorTest {

    @Nested
    class RootNode {

        @Test
        void shouldDeleteRootNode() {
            final CommentRelationshipRepository commentChildRepository = Mockito.mock(
                    CommentRelationshipRepository.class);
            final var commentInvestigator = new CommentInvestigator(commentChildRepository);
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("1");
            assertThat(commentsToDelete.commentIds).containsExactly("1");
            assertThat(commentsToDelete.commentRelationshipIds).isEmpty();
        }
    }

    @Nested
    class InternalNode {

        @Test
        void shouldDeleteInternalNode() {
            final CommentRelationshipRepository commentChildRepository = Mockito.mock(
                    CommentRelationshipRepository.class);
            final var commentInvestigator = new CommentInvestigator(commentChildRepository);
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("1");
            assertThat(commentsToDelete.commentIds).containsExactly("1");
            assertThat(commentsToDelete.commentRelationshipIds).isEmpty();
        }
    }

    @Nested
    class LeafNode {

        @Test
        void shouldDeleteLeafNode() {
            final CommentRelationshipRepository commentChildRepository = Mockito.mock(
                    CommentRelationshipRepository.class);
            when(commentChildRepository.findLeafNodesParentConnection("1")).thenReturn(
                    new CommentRelationship("1", "1", "11", "101"));
            final var commentInvestigator = new CommentInvestigator(commentChildRepository);
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("1");
            // TODO: Fix this test.
            // assertThat(commentsToDelete.commentIds).containsExactly("1");
            // assertThat(commentsToDelete.commentRelationshipIds).containsExactly("1");
        }
    }

    @Nested
    class IsolatedNode {

        @Test
        void shouldDeleteIsolatedNode() {
            final CommentRelationshipRepository commentChildRepository = Mockito.mock(
                    CommentRelationshipRepository.class);
            when(commentChildRepository.findLeafNodesParentConnection("1")).thenReturn(
                    new CommentRelationship("1", "1", "11", "101"));
            final var commentInvestigator = new CommentInvestigator(commentChildRepository);
            final CommentsToDelete commentsToDelete = commentInvestigator.findCommentsToDelete("1");
            assertThat(commentsToDelete.commentIds).containsExactly("1");
            assertThat(commentsToDelete.commentRelationshipIds).isEmpty();
        }
    }
}
