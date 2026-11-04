package com.frrok.bibloteka.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RezervimRequest(
        @NotNull
        @Positive
        Integer libriId
) {
}
