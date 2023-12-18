package com.coursework.springbooteshop.errors;

public class UserNotFound extends RuntimeException {

    public UserNotFound(int id) {
        super("User with id " + id + " not found");
    }

    public UserNotFound(String username) {
        super("User with username " + username + " not found");
    }
}
