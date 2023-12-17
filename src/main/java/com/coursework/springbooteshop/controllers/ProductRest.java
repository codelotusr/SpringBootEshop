package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRest {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping(value = "addProduct")

}
