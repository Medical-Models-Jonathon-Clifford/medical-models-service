package org.jono.medicalmodelsservice.service.comment;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class CommentsToDelete {
    List<String> commentIds;
    List<String> childCommentIds;
}
