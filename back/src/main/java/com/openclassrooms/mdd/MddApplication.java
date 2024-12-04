package com.openclassrooms.mdd;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * MDD main application
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:47
 */

@SpringBootApplication
@EnableJpaAuditing
public class MddApplication {
    public static void main(String[] args) {
        SpringApplication.run(MddApplication.class, args);
    }
}