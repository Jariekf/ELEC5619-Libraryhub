package com.example.libraryhub.config;

import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (String loginIdentifier) -> {
            User u = userRepository.findByUsername(loginIdentifier)
                    .or(() -> userRepository.findByEmail(loginIdentifier))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginIdentifier));
            String role = (u instanceof Admin) ? "ADMIN" : "CUSTOMER";
            UserDetails details = org.springframework.security.core.userdetails.User
                    .withUsername(u.getUsername())
                    .password(u.getPasswordHash())
                    .roles(role)
                    .disabled(!u.isActive())
                    .build();
            return details;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/auth/**",
                        "/api/**",
                        "/chatbot/**"
                ))
                .authorizeHttpRequests(reg -> reg
                        // Public endpoints
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()

                        // API - Books (public read, admin write)
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books/search").permitAll()

                        // API - Admin operations
                        .requestMatchers(HttpMethod.POST, "/api/books/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/books/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/books/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/books/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // API - User operations
                        .requestMatchers("/api/user/current").authenticated()

                        // API - Reviews
                        .requestMatchers(HttpMethod.POST, "/api/books/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()

                        // API - Reservations (allow both CUSTOMER and ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/books/*/reserve").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*/reserve").hasAnyRole("CUSTOMER", "ADMIN")

                        // API - Waiting List (allow both CUSTOMER and ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/books/*/waitinglist").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*/waitinglist").hasAnyRole("CUSTOMER", "ADMIN")

                        // API - Chatbot
                        .requestMatchers("/api/chatbot/**", "/chat", "/chatbot/**").authenticated()

                        // MVC endpoints (Thymeleaf)
                        .requestMatchers("/books", "/books/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/books/*/reserve").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/books/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.POST, "/books/*/reviews/*/delete").authenticated()
                        .requestMatchers(HttpMethod.POST, "/admin/books/*/reviews/*/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/books/admin/edit/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/books/admin/edit/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/books/admin/delete/*").hasRole("ADMIN")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login").permitAll()
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/books", true)
                        .failureUrl("/auth/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }
}