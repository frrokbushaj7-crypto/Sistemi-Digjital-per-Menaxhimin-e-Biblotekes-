package com.frrok.bibloteka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            JdbcTemplate jdbcTemplate
    ) {
        return email -> {
            String sql = """
                    SELECT email, fjalekalimi_hash,
                           roli, email_verifikuar
                    FROM perdoruesit
                    WHERE email = ?
                    """;

            return jdbcTemplate.query(sql, (rs, rowNum) ->
                    User.withUsername(rs.getString("email"))
                            .password(rs.getString("fjalekalimi_hash"))
                            .roles(rs.getString("roli"))
                            .disabled(
                                    !rs.getBoolean("email_verifikuar")
                            )
                            .build(),
                    email.strip()
            ).stream().findFirst().orElseThrow(
                    () -> new UsernameNotFoundException(
                            "Kredenciale të pavlefshme."
                    )
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/librat",
                        "/api/librat/**",
                        "/api/csrf"
                ).permitAll()

                .requestMatchers(
                        "/api/librat",
                        "/api/librat/**"
                ).hasRole("BIBLIOTEKAR")

                .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}