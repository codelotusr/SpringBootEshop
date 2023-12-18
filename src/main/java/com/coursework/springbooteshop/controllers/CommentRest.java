package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.CommentNotFound;
import com.coursework.springbooteshop.model.Comment;
import com.coursework.springbooteshop.repos.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentRest {
    private CommentRepository commentRepository;

    @PostMapping(value = "addComment")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        Comment savedComment = commentRepository.saveAndFlush(comment);
        return ResponseEntity.ok(savedComment);
    }

    @GetMapping(value = "getAllComments")
    public ResponseEntity<Iterable<Comment>> getAllComments() {
        Iterable<Comment> comments = commentRepository.findAll();
        return ResponseEntity.ok(comments);
    }

    @GetMapping(value = "getCommentById/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable(name = "id") int id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFound(id));
        return ResponseEntity.ok(comment);
    }




}
