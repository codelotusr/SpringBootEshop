package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.ProductNotFound;
import com.coursework.springbooteshop.model.CentralProcessingUnit;
import com.coursework.springbooteshop.model.GraphicsCard;
import com.coursework.springbooteshop.model.Motherboard;
import com.coursework.springbooteshop.model.Product;
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
public class ProductRest {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping(value = "addProductCustom")
    public ResponseEntity<?> addProductCustom(@RequestBody String productInfo) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        JsonObject jsonObject = gson.fromJson(productInfo, JsonObject.class);

        String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;
        if (type == null) {
            return new ResponseEntity<>("'type' field is required", HttpStatus.BAD_REQUEST);
        }

        Product product;
        switch (type) {
            case "CentralProcessingUnit":
                product = gson.fromJson(productInfo, CentralProcessingUnit.class);
                break;
            case "GraphicsCard":
                product = gson.fromJson(productInfo, GraphicsCard.class);
                break;
            case "Motherboard":
                product = gson.fromJson(productInfo, Motherboard.class);
                break;
            default:
                return new ResponseEntity<>("Invalid product type", HttpStatus.BAD_REQUEST);
        }


        Product savedProduct = productRepository.saveAndFlush(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping(value = "getAllProducts")
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping(value = "getProductById/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") int id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(id));
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping(value = "updateProduct/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody String productDetailsJson) {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        JsonObject jsonObject = gson.fromJson(productDetailsJson, JsonObject.class);

        String type = jsonObject.get("type").getAsString();
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound(id));

        if ("CentralProcessingUnit".equals(type) && existingProduct instanceof CentralProcessingUnit) {
            CentralProcessingUnit updatedCPU = gson.fromJson(productDetailsJson, CentralProcessingUnit.class);
            updateCentralProcessingUnit((CentralProcessingUnit) existingProduct, updatedCPU);
        } else if ("GraphicsCard".equals(type) && existingProduct instanceof GraphicsCard) {
            GraphicsCard updatedGraphicsCard = gson.fromJson(productDetailsJson, GraphicsCard.class);
            updateGraphicsCard((GraphicsCard) existingProduct, updatedGraphicsCard);
        } else if ("Motherboard".equals(type) && existingProduct instanceof Motherboard) {
            Motherboard updatedMotherboard = gson.fromJson(productDetailsJson, Motherboard.class);
            updateMotherboard((Motherboard) existingProduct, updatedMotherboard);
        } else {
            return new ResponseEntity<>("Invalid product type or ID", HttpStatus.BAD_REQUEST);
        }

        productRepository.save(existingProduct);
        return new ResponseEntity<>(existingProduct, HttpStatus.OK);
    }

    private void updateCentralProcessingUnit(CentralProcessingUnit existingProduct, CentralProcessingUnit updatedProduct) {
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        existingProduct.setSocket(updatedProduct.getSocket());
        existingProduct.setCoreCount(updatedProduct.getCoreCount());
        existingProduct.setCoreFrequency(updatedProduct.getCoreFrequency());
        existingProduct.setTdp(updatedProduct.getTdp());
        existingProduct.setWarehouse(updatedProduct.getWarehouse());
    }

    private void updateGraphicsCard(GraphicsCard existingProduct, GraphicsCard updatedProduct) {
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        existingProduct.setMemorySize(updatedProduct.getMemorySize());
        existingProduct.setMemoryFrequency(updatedProduct.getMemoryFrequency());
        existingProduct.setTdp(updatedProduct.getTdp());
        existingProduct.setMemoryType(updatedProduct.getMemoryType());
        existingProduct.setCoreFrequency(updatedProduct.getCoreFrequency());
        existingProduct.setWarehouse(updatedProduct.getWarehouse());
    }

    private void updateMotherboard(Motherboard existingProduct, Motherboard updatedProduct) {
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());
        existingProduct.setSocket(updatedProduct.getSocket());
        existingProduct.setMemoryType(updatedProduct.getMemoryType());
        existingProduct.setChipset(updatedProduct.getChipset());
        existingProduct.setMaxMemorySize(updatedProduct.getMaxMemorySize());
        existingProduct.setMaxMemoryFrequency(updatedProduct.getMaxMemoryFrequency());
        existingProduct.setWarehouse(updatedProduct.getWarehouse());
    }

    @DeleteMapping(value = "deleteProduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") int id) {
        boolean existsBeforeDelete = productRepository.existsById(id);
        if (existsBeforeDelete) {
            productRepository.deleteById(id);
            boolean existsAfterDelete = productRepository.existsById(id);
            if (!existsAfterDelete) {
                return new ResponseEntity<>("Product with id = " + id + " was successfully deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to delete the product with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ProductNotFound(id);
        }
    }


}
