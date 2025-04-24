package org.jono.medicalmodelsservice.repository.jdbc;

import org.jono.medicalmodelsservice.model.CommentChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CommentChildRepository {
    private final CommentChildCrudRepository commentChildCrudRepository;

    @Autowired
    public CommentChildRepository(CommentChildCrudRepository commentChildCrudRepository) {
        this.commentChildCrudRepository = commentChildCrudRepository;
    }

    public List<CommentChild> findByCommentId(String commentId) {
        return this.commentChildCrudRepository.findAllByCommentId(commentId);
    }

    public List<CommentChild> findCommentChildrenByCommentId(String commentId) {
        List<CommentChild> commentChildren = this.commentChildCrudRepository.findAllByCommentId(commentId);
        List<CommentChild> allCommentChildren = new ArrayList<>(commentChildren);
        for (CommentChild commentChild : commentChildren) {
            List<CommentChild> nextCommentChildren = this.commentChildCrudRepository.findAllByCommentId(commentChild.getChildCommentId());
            allCommentChildren.addAll(nextCommentChildren);
        }
        return allCommentChildren;
    }

    public List<CommentChild> findListByChildCommentId(String childCommentId) {
        return this.commentChildCrudRepository.findAllByChildCommentId(childCommentId);
    }

    public CommentChild findLeafNodesParentConnection(String childCommentId) {
        return this.commentChildCrudRepository.findFirstByChildCommentId(childCommentId);
    }

    public void deleteByIds(Collection<String> ids) {
        this.commentChildCrudRepository.deleteAllById(ids);
    }

    public List<CommentChild> findCommentChildrenByChildCommentId(String commentId) {
        List<CommentChild> commentChildren = this.commentChildCrudRepository.findAllByChildCommentId(commentId);
        List<CommentChild> allCommentChildren = new ArrayList<>(commentChildren);
        for (CommentChild commentChild : commentChildren) {
            List<CommentChild> nextCommentChildren = this.commentChildCrudRepository.findAllByChildCommentId(commentChild.getCommentId());
            allCommentChildren.addAll(nextCommentChildren);
        }
        return allCommentChildren;
    }
}
