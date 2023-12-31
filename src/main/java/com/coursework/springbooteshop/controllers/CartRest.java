package com.coursework.springbooteshop.controllers;

import com.coursework.springbooteshop.errors.CartNotFound;
import com.coursework.springbooteshop.errors.ProductNotFound;
import com.coursework.springbooteshop.errors.UserNotFound;
import com.coursework.springbooteshop.model.Cart;
import com.coursework.springbooteshop.model.Product;
import com.coursework.springbooteshop.model.User;
import com.coursework.springbooteshop.repos.CartRepository;
import com.coursework.springbooteshop.repos.ProductRepository;
import com.coursework.springbooteshop.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class CartRest {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @PostMapping("/addCart")
    public ResponseEntity<Cart> addCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        User user = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new UserNotFound("User not found with username: " + currentUserName));

        Cart cart = new Cart();
        cart.setOwner(user);
        cart.setDateCreated(LocalDate.now());
        cart.setItemsInCart(new ArrayList<>());

        Cart newCart = cartRepository.save(cart);
        return new ResponseEntity<>(newCart, HttpStatus.CREATED);
    }

    @GetMapping("/getAllCarts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/getCart/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping("/updateCart/{id}")
    @Transactional
    public ResponseEntity<Cart> updateCart(@PathVariable int id, @RequestBody Cart cartDetails) {

        Cart existingCart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));

        if (cartDetails.getOwner() != null && (existingCart.getOwner() == null || cartDetails.getOwner().getId() != existingCart.getOwner().getId())) {
            User newOwner = userRepository.findById(cartDetails.getOwner().getId())
                    .orElseThrow(() -> new UserNotFound(cartDetails.getOwner().getId()));
            existingCart.setOwner(newOwner);
        }

        if (cartDetails.getItemsInCart() != null && !cartDetails.getItemsInCart().isEmpty()) {
            existingCart.getItemsInCart().forEach(product -> product.setCart(null));
            existingCart.getItemsInCart().clear();

            for (Product productDto : cartDetails.getItemsInCart()) {
                Product product = productRepository.findById(productDto.getId())
                        .orElseThrow(() -> new ProductNotFound(productDto.getId()));
                product.setCart(existingCart);
                existingCart.getItemsInCart().add(product);
            }
        }

        Cart updatedCart = cartRepository.saveAndFlush(existingCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }


    @DeleteMapping("/deleteCart/{id}")
    @Transactional
    public ResponseEntity<String> deleteCart(@PathVariable int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));

        for (Product product : cart.getItemsInCart()) {
            product.setCart(null);
            productRepository.save(product);
        }

        cart.getItemsInCart().clear();

        cart.getOwner().getMyCarts().remove(cart);

        cartRepository.delete(cart);

        return new ResponseEntity<>("Cart with id = " + id + " was successfully deleted", HttpStatus.OK);
    }



}
