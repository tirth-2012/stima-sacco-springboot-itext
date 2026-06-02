package com.rutusoft.flowable.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          AuthenticationProvider customAuthenticationProvider) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    // ✅ SINGLE AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);

        return new ProviderManager(
                daoProvider,
                customAuthenticationProvider
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //.cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/flowable-service/swagger-ui/**",
                        "/flowable-service/v3/api-docs/**",
                        "/flowable-service/webjars/**",
                        "/users/authenticate",
                        "/draft/**"
                ).permitAll()
                .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .addFilterBefore(
                        new JWTAuthorizationFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 🌍 CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(  // 👈 use patterns, not origins
                "http://192.168.1.9:5173",
                "http://localhost:5173",
                "https://localhost",
                "http://localhost:8098/",
                "https://102.37.105.250",
                "http://102.37.105.250",
                "https://nlsbanking.com",
                "http://192.168.1.42:5173",
                "*"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));  // 👈 expose JWT header to browser
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
