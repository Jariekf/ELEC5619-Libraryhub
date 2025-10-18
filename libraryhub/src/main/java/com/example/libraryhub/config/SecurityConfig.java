package com.example.libraryhub.config;

import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Password encoder for hashing user passwords. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Bridge AuthenticationManager out of Spring Boot auto config. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (String username) -> {
            User u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            // Set authorities by role; here we use ADMIN for Admin subclass, otherwise CUSTOMER
            String role = (u instanceof Admin) ? "ADMIN" : "CUSTOMER";
            UserDetails details = org.springframework.security.core.userdetails.User
                    .withUsername(u.getUsername())
                    .password(u.getPasswordHash())   // ensure your User has getPasswordHash()
                    .roles(role)                     // results in "ROLE_ADMIN"/"ROLE_CUSTOMER"
                    .disabled(!u.isActive())
                    .build();
            return details;
        };
    }

    /** HTTP security: allow auth pages, require login elsewhere, enable form login + logout. */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**", "/api/**")) // REST endpoints use tokenless auth
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login").permitAll()
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            Map<String, Object> body = new HashMap<>();
                            body.put("message", "Login successful");
                            objectMapper.writeValue(response.getWriter(), body);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            Map<String, String> body = new HashMap<>();
                            if (exception instanceof UsernameNotFoundException) {
                                body.put("error", "Username does not exist");
                            } else if (exception instanceof BadCredentialsException) {
                                body.put("error", "Incorrect password");
                            } else {
                                body.put("error", "Login failed");
                            }
                            objectMapper.writeValue(response.getWriter(), body);
                        }))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")                        // POST /auth/logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
}
