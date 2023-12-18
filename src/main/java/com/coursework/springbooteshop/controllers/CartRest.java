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
                .orElseThrow(() -> new CartNotFound(id)); // Implement CartNotFoundException

        // Update the owner of the cart
        if (cartDetails.getOwner() != null) {
            User user = userRepository.findById(cartDetails.getOwner().getId())
                    .orElseThrow(() -> new UserNotFound(cartDetails.getOwner().getId())); // Implement UserNotFoundException
            existingCart.setOwner(user);
        }

        // Update the products in the cart
        if (cartDetails.getItemsInCart() != null) {
            // Clear the existing collection instead of replacing it
            existingCart.getItemsInCart().clear();

            // Add all the new products to the existing collection
            for (Product productDto : cartDetails.getItemsInCart()) {
                Product product = productRepository.findById(productDto.getId())
                        .orElseThrow(() -> new ProductNotFound(productDto.getId())); // Implement ProductNotFoundException
                product.setCart(existingCart); // Link the product to the cart
                existingCart.getItemsInCart().add(product);
            }
        }

        Cart updatedCart = cartRepository.save(existingCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }




    @DeleteMapping("/deleteCart/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable int id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFound(id));

        cartRepository.delete(cart);
        return new ResponseEntity<>("Cart with id = " + id + " was successfully deleted", HttpStatus.OK);
    }

}
