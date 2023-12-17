package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.WarehouseNotFound;
import com.coursework.springbooteshop.model.Warehouse;
import com.coursework.springbooteshop.repos.WarehouseRepository;
import com.coursework.springbooteshop.serializers.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
public class WarehouseRest {
    @Autowired
    private WarehouseRepository warehouseRepository;

    @PostMapping(value = "addWarehouse")
    public ResponseEntity<Warehouse> addWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse savedWarehouse = warehouseRepository.saveAndFlush(warehouse);
        return ResponseEntity.ok(savedWarehouse);
    }

    @GetMapping(value = "getAllWarehouses")
    public ResponseEntity<Iterable<Warehouse>> getAllWarehouses() {
        Iterable<Warehouse> warehouses = warehouseRepository.findAll();
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @GetMapping(value = "getWarehouseById/{id}")
    public EntityModel<Warehouse> getWarehouseById(@PathVariable(name = "id") int id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFound(id));
        return EntityModel.of(warehouse,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WarehouseRest.class).getWarehouseById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(WarehouseRest.class).getAllWarehouses()).withRel("Warehouses"));
    }

    @PutMapping(value = "updateWarehouse/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable(name = "id") int id, @RequestBody String warehouseInfoJson) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Warehouse warehouseInfo = gson.fromJson(warehouseInfoJson, Warehouse.class);
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFound(id));
        warehouse.setTitle(warehouseInfo.getTitle());
        warehouse.setAddress(warehouseInfo.getAddress());
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @DeleteMapping(value = "deleteWarehouse/{id}")
    public ResponseEntity<String> deleteWarehouse(@PathVariable(name = "id") int id) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        if (warehouse.isPresent()) {
            warehouseRepository.deleteById(id);
            return new ResponseEntity<>("Warehouse with id = " + id + " was successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete the warehouse with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
