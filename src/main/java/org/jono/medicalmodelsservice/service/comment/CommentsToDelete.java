package org.jono.medicalmodelsservice.service.comment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
class CommentsToDelete {
    List<String> commentIds;
    List<String> childCommentIds;
}
