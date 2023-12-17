package com.coursework.springbooteshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(unique = true)
    String username;
    String password;
    LocalDate birthDate;
    String firstName;
    String lastName;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Cart> myCarts;

    public User(String login, String password, LocalDate birthDate, String name, String surname) {
        this.username = login;
        this.password = password;
        this.birthDate = birthDate;
        this.firstName = name;
        this.lastName = surname;
    }

    public User(int id, String login, String password, LocalDate birthDate) {
        this.id = id;
        this.username = login;
        this.password = password;
        this.birthDate = birthDate;
    }


    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}