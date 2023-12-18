package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.UserNotFound;
import com.coursework.springbooteshop.model.*;
import com.coursework.springbooteshop.repos.CartRepository;
import com.coursework.springbooteshop.repos.ProductRepository;
import com.coursework.springbooteshop.repos.UserRepository;
import com.coursework.springbooteshop.serializers.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;


@RestController
public class UserRest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    @PostMapping(value = "addUserFull")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping(value = "addUserCustom")
    public ResponseEntity<?> saveUserCustom(@RequestBody String userInfo) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        JsonObject jsonObject = gson.fromJson(userInfo, JsonObject.class);

        String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;
        if (type == null) {
            return new ResponseEntity<>("'type' field is required", HttpStatus.BAD_REQUEST);
        }

        User user;
        switch (type) {
            case "Customer":
                user = gson.fromJson(userInfo, Customer.class);
                break;
            case "Manager":
                user = gson.fromJson(userInfo, Manager.class);
                break;
            default:
                return new ResponseEntity<>("Invalid user type", HttpStatus.BAD_REQUEST);
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @GetMapping(value = "getAllUsers")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "getUserById/{id}")
    public EntityModel<User> getUserById(@PathVariable(name = "id") int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        return EntityModel.of(user,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserRest.class).getUserById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserRest.class).getAllUsers()).withRel("Users"));
    }

    @GetMapping(value = "getUserByUsername/{username}")
    public EntityModel<User> getUserByUsername(@PathVariable(name = "username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        return EntityModel.of(user,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserRest.class).getUserByUsername(username)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserRest.class).getAllUsers()).withRel("Users"));
    }

    @PutMapping(value = "updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody String userDetailsJson) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        JsonObject jsonObject = gson.fromJson(userDetailsJson, JsonObject.class);

        String type = jsonObject.get("type").getAsString();
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        if ("Customer".equals(type) && existingUser instanceof Customer) {
            Customer updatedCustomer = gson.fromJson(userDetailsJson, Customer.class);
            updateCustomer((Customer) existingUser, updatedCustomer);
        } else if ("Manager".equals(type) && existingUser instanceof Manager) {
            Manager updatedManager = gson.fromJson(userDetailsJson, Manager.class);
            updateManager((Manager) existingUser, updatedManager);
        } else {
            return new ResponseEntity<>("Invalid user type or ID", HttpStatus.BAD_REQUEST);
        }

        userRepository.save(existingUser);
        return new ResponseEntity<>(existingUser, HttpStatus.OK);
    }

    private void updateCustomer(Customer existingCustomer, Customer updatedCustomer) {
        existingCustomer.setUsername(updatedCustomer.getUsername());
        existingCustomer.setPassword(passwordEncoder.encode(updatedCustomer.getPassword()));
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setBirthDate(updatedCustomer.getBirthDate());
        existingCustomer.setAddress(updatedCustomer.getAddress());
        existingCustomer.setCardNo(updatedCustomer.getCardNo());
        existingCustomer.setRole(updatedCustomer.getRole());
    }

    private void updateManager(Manager existingManager, Manager updatedManager) {
        existingManager.setUsername(updatedManager.getUsername());
        existingManager.setPassword(passwordEncoder.encode(updatedManager.getPassword()));
        existingManager.setFirstName(updatedManager.getFirstName());
        existingManager.setLastName(updatedManager.getLastName());
        existingManager.setBirthDate(updatedManager.getBirthDate());
        existingManager.setEmployeeId(updatedManager.getEmployeeId());
        existingManager.setEmploymentDate(updatedManager.getEmploymentDate());
        existingManager.setMedicalCertification(updatedManager.getMedicalCertification());
        existingManager.setRole(updatedManager.getRole());
    }


    @DeleteMapping(value = "deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        for (Cart cart : user.getMyCarts()) {
            for (Product product : cart.getItemsInCart()) {
                product.setCart(null);
                productRepository.save(product);
            }
            cartRepository.delete(cart);
        }

        userRepository.deleteById(id);

        boolean existsAfterDelete = userRepository.existsById(id);
        if (!existsAfterDelete) {
            return new ResponseEntity<>("User with id = " + id + " was successfully deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete the user with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
