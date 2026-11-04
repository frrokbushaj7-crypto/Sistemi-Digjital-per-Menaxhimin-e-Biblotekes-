package com.frrok.bibloteka.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.frrok.bibloteka.model.Libri;

@Repository
public class LibriRepository {

    private final JdbcTemplate jdbcTemplate;

    public LibriRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Libri> gjejTeGjithe() {
        String sql = """
                SELECT libri_id, titulli, permbledhja, gjuha
                FROM librat
                ORDER BY libri_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Libri(
                        rs.getInt("libri_id"),
                        rs.getString("titulli"),
                        rs.getString("permbledhja"),
                        rs.getString("gjuha")
                )
        );
    }

    public Optional<Libri> gjejSipasId(int id) {
        String sql = """
                SELECT libri_id, titulli, permbledhja, gjuha
                FROM librat
                WHERE libri_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Libri(
                        rs.getInt("libri_id"),
                        rs.getString("titulli"),
                        rs.getString("permbledhja"),
                        rs.getString("gjuha")
                ), id
        ).stream().findFirst();
    }

    public Libri shto(
            String titulli,
            String permbledhja,
            String gjuha
    ) {
        String sql = """
                INSERT INTO librat (titulli, permbledhja, gjuha)
                VALUES (?, ?, ?)
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, titulli);
            statement.setString(2, permbledhja);
            statement.setString(3, gjuha);

            return statement;
        }, keyHolder);

        Number id = keyHolder.getKey();

        if (id == null) {
            throw new IllegalStateException(
                    "ID-ja e librit nuk u gjenerua."
            );
        }

        return new Libri(id.intValue(), titulli, permbledhja, gjuha);
    }
}