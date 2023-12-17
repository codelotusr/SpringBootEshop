package com.coursework.springbooteshop.repos;

import com.coursework.springbooteshop.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
