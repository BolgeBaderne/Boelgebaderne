package com.example.bolgebaderne.security;

import com.example.bolgebaderne.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable());

                http.authorizeHttpRequests(auth -> auth
                        // login + public endpoints må være åbne
                        .requestMatchers(
                                "/login",
                                "/api/public/**",
                                "/api/auth/**",
                                "/h2-console/**",
                                "/api/events/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()



                        // FRONTEND-medlemssider:
                        .requestMatchers("/member/**").hasAnyRole("MEMBER", "ADMIN")

                        //ADMIN API TIL EVENTS
                                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")

                                // ADMIN-FRONTEND (HTML-siden)
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                        // kun MEMBER/ADMIN på member-API
                        .requestMatchers("/api/member/**").hasAnyRole("MEMBER", "ADMIN")

                        // alt andet kræver login
                        .anyRequest().authenticated()
                )

                // FORM LOGIN til browseren
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")      // <- vigtig!
                        .passwordParameter("password")   // valgfri, men fint
//                        .defaultSuccessUrl("/api/member/profile", false)
                        .failureUrl("/login?error")         // 👈 vigtig for fejlbesked
                        .permitAll()
                )

                        // LOGOUT (bare nice to have)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // 👈 vigtig for “du er logget ud”


                        )
                        .exceptionHandling(ex -> ex
                                // Ikke logget ind → redirect til login med auth=required
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendRedirect("/login?auth=required");
                                })
                                // Logget ind men forkert rolle → membership-required (har du allerede)
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    // Brugeren er logget ind, men har ikke den rigtige rolle
                                    response.sendRedirect("/membership-required");
                                })
                        )

                        // VIGTIGT: Basic Auth til Postman / API-kald
                        .httpBasic(Customizer.withDefaults());
        // H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }}

