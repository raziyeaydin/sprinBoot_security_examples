package com.example.springapp.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    //We don't want to use any database, so we can use In-Memory for having data
    //with @Bean annotation, one instance of UserDetailsService is added into IOC Container.
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        //without any roles
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("guest")
                .password("guest")
                .build());
        //with USER role
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER") //this roles method will be used by SecurityFilterChain bean
                .build());
        //with ADMIN role
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN") //this roles method will be used by SecurityFilterChain bean
                .build());
        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/public").permitAll() // it means every user can access to /public endpoint
                        .requestMatchers("/private").hasRole("USER") // it means the users who have USER role can access to /admin endpoint
                        .requestMatchers("/admin").hasRole("ADMIN") // it means the users who have ADMIN role can access to /admin endpoint
                        .anyRequest().authenticated() // For other every endpoints authentication is required
                )
                .httpBasic(Customizer.withDefaults()) // HTTP Basic Auth
                .csrf((csrf) -> csrf.disable()); // deactivate CSRF protection (generally for REST APIs)

        return http.build();
    }

}
