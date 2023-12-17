package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.repos.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WarehouseRest {
    @Autowired
    private WarehouseRepository warehouseRepository;


}
