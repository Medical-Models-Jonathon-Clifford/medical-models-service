package org.jono.medicalmodelsservice.service.comment;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.CommentRelationship;
import org.jono.medicalmodelsservice.service.ForestBuilder;

@Slf4j
public class CommentForestBuilder {

    private CommentForestBuilder() {
        // Utility class
    }

    public static List<CommentTree> buildForest(final List<Comment> commentList,
            final List<CommentRelationship> commentRelationshipList) {
        return ForestBuilder.buildForest(commentList, commentRelationshipList, CommentTree::new,
                                         CommentTree::getChildren);
    }
}
