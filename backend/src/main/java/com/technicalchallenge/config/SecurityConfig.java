package com.technicalchallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF: ignore dev tooling + swagger + h2
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                    AntPathRequestMatcher.antMatcher("/h2-console/**"),
                    AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
                    AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
                    AntPathRequestMatcher.antMatcher("/swagger-ui.html")
            ))
            // Allow H2 console to render in a frame
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
                            AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
                            AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
                            AntPathRequestMatcher.antMatcher("/h2-console/**"),
                            AntPathRequestMatcher.antMatcher("/actuator/health")
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            // Basic + form login for now
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

        return http.build();
    }
}


