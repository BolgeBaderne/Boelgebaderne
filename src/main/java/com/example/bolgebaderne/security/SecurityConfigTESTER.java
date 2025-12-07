/////*  -------------------------------------
////package com.example.bolgebaderne.security;
//// */
////
////import com.example.bolgebaderne.repository.UserRepository;
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.Customizer;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.core.userdetails.UserDetailsService;
////import org.springframework.security.core.userdetails.UsernameNotFoundException;
////import org.springframework.security.crypto.password.NoOpPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
//
//
///*  -------------------------------------  BRUGBART FRA HER
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity //ift @preauthorize
//public class SecurityConfigTESTER {
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Vi bygger API → gør livet nemmere mht. CSRF
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/h2-console/**") // ignorér CSRF for H2
//                        .disable()
//                )
////        http
////                .csrf(csrf -> csrf.disable())
//                //               .authorizeHttpRequests(auth -> auth
////                        .anyRequest().permitAll()  // <- MIDLERIDTIGT: alt er åbent
////                )
//
//                //adgang
//               .authorizeHttpRequests(auth -> auth
//                       .requestMatchers("/api/public/**", "/api/auth/**", "/h2-console/**").permitAll()
//                      .requestMatchers("/api/member/**").hasAnyRole("MEMBER","NON_MEMBER", "ADMIN")
//
//                       // MEDLEMS-API → kræver rolle MEMBER eller ADMIN
//                       .requestMatchers("/api/member/**")
//                       .hasAnyRole("MEMBER", "ADMIN")
//                       .anyRequest().authenticated()
//
//               )
//                .httpBasic(Customizer.withDefaults())
//
//                // ⭐ GLOBAL SECURITY ERROR HANDLING
//                // 🔥 Her håndterer vi 401 + 403 som JSON
//                .exceptionHandling(ex -> ex
//
//                        // 401 – ikke logget ind / forkert login
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
//                            response.setContentType("application/json");
//                            response.getWriter().write("""
//                                {
//                                  "status": 401,
//                                  "error": "UNAUTHORIZED",
//                                  "message": "Du skal være logget ind for at se denne side."
//                                }
//                                """);
//                        })
//
//                        // 403 – logget ind, men mangler rolle
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
//                            response.setContentType("application/json");
//                            response.getWriter().write("""
//                                {
//                                  "status": 403,
//                                  "error": "FORBIDDEN",
//                                  "message": "Du har ikke adgang til denne funktion."
//                                }
//                                """);
//                        })
//                );
//
//
//
//        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));   // H2-console i frames
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//     return NoOpPasswordEncoder.getInstance(); // KUN til test!
//        // return new BCryptPasswordEncoder();
//    }
//
//
//
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        // Vi logger ind med EMAIL
//        return email -> userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//    }
//}
//------------------------------ /* BRUGBART SLUT HER
////@Configuration
////@EnableWebSecurity
////@EnableMethodSecurity // så du kan bruge @PreAuthorize senere, hvis du vil
////public class SecurityConfig {
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                // Vi bygger primært et JSON-API → CSRF slås fra for at gøre livet nemmere
////                .csrf(csrf -> csrf.disable())
////
////                .authorizeHttpRequests(auth -> auth
////                        // Offentlige endpoints (ingen login kræves)
////                        .requestMatchers("/api/public/**").permitAll()
////                        .requestMatchers("/api/auth/**").permitAll()
////                        .requestMatchers("/h2-console/**").permitAll()
////
////                        // Medlems-API: kræver rolle MEMBER eller ADMIN
////                        .requestMatchers("/api/member/**")
////                        .hasAnyRole("MEMBER", "ADMIN")
////
////                        // Alt andet kræver bare at man er logget ind
////                        .anyRequest().authenticated()
////                )
////
////                // HTTP Basic Authentication (browser/ Postman popup)
////                .httpBasic(Customizer.withDefaults());
////
////        // H2-console bruger frames → tillad samme origin
////        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
////
////        return http.build();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////
////  }
////    @Bean
////    public UserDetailsService userDetailsService(UserRepository userRepository) {
////        return email -> userRepository.findByEmail(email)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
////    }
////
////
////}
