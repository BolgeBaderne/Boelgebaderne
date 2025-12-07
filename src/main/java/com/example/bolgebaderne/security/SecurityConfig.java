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

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // login + public endpoints må være åbne
                        .requestMatchers(
                                "/login",
                                "/api/public/**",
                                "/api/auth/**",
                                "/h2-console/**"
                        ).permitAll()

                        // FRONTEND-medlemssider:
                        .requestMatchers("/member/**").hasAnyRole("MEMBER", "ADMIN")

                        // kun MEMBER/ADMIN på member-API
                        .requestMatchers("/api/member/**")
                        .hasAnyRole("MEMBER", "ADMIN")

                        // alt andet kræver login
                        .anyRequest().authenticated()
                )

                // FORM LOGIN til browseren
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")      // <- vigtig!
                        .passwordParameter("password")   // valgfri, men fint
                        .defaultSuccessUrl("/api/member/profile", true)
                        .permitAll()
                )

                // LOGOUT (bare nice to have)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );

        // H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
    @Bean
  public PasswordEncoder passwordEncoder() {
      // simpelt til udvikling/eksamen – plaintext passwords
      return NoOpPasswordEncoder.getInstance();
   }

    @Bean
   public UserDetailsService userDetailsService(UserRepository userRepository) {
       return email -> userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
}}

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//                        // offentlige sider + login-side
//                        .requestMatchers(
//                                "/login",
//                                "/api/public/**",
//                                "/api/auth/**",
//                                "/h2-console/**"
//                        ).permitAll()
//
//                        // /api/member/** kun for MEMBER eller ADMIN
//                        .requestMatchers("/api/member/**")
//                        .hasAnyRole("MEMBER", "ADMIN")
//
//                        // alt andet kræver bare at man er logget ind
//                        .anyRequest().authenticated()
//                )
//
//                // FORM LOGIN til browser-UI
//                .formLogin(form -> form
//                        .loginPage("/login")                // vores egen login-side
//                        .defaultSuccessUrl("/api/member/profile", true)// kan du ændre senere
//                        .permitAll()
//                )
//
//                // BASIC auth bevares, så Postman stadig fungerer
////                .httpBasic(Customizer.withDefaults())
//
//                // Custom 401/403 JSON-fejl (som vi lavede til #72)
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(401);
//                            response.setContentType("application/json");
//                            try (PrintWriter out = response.getWriter()) {
//                                out.write("""
//                                        {
//                                          "status": 401,
//                                          "error": "Unauthorized",
//                                          "message": "Du skal være logget ind for at se denne side."
//                                        }
//                                        """);
//                            }
//                        })
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.setStatus(403);
//                            response.setContentType("application/json");
//                            try (PrintWriter out = response.getWriter()) {
//                                out.write("""
//                                        {
//                                          "status": 403,
//                                          "error": "Forbidden",
//                                          "message": "Du har ikke adgang til denne funktion."
//                                        }
//                                        """);
//                            }
//                        })
//                );
//
//        // H2 console må bruge frames
//        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
//
//        return http.build();
//    }
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // simpelt til udvikling/eksamen – plaintext passwords
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        return email -> userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//    }
//}
