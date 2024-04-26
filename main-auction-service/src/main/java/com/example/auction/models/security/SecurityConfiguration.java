package com.example.auction.models.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(session -> session.sessionCreationPolicy(NEVER))
        .authorizeHttpRequests(auth ->
            auth.requestMatchers("/login**").permitAll()
                .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(login ->
            login
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login/perform_login").permitAll()
                .failureUrl("login?error=true")
                .defaultSuccessUrl("/"))
        .logout(logout ->
            logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .clearAuthentication(true))
        .build();
  }
}
