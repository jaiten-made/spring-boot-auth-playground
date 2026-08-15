package com.springboot.auth.config;

import com.springboot.auth.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. Define PasswordEncoder bean (using BCrypt for strong industry standard hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Expose AuthenticationManager bean so AuthController can authenticate requests
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 3. Configure SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (safe for stateless REST APIs using JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Disable Frame Options so that the H2 console (which uses iframes) can load
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            
            // Set session management to stateless (no JSESSIONID cookie generated/used)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // Allow public access to home page, console UI, login/register endpoints, public endpoint, and H2 console
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/", "/index.html", "/api/auth/login", "/api/auth/register", "/api/public", "/css/**", "/js/**").permitAll()
                // All other requests (such as /api/secured) require authentication
                .anyRequest().authenticated()
            )
            
            // Add our custom JWT filter before the standard username-password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
