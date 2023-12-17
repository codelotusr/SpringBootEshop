package com.coursework.springbooteshop;

import com.coursework.springbooteshop.model.Customer;
import com.coursework.springbooteshop.model.Manager;
import com.coursework.springbooteshop.model.User;
import com.coursework.springbooteshop.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication
public class SpringBootEshopApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootEshopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEshopApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository repository) {
        return (args) -> {
            saveUserIfNotExists(repository, new Customer("aaa", "password", LocalDate.of(2002, 9, 15), "Testinis", "Customer", "Test g. 15", "5555555555555555", "CUSTOMER"));
            saveUserIfNotExists(repository, new Customer("aaa1", "password", LocalDate.of(2003, 9, 12), "Testinis1", "Customer1", "Test g. 5", "4555555555555555", "CUSTOMER"));
            saveUserIfNotExists(repository, new Manager("man1", "password", LocalDate.of(2000, 10, 11), "Testinis2", "Manager", "EMP12345", "Certified", LocalDate.of(2020, 10, 11), "ADMIN"));
            saveUserIfNotExists(repository, new Manager("man2", "password", LocalDate.of(2001, 10, 11), "Testinis3", "Manager1", "EMP12346", "Certified", LocalDate.of(2020, 5, 11), "MANAGER"));


            logger.info("All users found with findAll():");
            logger.info("-------------------------------");
            for (User customer : repository.findAll()) {
                logger.info(customer.toString());
            }
            logger.info("");

            Optional<User> user = repository.findById(1);
            logger.info("User found with findById(1L):");
            logger.info("--------------------------------");
            logger.info(user.toString());
            logger.info("");

            /*
            logger.info("Customer found with findByLastName('Bauer'):");
            logger.info("--------------------------------------------");
            repository.findByName("Testinis").forEach(t -> {
                logger.info(t.toString());
            });

             */
            logger.info("");
        };
    }

    private void saveUserIfNotExists(UserRepository repository, User user) {
        repository.findByUsername(user.getUsername()).ifPresentOrElse(
                existingUser -> logger.info("User with username " + user.getUsername() + " already exists."),
                () -> repository.save(user)
        );
    }
}
