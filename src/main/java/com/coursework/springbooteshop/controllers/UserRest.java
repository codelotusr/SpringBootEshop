package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.UserNotFound;
import com.coursework.springbooteshop.model.Customer;
import com.coursework.springbooteshop.model.Manager;
import com.coursework.springbooteshop.model.User;
import com.coursework.springbooteshop.repos.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.persistence.Entity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserRest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "addUserFull")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping(value = "addUserCustom")
    public ResponseEntity<User> saveUserCustom(@RequestBody String userInfo) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(userInfo, JsonObject.class);

        String type = jsonObject.get("type").getAsString();
        User user = switch (type) {
            case "Customer" -> gson.fromJson(userInfo, Customer.class);
            case "Manager" -> gson.fromJson(userInfo, Manager.class);
            default -> gson.fromJson(userInfo, User.class);
        };

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

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

    @PutMapping(value = "updateUser")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(value = "deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            boolean existsAfterDelete = userRepository.existsById(id);
            if (!existsAfterDelete) {
                return new ResponseEntity<>("User with id = " + id + " was successfully deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to delete the user with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new UserNotFound(id);
        }
    }


}
