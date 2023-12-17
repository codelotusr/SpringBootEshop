package com.coursework.springbooteshop.repos;

import com.coursework.springbooteshop.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
}
