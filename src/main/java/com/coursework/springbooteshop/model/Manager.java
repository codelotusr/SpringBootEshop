package com.coursework.springbooteshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manager extends User {
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    @NotBlank(message = "Medical certification is required")
    private String medicalCertification;
    private LocalDate employmentDate;
    @ManyToMany
    private List<Warehouse> worksAtWarehouse;
    public Manager(String username, String password, LocalDate birthDate, String firstName, String lastName, String employeeId, String medicalCertification, LocalDate employmentDate, String role) {
        super(username, password, birthDate, firstName, lastName, role);
        this.employeeId = employeeId;
        this.medicalCertification = medicalCertification;
        this.employmentDate = employmentDate;
    }

}
