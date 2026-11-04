package com.frrok.bibloteka.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LibriRequest(
        @NotBlank(message = "Titulli është i detyrueshëm.")
        @Size(max = 255, message = "Titulli lejon deri në 255 karaktere.")
        String titulli,

        @Size(max = 10000, message = "Përmbledhja është shumë e gjatë.")
        String permbledhja,

        @NotBlank(message = "Gjuha është e detyrueshme.")
        @Size(max = 50, message = "Gjuha lejon deri në 50 karaktere.")
        String gjuha
) {
}