package com.coursework.springbooteshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    String username;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
    @NotNull(message = "Birth date is required")
    LocalDate birthDate;
    @NotNull(message = "First name is required")
    String firstName;
    @NotNull(message = "Last name is required")
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
