package com.codewitharjun.fullstack_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // only admin
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // both user + admin
                        .anyRequest().permitAll() // allow everything else for now
                )
                .httpBasic(); // simple basic auth (you can replace with JWT later)
        return http.build();
    }
}
