package com.application.QueryGrid.Configuration;

import com.application.QueryGrid.Entity.UserAuth.Role;
import com.application.QueryGrid.Utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter JwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(Constants.WHITELIST_URL).permitAll()
                        .requestMatchers("/app/v1/users/**").hasAnyRole(
                                Role.ADMINISTRATOR.name(),
                                Role.SUPERUSER.name(),
                                Role.USER.name()
                        )
                        .requestMatchers("/app/v1/coordinators/**").hasAnyRole(
                                Role.ADMINISTRATOR.name(),
                                Role.SUPERUSER.name(),
                                Role.COORDINATOR.name()
                        )
                        .requestMatchers("/app/v1/admins/**").hasAnyRole(
                                Role.ADMINISTRATOR.name()
                        )
                        .requestMatchers("/app/v1/configuration/**").hasAnyRole(
                                Role.ADMINISTRATOR.name(),
                                Role.SUPERUSER.name()
                        )
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // allow cookies/auth headers if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
