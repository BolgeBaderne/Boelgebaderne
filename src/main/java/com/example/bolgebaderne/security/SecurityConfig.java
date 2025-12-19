package com.example.bolgebaderne.security;

import com.example.bolgebaderne.repository.UserRepository;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        // H2 console (frames)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.authorizeHttpRequests(auth -> auth

                //ALT statisk (css/js/images/webjars/favicon osv.)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // ekstra static paths I bruger (icons er ikke "common location")
                .requestMatchers("/icons/**").permitAll()
                .requestMatchers("/api/events/**").permitAll()


                //Public pages
                .requestMatchers(
                        "/", "/index.html",
                        "/login", "/error", "/membership-required",
                        "/event.html", "/about.html", "/faq.html",
                        "/membership.html", "/sauna-info.html"

                ).permitAll()

                //Public API / system endpoints
                .requestMatchers(
                        "/api/public/**",
                        "/api/auth/**",
                        "/h2-console/**",
                        "/api/timeslots/**",
                        "/api/timeslots/*/waitlist/**"
                ).permitAll()

                //Medlemssider / medlem API
                .requestMatchers("/member/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/api/member/**").hasAnyRole("MEMBER", "ADMIN")

                //Alt andet kræver login
                .anyRequest().authenticated()
        );

        // FORM LOGIN til browseren (single, correct configuration)
        http.formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard.html", true)
                .failureUrl("/login?error")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
        );

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
    }
}
