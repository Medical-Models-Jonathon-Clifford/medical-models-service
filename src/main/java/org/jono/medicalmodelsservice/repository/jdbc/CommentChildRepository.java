package org.jono.medicalmodelsservice.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jono.medicalmodelsservice.model.CommentChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentChildRepository {
  private final CommentChildCrudRepository commentChildCrudRepository;

  @Autowired
  public CommentChildRepository(final CommentChildCrudRepository commentChildCrudRepository) {
    this.commentChildCrudRepository = commentChildCrudRepository;
  }

  public List<CommentChild> findByCommentId(final String commentId) {
    return this.commentChildCrudRepository.findAllByCommentId(commentId);
  }

  public List<CommentChild> findCommentChildrenByCommentId(final String commentId) {
    final List<CommentChild> commentChildren = this.commentChildCrudRepository.findAllByCommentId(commentId);
    final List<CommentChild> allCommentChildren = new ArrayList<>(commentChildren);
    for (final CommentChild commentChild : commentChildren) {
      final List<CommentChild> nextCommentChildren = this.commentChildCrudRepository.findAllByCommentId(
          commentChild.getChildCommentId());
      allCommentChildren.addAll(nextCommentChildren);
    }
    return allCommentChildren;
  }

  public List<CommentChild> findListByChildCommentId(final String childCommentId) {
    return this.commentChildCrudRepository.findAllByChildCommentId(childCommentId);
  }

  public CommentChild findLeafNodesParentConnection(final String childCommentId) {
    return this.commentChildCrudRepository.findFirstByChildCommentId(childCommentId);
  }

  public void deleteByIds(final Collection<String> ids) {
    this.commentChildCrudRepository.deleteAllById(ids);
  }

  public List<CommentChild> findCommentChildrenByChildCommentId(final String commentId) {
    final List<CommentChild> commentChildren = this.commentChildCrudRepository.findAllByChildCommentId(commentId);
    final List<CommentChild> allCommentChildren = new ArrayList<>(commentChildren);
    for (final CommentChild commentChild : commentChildren) {
      final List<CommentChild> nextCommentChildren = this.commentChildCrudRepository.findAllByChildCommentId(
          commentChild.getCommentId());
      allCommentChildren.addAll(nextCommentChildren);
    }
    return allCommentChildren;
  }
}
