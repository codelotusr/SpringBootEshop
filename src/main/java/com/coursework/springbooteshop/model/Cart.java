package com.coursework.springbooteshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate dateCreated;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<Product> itemsInCart;
    @ManyToOne
    @JsonBackReference
    private User owner;

    public Cart(User owner) {
        this.owner = owner;
        this.dateCreated = LocalDate.now();
        this.itemsInCart = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", itemsInCart=" + itemsInCart +
                ", owner=" + owner +
                '}';
    }
}