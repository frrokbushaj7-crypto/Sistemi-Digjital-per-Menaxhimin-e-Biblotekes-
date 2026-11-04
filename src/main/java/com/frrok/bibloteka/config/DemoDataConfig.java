package com.frrok.bibloteka.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    public CommandLineRunner krijoLlogariteDemo(
            JdbcTemplate jdbc,
            PasswordEncoder encoder,
            Environment environment
    ) {
        return args -> {
            krijoPerdoruesin(
                    jdbc, encoder,
                    "Bibliotekar", "bibliotekar@demo.test",
                    "BIBLIOTEKAR",
                    environment.getRequiredProperty(
                            "DEMO_LIBRARIAN_PASSWORD"
                    )
            );

            krijoPerdoruesin(
                    jdbc, encoder,
                    "Lexues", "lexues@demo.test",
                    "LEXUES",
                    environment.getRequiredProperty(
                            "DEMO_READER_PASSWORD"
                    )
            );
        };
    }

    private void krijoPerdoruesin(
            JdbcTemplate jdbc,
            PasswordEncoder encoder,
            String emri,
            String email,
            String roli,
            String fjalekalimi
    ) {
        if (fjalekalimi.length() < 8) {
            throw new IllegalArgumentException(
                    "Fjalëkalimi demo duhet të ketë të paktën 8 karaktere."
            );
        }

        String sql = """
                INSERT INTO perdoruesit (
                    emri, mbiemri, email,
                    fjalekalimi_hash, roli, email_verifikuar
                )
                VALUES (?, 'Demo', ?, ?, ?, TRUE)
                ON DUPLICATE KEY UPDATE
                    fjalekalimi_hash = VALUES(fjalekalimi_hash),
                    roli = VALUES(roli),
                    email_verifikuar = TRUE
                """;

        jdbc.update(
                sql,
                emri,
                email,
                encoder.encode(fjalekalimi),
                roli
        );
    }
}