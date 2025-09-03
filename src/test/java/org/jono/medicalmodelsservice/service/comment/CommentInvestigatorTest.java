package org.jono.medicalmodelsservice.service.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.model.Tuple2;
import org.jono.medicalmodelsservice.repository.jdbc.CommentChildRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CommentInvestigatorTest {

    @Test
    void findNodesToDelete() {
        final CommentChildRepository commentChildRepository = Mockito.mock(CommentChildRepository.class);
        final var commentInvestigator = new CommentInvestigator(commentChildRepository);
        final CommentsToDelete commentsToDelete = commentInvestigator.findNodesToDelete("1", new Tuple2<>(
                List.of(new CommentRelationship("1", "11", "101")),
                List.of(new CommentRelationship("1", "11", "101"))));
        assertThat(commentsToDelete.commentIds).containsExactly("1");
    }
}