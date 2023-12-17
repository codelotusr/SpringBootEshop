package com.coursework.springbooteshop.errors;

public class WarehouseNotFound extends RuntimeException {
    public WarehouseNotFound(Integer id) {
        super("Could not find warehouse " + id);
    }
}
