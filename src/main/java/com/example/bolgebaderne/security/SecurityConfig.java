package com.example.bolgebaderne.security;

import com.example.bolgebaderne.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
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
                // Vi bygger API → gør livet nemmere mht. CSRF
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**") // ignorér CSRF for H2
                        .disable()
                )
//        http
//                .csrf(csrf -> csrf.disable())
                //               .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()  // <- MIDLERIDTIGT: alt er åbent
//                )
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/api/public/**", "/api/auth/**", "/h2-console/**").permitAll()
                      .requestMatchers("/api/member/**").hasAnyRole("MEMBER","NON_MEMBER", "ADMIN")
                       .anyRequest().authenticated()
               )
                .httpBasic(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
     return NoOpPasswordEncoder.getInstance(); // KUN til test!
        // return new BCryptPasswordEncoder();
    }



    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity // så du kan bruge @PreAuthorize senere, hvis du vil
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Vi bygger primært et JSON-API → CSRF slås fra for at gøre livet nemmere
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//                        // Offentlige endpoints (ingen login kræves)
//                        .requestMatchers("/api/public/**").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/h2-console/**").permitAll()
//
//                        // Medlems-API: kræver rolle MEMBER eller ADMIN
//                        .requestMatchers("/api/member/**")
//                        .hasAnyRole("MEMBER", "ADMIN")
//
//                        // Alt andet kræver bare at man er logget ind
//                        .anyRequest().authenticated()
//                )
//
//                // HTTP Basic Authentication (browser/ Postman popup)
//                .httpBasic(Customizer.withDefaults());
//
//        // H2-console bruger frames → tillad samme origin
//        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//
//  }
//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepository) {
//        return email -> userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//    }
//
//
//}
