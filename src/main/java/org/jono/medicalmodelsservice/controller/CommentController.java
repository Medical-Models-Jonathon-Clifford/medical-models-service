package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Comment;
import org.jono.medicalmodelsservice.model.dto.EditCommentDto;
import org.jono.medicalmodelsservice.model.NewComment;
import org.jono.medicalmodelsservice.service.comment.CommentService;
import org.jono.medicalmodelsservice.service.comment.CommentNode;
import org.springframework.beans.factory.annotation.Autowired;
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
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public Mono<Comment> handleCommentPost(@RequestBody NewComment newComment) {
        return commentService.createComment(newComment);
    }

    @GetMapping(path = "/documents/{documentId}",
            produces = "application/json")
    @ResponseBody
    public Mono<List<CommentNode>> getCommentsForDocumentId(@PathVariable String documentId) {
        return commentService.getComments(documentId);
    }

    @PutMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Comment> handleCommentsPut(@PathVariable String id,
                                           @RequestBody EditCommentDto editCommentDto) {
        return commentService.updateComment(id, editCommentDto);
    }

    @DeleteMapping(path = "/{id}",
            produces = "application/json")
    @ResponseBody
    public Mono<Long> deleteComment(@PathVariable String id) {
        return commentService.deleteComment(id);
    }
}
