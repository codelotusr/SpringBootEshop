package com.coursework.springbooteshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String commentTitle;
    private String commentBody;
    private LocalDate dateCreated;
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<Comment> replies;
    @ManyToOne
    @JsonIgnore
    private Comment parentComment;

    public Comment(String commentTitle, String commentBody) {
        this.commentTitle = commentTitle;
        this.commentBody = commentBody;
        this.dateCreated = LocalDate.now();
        this.replies = new ArrayList<>();
    }

    public Comment(String commentTitle, String commentBody, Comment parentComment) {
        this.commentTitle = commentTitle;
        this.commentBody = commentBody;
        this.dateCreated = LocalDate.now();
        this.parentComment = parentComment;
        this.replies = new ArrayList<>();
    }

    @Override
    public String toString() {
        return commentTitle + ":" + dateCreated;
    }
}
