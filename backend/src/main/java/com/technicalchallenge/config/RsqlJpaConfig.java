package com.technicalchallenge.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RsqlJpaConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public EntityManager rsqlEntityManager() {
        return entityManager;
    }
}
