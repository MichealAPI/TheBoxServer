package it.mikeslab.thebox.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());

        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // todo: educational purposes only, do not use in production
                // Exclude static resources from security filters
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**", "/js/**", "/images/**", "/components/**", "/css/**").permitAll() // Permit all access to static resources
                        .requestMatchers("/", "/register", "/api/register").permitAll()
                        .anyRequest().authenticated() // All other requests require authentication
                )
                // Configure custom login page
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .usernameParameter("email")
                        .loginProcessingUrl("/auth") // Endpoint for login form submission
                        .defaultSuccessUrl("/courses", true) // Redirect to courses after successful login
                        .permitAll() // Allow everyone to access the login page and submission URL
                )
                // Configure logout behavior
                .logout(logout -> logout
                        .logoutUrl("/logout") // Specify a custom logout URL
                        .logoutSuccessUrl("/") // Redirect to home after logout
                        .permitAll() // Allow everyone to access logout
                )
                // Exception handling configuration
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied") // Custom access denied page
                )
                // CSRF default handling (can be customized further)
                //.csrf(csrf -> csrf
                //        .ignoringRequestMatchers("/auth") // Ignore CSRF for login endpoint
                //)
                // Add default CORS configuration (can also be externalized)
                .cors(AbstractHttpConfigurer::disable); // Customizer.withDefaults() // todo Enable default CORS configuration

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
