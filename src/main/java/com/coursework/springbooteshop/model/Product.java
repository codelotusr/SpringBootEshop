package com.coursework.springbooteshop.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    String description;
    String manufacturer;

    @ManyToOne
    @JsonBackReference
    Warehouse warehouse;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    List<Review> reviews;

    @ManyToOne
    Cart cart;

    public Product(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Product(String title, String description, String manufacturer, Warehouse warehouse) {
        this.title = title;
        this.description = description;
        this.manufacturer = manufacturer;
        this.warehouse = warehouse;
    }

    public Product(String title, String description, String manufacturer) {
        this.title = title;
        this.description = description;
        this.manufacturer = manufacturer;
    }

    @Override
    public String toString() {
        return title + " " + manufacturer;
    }

}
