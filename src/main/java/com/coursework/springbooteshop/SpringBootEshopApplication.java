package com.coursework.springbooteshop;

import com.coursework.springbooteshop.model.Customer;
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
            repository.save(new Customer("a", "a", LocalDate.of(2002, 9, 15), "Testinis", "Customer", "Test g. 15", "5555555555555555"));
            repository.save(new Customer("a1", "a1", LocalDate.of(2003, 9, 12), "Testinis1", "Customer1", "Test g. 5", "4555555555555555"));


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
}
