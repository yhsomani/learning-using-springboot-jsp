package com.ruraledu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/h2-console/**",  // Only for dev, remove in production
                    "/api/public/**",  // Public APIs are read-only, no state changes
                    "/api/admin/**",   // Allow admin API calls from dashboard
                    "/login",          // Form login POST
                    "/logout",         // Logout POST
                    "/register"        // Registration POST
                )
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/", "/register", "/login", "/error", "/css/**", "/js/**", "/images/**", "/WEB-INF/jsp/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // API Documentation - secured
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN")
                
                // Actuator - secured with ADMIN role only
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Admin routes
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Teacher routes
                .requestMatchers("/teacher/**").hasAnyRole("TEACHER", "ADMIN")
                
                // Student routes
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/api/lessons/*/progress").hasRole("STUDENT")
                .requestMatchers("/api/lessons/*/complete").hasRole("STUDENT")
                .requestMatchers("/api/courses/*/quiz/submit").hasRole("STUDENT")
                
                // Parent routes
                .requestMatchers("/parent/**").hasRole("PARENT")
                
                // Payment routes - any authenticated user
                .requestMatchers("/payments/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/main/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .permitAll()
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())  // Prevent clickjacking
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:;")
                )
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // In production, replace with actual domain
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8081",
            "http://localhost:5173", 
            "http://localhost:5174", 
            "http://localhost:5175"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-XSRF-TOKEN"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "X-XSRF-TOKEN",
            "Authorization"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);  // Cache preflight for 1 hour
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);  // Stronger cost factor
    }
}
