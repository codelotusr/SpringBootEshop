package com.coursework.springbooteshop.repos;

import com.coursework.springbooteshop.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
