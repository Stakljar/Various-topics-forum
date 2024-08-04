package com.example.various_topics_forum.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.various_topics_forum.security.JwtAuthenticationFilter;
import com.example.various_topics_forum.user.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {

            @ResponseBody
            public void commence(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException authenticationException) throws IOException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(authenticationException.getMessage());
            }
        };
    }

    @Bean
    LogoutHandler logoutHandler() {
        return new LogoutHandler() {

            @Override
            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                Cookie cookie = new Cookie("refresh_token", null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
    		.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(req ->
                req.requestMatchers("/api/v*/auth/**", "/error").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/v*/discussions/**").hasAnyRole(Role.ADMIN.name(), Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/v*/comments/**").hasAnyRole(Role.ADMIN.name(), Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.POST, "/api/v*/discussions/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.POST, "/api/v*/comments/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v*/discussions/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v*/comments/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.GET, "/api/v*/discussions/with_user_votes/**", "/api/v*/discussions/user/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.GET, "/api/v*/comments/with_user_votes/**", "/api/v*/comments/user/**").hasRole(Role.REGULAR_USER.name())
                    .requestMatchers(HttpMethod.GET, "/api/v*/discussions/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v*/comments/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v*/users/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout.logoutUrl("/api/v1/auth/signout")
                    .addLogoutHandler(logoutHandler())
                    .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
            .build();
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
