package com.coursework.springbooteshop.repos;

import com.coursework.springbooteshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
