package com.frrok.bibloteka.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RezervimSkadimi {

    private final JdbcTemplate jdbc;

    public RezervimSkadimi(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Scheduled(fixedDelay = 60000)
    public void skadoRezervimet() {
        jdbc.update("""
                UPDATE rezervimet r
                JOIN kopjet k ON k.kopje_id = r.kopje_id
                SET r.statusi = 'SKADUAR',
                    k.gjendja = CASE
                        WHEN k.gjendja = 'E_REZERVUAR'
                        THEN 'E_DISPONUESHME'
                        ELSE k.gjendja
                    END
                WHERE r.statusi = 'MIRATUAR'
                  AND r.afati_marrjes <= CURRENT_TIMESTAMP
                """);
    }
}