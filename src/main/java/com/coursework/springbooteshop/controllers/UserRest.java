package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.UserNotFound;
import com.coursework.springbooteshop.model.User;
import com.coursework.springbooteshop.repos.UserRepository;
import com.google.gson.Gson;
import jakarta.persistence.Entity;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

import static com.sun.tools.javac.jvm.Gen.one;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserRest {
    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "getAllUsers")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping(value = "addUserFull")
    public @ResponseBody User saveUser(@RequestBody User user) {
        return userRepository.saveAndFlush(user);
    }

    /*
    @PostMapping(value = "addUserCustom")
    public @ResponseBody User saveUserCustom(@RequestBody String userInfo) {
        Gson gson = new Gson();
        Properties properties = gson.fromJson(userInfo, Properties.class);
        return UserRepository.saveAndFlush(new User(properties.getProperty("login"), properties.getProperty("password")));
    }
     */

    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody User user) {
        userRepository.save(user);
        return userRepository.findById(user.getId()).get();
    }

    @DeleteMapping(value = "deleteUser/{id}")
    public @ResponseBody String deleteUser(@PathVariable(name = "id") int id) {
        userRepository.deleteById(id);
        // check if deleted
        return "User with id = " + id + " was deleted";
    }

    @GetMapping(value = "getUserById/{id}")
    public EntityModel<User> getUserById(@PathVariable(name = "id") int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        return EntityModel.of(user, linkTo(methodOn(UserRest.class, one(id))).withSelfRel(), linkTo(UserRest.class, getAllUsers()).withRel("Users"));
    }

}
