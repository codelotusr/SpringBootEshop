package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.CommentNotFound;
import com.coursework.springbooteshop.model.Comment;
import com.coursework.springbooteshop.model.Product;
import com.coursework.springbooteshop.model.Review;
import com.coursework.springbooteshop.repos.CommentRepository;
import com.coursework.springbooteshop.repos.ProductRepository;
import com.coursework.springbooteshop.serializers.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class CommentRest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping(value = "addComment")
    public ResponseEntity<?> addComment(@RequestBody String commentInfo) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        JsonObject jsonObject = gson.fromJson(commentInfo, JsonObject.class);

        String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;
        if (type == null) {
            return new ResponseEntity<>("'type' field is required", HttpStatus.BAD_REQUEST);
        }

        Comment comment;
        switch (type) {
            case "Comment":
                comment = gson.fromJson(commentInfo, Comment.class);
                break;
            case "Review":
                comment = gson.fromJson(commentInfo, Review.class);
                break;
            default:
                return new ResponseEntity<>("Invalid comment type", HttpStatus.BAD_REQUEST);
        }

        Comment savedComment = commentRepository.saveAndFlush(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
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

    @PutMapping(value = "updateComment/{id}")
    public ResponseEntity<?> updateComment(@PathVariable int id, @RequestBody String commentDetailsJson) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        JsonObject jsonObject = gson.fromJson(commentDetailsJson, JsonObject.class);

        String type = jsonObject.get("type").getAsString();
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFound(id));

        if ("Comment".equals(type) && existingComment != null) {
            Comment updatedComment = gson.fromJson(commentDetailsJson, Comment.class);
            updateCommentDetails(existingComment, updatedComment);
        } else if ("Review".equals(type) && existingComment instanceof Review) {
            Review updatedReview = gson.fromJson(commentDetailsJson, Review.class);
            updateReviewDetails((Review) existingComment, updatedReview);
        } else {
            return new ResponseEntity<>("Invalid comment type or ID", HttpStatus.BAD_REQUEST);
        }

        commentRepository.save(existingComment);
        return new ResponseEntity<>(existingComment, HttpStatus.OK);
    }

    private void updateCommentDetails(Comment existingComment, Comment updatedComment) {
        existingComment.setCommentTitle(updatedComment.getCommentTitle());
        existingComment.setCommentBody(updatedComment.getCommentBody());
        existingComment.setDateCreated(updatedComment.getDateCreated());
    }

    private void updateReviewDetails(Review existingReview, Review updatedReview) {
        existingReview.setRating(updatedReview.getRating());
        existingReview.setCommentTitle(updatedReview.getCommentTitle());
        existingReview.setCommentBody(updatedReview.getCommentBody());
        existingReview.setDateCreated(updatedReview.getDateCreated());
    }

    @DeleteMapping(value = "deleteComment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "id") int id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFound(id));

        if (comment instanceof Review review) {
            Product product = review.getProduct();
            if (product != null) {
                product.getReviews().remove(review);
                productRepository.save(product);
            }
        }

        commentRepository.deleteById(id);
        boolean existsAfterDelete = commentRepository.existsById(id);
        if (!existsAfterDelete) {
            return new ResponseEntity<>("Comment/Review with id = " + id + " was successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete the comment/review with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
