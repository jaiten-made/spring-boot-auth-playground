package com.springboot.playground.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String secretString;

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

    // 3. Define JwtDecoder bean using HMAC secret key
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(
                secretString.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    // 4. Configure SecurityFilterChain
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
                .requestMatchers(
                    "/", "/index.html", "/api/v1/auth/login", "/api/v1/users", "/api/v1/auth/public-data", "/css/**", "/js/**",
                    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/error"
                ).permitAll()
                // All other requests (such as /api/v1/auth/private-data) require authentication
                .anyRequest().authenticated()
            )
            
            // Enable built-in OAuth2 JWT resource server support
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
