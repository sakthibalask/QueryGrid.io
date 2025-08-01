package com.application.QueryGrid.io.Configuration;

import com.application.QueryGrid.io.Entity.UserAuth.Role;
import com.application.QueryGrid.io.Utils.Constants;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter JwtAuthFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(Constants.WHITELIST_URL).permitAll()
                        .requestMatchers("/app/v1/users/**").hasAnyRole(
                                Role.ADMINISTRATOR.name(),
                                Role.SUPERUSER.name(),
                                Role.COORDINATOR.name(),
                                Role.USER.name()
                        )
                        .requestMatchers("/app/v1/superusers/**").hasAnyRole(
                                Role.ADMINISTRATOR.name(),
                                Role.SUPERUSER.name()
                        )
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
