package com.donate4life.demo.config;

import com.donate4life.demo.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ⭐ UPDATED: Added "/verify-otp" to the list of public pages
                        .requestMatchers("/", "/register", "/login", "/style.css", "/error/**", "/verify-otp").permitAll()
                        // Protect admin routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .userDetailsService(jpaUserDetailsService)
                // Configure form-based login
                .formLogin(form -> form
                        .loginPage("/login")
                        // Redirect admin to dashboard, others to profile
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/profile");
                            }
                        })
                        .permitAll()
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}