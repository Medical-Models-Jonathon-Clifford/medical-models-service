package org.jono.medicalmodelsservice.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.service.comment.CommentService;
import org.jono.medicalmodelsservice.service.comment.CommentTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public Comment handleCommentPost(@RequestBody final NewComment newComment) {
        return commentService.createComment(newComment);
    }

    @GetMapping(path = "/documents/{documentId}",
            produces = "application/json")
    @ResponseBody
    public List<CommentTree> getCommentsForDocumentId(@PathVariable final String documentId) {
        return commentService.getComments(documentId);
    }

    @PutMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public ResponseEntity<Comment> handleCommentsPut(@PathVariable final String id,
            @RequestBody final EditCommentDto editCommentDto) {
        return commentService.updateComment(id, editCommentDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public void deleteComment(@PathVariable final String id) {
        commentService.deleteComment(id);
    }
}
