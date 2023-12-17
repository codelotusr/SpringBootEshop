package com.coursework.springbooteshop.errors;

public class ProductNotFound extends RuntimeException {
    public ProductNotFound(Integer id) {
        super("Could not find product " + id);
    }
}
