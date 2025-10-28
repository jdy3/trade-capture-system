package com.technicalchallenge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableJpaRepositories(basePackages = "com.technicalchallenge.repository")
@EntityScan(basePackages = "com.technicalchallenge.model")
public class PersistenceConfig {}

