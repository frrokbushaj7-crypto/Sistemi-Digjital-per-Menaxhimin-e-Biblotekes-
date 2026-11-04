package com.frrok.bibloteka.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frrok.bibloteka.model.RezervimRequest;
import com.frrok.bibloteka.model.Rezervimi;
import com.frrok.bibloteka.service.RezervimService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rezervimet")
public class RezervimController {

    private final RezervimService rezervimService;

    public RezervimController(RezervimService rezervimService) {
        this.rezervimService = rezervimService;
    }

    @PostMapping
    public ResponseEntity<Rezervimi> krijo(
            @Valid @RequestBody RezervimRequest kerkesa,
            Principal principal
    ) {
        Rezervimi rezervimi = rezervimService.krijo(
                kerkesa.libriId(),
                principal.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rezervimi);
    }

    @PostMapping("/{id}/mirato")
    public ResponseEntity<Rezervimi> mirato(
            @PathVariable("id") int id,
            Principal principal
    ) {
        return ResponseEntity.ok(
                rezervimService.mirato(id, principal.getName())
        );
    }
}