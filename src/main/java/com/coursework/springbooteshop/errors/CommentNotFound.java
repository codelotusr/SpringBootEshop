package com.coursework.springbooteshop.errors;

public class CommentNotFound extends RuntimeException {
    public CommentNotFound(int id) {
        super("Comment with id " + id + " not found");
    }
    public CommentNotFound() {
        super("Comment not found");
    }
    public CommentNotFound(String message) {
        super(message);
    }
    public CommentNotFound(String message, Throwable cause) {
        super(message, cause);
    }
    public CommentNotFound(Throwable cause) {
        super(cause);
    }
    public CommentNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
