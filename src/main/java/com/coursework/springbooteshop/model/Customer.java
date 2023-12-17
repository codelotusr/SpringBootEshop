package com.coursework.springbooteshop.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer extends User {
    private String address;
    private String cardNo;


    public Customer(String login, String password, LocalDate birthDate, String name, String surname, String address, String cardNo) {
        super(login, password, birthDate, name, surname);
        this.address = address;
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username=" + username +
                ", password=" + password +
                ", birthDate=" + birthDate +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", address=" + address +
                '}';
    }

}
