package com.eatlens.app.configuration;


import com.eatlens.app.security.JwtAuthenticationEntryPoint;
import com.eatlens.app.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // User endpoints - PUBLIC
                        .requestMatchers("/api/users/check-email").permitAll()
                        // Health check - PUBLIC
                        .requestMatchers("/api/health").permitAll()
                        // Restaurant endpoints - PUBLIC (GET operations)
                        .requestMatchers(HttpMethod.GET, "/api/restaurants", "/api/restaurants/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/search", "/api/restaurants/nearby").permitAll()

                        // Menu endpoints - PUBLIC (GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/menu/restaurants/*/items",
                                "/api/menu/restaurants/*/categories",
                                "/api/menu/categories/*/items",
                                "/api/menu/items/*",
                                "/api/menu/restaurants/*/popular").permitAll()

                        // Review endpoints - PUBLIC (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/restaurants/*").permitAll()

                        // AI Analysis - PUBLIC (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/ai-analysis/restaurants/*").permitAll()

                        // Restaurant owner endpoints - AUTHENTICATED
                        .requestMatchers(HttpMethod.POST, "/api/restaurants").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/restaurants/*").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/*").hasRole("RESTAURANT_OWNER")
                        .requestMatchers("/api/restaurants/my-restaurants").hasRole("RESTAURANT_OWNER")

                        // Menu management - RESTAURANT_OWNER only
                        .requestMatchers(HttpMethod.POST, "/api/menu/**").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/menu/**").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/menu/**").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/menu/**").hasRole("RESTAURANT_OWNER")

                        // Review endpoints - CUSTOMER
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/*").hasRole("CUSTOMER")

                        // Review owner response - RESTAURANT_OWNER
                        .requestMatchers(HttpMethod.POST, "/api/reviews/*/owner-response").hasRole("RESTAURANT_OWNER")

                        // User profile endpoints - AUTHENTICATED
                        .requestMatchers("/api/users/profile", "/api/users/change-password", "/api/users/account").authenticated()

                        // My reviews - AUTHENTICATED
                        .requestMatchers("/api/reviews/my-reviews").authenticated()

                        // Any other request needs authentication
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}