package com.coursework.springbooteshop.errors;

public class CartNotFound extends RuntimeException {
    public CartNotFound(Integer id) {
        super("Could not find cart " + id);
    }
}
