package com.frrok.bibloteka.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
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
}