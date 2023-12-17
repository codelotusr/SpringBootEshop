package com.coursework.springbooteshop.repos;

import com.coursework.springbooteshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
