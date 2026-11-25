package com.frrok.bibloteka.model;

import java.time.LocalDateTime;

public record RezervimDetaje(
        Integer rezervimiId,
        String lexuesiEmail,
        String titulli,
        String statusi,
        LocalDateTime dataKerkeses,
        LocalDateTime afatiMarrjes
) {
}