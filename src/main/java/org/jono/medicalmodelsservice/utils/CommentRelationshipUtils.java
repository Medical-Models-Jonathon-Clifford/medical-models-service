package org.jono.medicalmodelsservice.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jono.medicalmodelsservice.model.CommentRelationship;

public class CommentRelationshipUtils {

    private CommentRelationshipUtils() {
        // Utility class
    }

    public static List<String> extractIds(final List<CommentRelationship> commentRelationshipList) {
        return commentRelationshipList.stream()
                .map(CommentRelationship::getId)
                .toList();
    }

    public static List<String> collectAllCommentIds(final List<CommentRelationship> relationships,
            final String targetId) {
        final Set<String> uniqueIds = new HashSet<>();
        relationships.forEach(rel -> {
            uniqueIds.add(rel.getParentCommentId());
            uniqueIds.add(rel.getChildCommentId());
        });
        uniqueIds.add(targetId);
        return new ArrayList<>(uniqueIds);
    }
}
