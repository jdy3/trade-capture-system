package com.technicalchallenge.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @PersistenceUnit
    private EntityManagerFactory emf;

}
