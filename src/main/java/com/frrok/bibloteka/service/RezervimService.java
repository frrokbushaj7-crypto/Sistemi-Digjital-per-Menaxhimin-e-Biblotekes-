package com.frrok.bibloteka.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.frrok.bibloteka.model.Rezervimi;

@Service
public class RezervimService {

    private final JdbcTemplate jdbc;

    public RezervimService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Metoda 1: paraqitja e kerkeses nga lexuesi.
    @Transactional
    public Rezervimi krijo(int libriId, String email) {

        Integer lexuesiId = jdbc.query(
                """
                SELECT perdoruesi_id
                FROM perdoruesit
                WHERE email = ? AND roli = 'LEXUES'
                """,
                (rs, rowNum) -> rs.getInt("perdoruesi_id"),
                email
        ).stream().findFirst().orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Vetëm lexuesi mund të paraqesë kërkesë."
                )
        );

        var libri = jdbc.query(
                """
                SELECT libri_id
                FROM librat
                WHERE libri_id = ?
                FOR UPDATE
                """,
                (rs, rowNum) -> rs.getInt("libri_id"),
                libriId
        );

        if (libri.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Libri nuk ekziston."
            );
        }

        Integer abonime = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM abonimet
                WHERE lexuesi_id = ?
                  AND statusi = 'AKTIV'
                  AND data_fillimit <= CURRENT_TIMESTAMP
                  AND data_perfundimit > CURRENT_TIMESTAMP
                """,
                Integer.class,
                lexuesiId
        );

        if (abonime == null || abonime == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Nevojitet abonim aktiv."
            );
        }

        Integer kerkesa = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM rezervimet
                WHERE lexuesi_id = ?
                  AND libri_id = ?
                  AND statusi IN ('NE_PRITJE', 'MIRATUAR')
                """,
                Integer.class,
                lexuesiId,
                libriId
        );

        if (kerkesa != null && kerkesa > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ke një rezervim të hapur për këtë libër."
            );
        }

        LocalDateTime tani = LocalDateTime.now().withNano(0);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                    INSERT INTO rezervimet (
                        lexuesi_id, libri_id, statusi, data_kerkeses
                    )
                    VALUES (?, ?, 'NE_PRITJE', ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, lexuesiId);
            statement.setInt(2, libriId);
            statement.setTimestamp(3, Timestamp.valueOf(tani));

            return statement;
        }, keyHolder);

        Number id = keyHolder.getKey();

        if (id == null) {
            throw new IllegalStateException(
                    "ID-ja e rezervimit nuk u gjenerua."
            );
        }

        return new Rezervimi(
                id.intValue(),
                lexuesiId,
                libriId,
                null,
                "NE_PRITJE",
                tani,
                null
        );
    }

    // Metoda 2: miratimi nga bibliotekari.
    @Transactional
    public Rezervimi mirato(int rezervimiId, String email) {

        Integer bibliotekariId = jdbc.query(
                """
                SELECT perdoruesi_id
                FROM perdoruesit
                WHERE email = ? AND roli = 'BIBLIOTEKAR'
                """,
                (rs, rowNum) -> rs.getInt("perdoruesi_id"),
                email
        ).stream().findFirst().orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Vetëm bibliotekari mund të miratojë."
                )
        );

        Rezervimi rezervimi = jdbc.query(
                """
                SELECT rezervimi_id, lexuesi_id, libri_id,
                       kopje_id, statusi, data_kerkeses, afati_marrjes
                FROM rezervimet
                WHERE rezervimi_id = ?
                FOR UPDATE
                """,
                (rs, rowNum) -> {
                    Timestamp afati = rs.getTimestamp("afati_marrjes");

                    return new Rezervimi(
                            rs.getInt("rezervimi_id"),
                            rs.getInt("lexuesi_id"),
                            rs.getInt("libri_id"),
                            rs.getObject("kopje_id", Integer.class),
                            rs.getString("statusi"),
                            rs.getTimestamp("data_kerkeses")
                                    .toLocalDateTime(),
                            afati == null ? null : afati.toLocalDateTime()
                    );
                },
                rezervimiId
        ).stream().findFirst().orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rezervimi nuk ekziston."
                )
        );

        if (!"NE_PRITJE".equals(rezervimi.statusi())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Rezervimi nuk është në pritje."
            );
        }

        Integer abonime = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM abonimet
                WHERE lexuesi_id = ?
                  AND statusi = 'AKTIV'
                  AND data_fillimit <= CURRENT_TIMESTAMP
                  AND data_perfundimit > CURRENT_TIMESTAMP
                """,
                Integer.class,
                rezervimi.lexuesiId()
        );

        if (abonime == null || abonime == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lexuesi nuk ka më abonim aktiv."
            );
        }

        Integer kopjeId = jdbc.query(
                """
                SELECT kopje_id
                FROM kopjet
                WHERE libri_id = ?
                  AND gjendja = 'E_DISPONUESHME'
                  AND destinimi = 'HUAZIM'
                ORDER BY kopje_id
                LIMIT 1
                FOR UPDATE
                """,
                (rs, rowNum) -> rs.getInt("kopje_id"),
                rezervimi.libriId()
        ).stream().findFirst().orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Nuk ka kopje të disponueshme për huazim."
                )
        );

        LocalDateTime tani = LocalDateTime.now().withNano(0);
        LocalDateTime afati = tani.plusHours(48);

        int kopjeTeNdryshuara = jdbc.update(
                """
                UPDATE kopjet
                SET gjendja = 'E_REZERVUAR'
                WHERE kopje_id = ?
                  AND gjendja = 'E_DISPONUESHME'
                  AND destinimi = 'HUAZIM'
                """,
                kopjeId
        );

        if (kopjeTeNdryshuara != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kopja nuk është më e disponueshme."
            );
        }

        int rezervimeTeNdryshuara = jdbc.update(
                """
                UPDATE rezervimet
                SET kopje_id = ?,
                    statusi = 'MIRATUAR',
                    data_miratimit = ?,
                    afati_marrjes = ?,
                    miratuar_nga = ?
                WHERE rezervimi_id = ?
                  AND statusi = 'NE_PRITJE'
                """,
                kopjeId,
                Timestamp.valueOf(tani),
                Timestamp.valueOf(afati),
                bibliotekariId,
                rezervimiId
        );

        if (rezervimeTeNdryshuara != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Rezervimi nuk mund të miratohet."
            );
        }

        return new Rezervimi(
                rezervimi.rezervimiId(),
                rezervimi.lexuesiId(),
                rezervimi.libriId(),
                kopjeId,
                "MIRATUAR",
                rezervimi.dataKerkeses(),
                afati
        );
    }
}