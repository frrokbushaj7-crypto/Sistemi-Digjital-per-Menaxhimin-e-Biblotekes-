package com.frrok.bibloteka.model;

import java.time.LocalDateTime;

public record Rezervimi(
        Integer rezervimiId,
        Integer lexuesiId,
        Integer libriId,
        Integer kopjeId,
        String statusi,
        LocalDateTime dataKerkeses,
        LocalDateTime afatiMarrjes
) {
}