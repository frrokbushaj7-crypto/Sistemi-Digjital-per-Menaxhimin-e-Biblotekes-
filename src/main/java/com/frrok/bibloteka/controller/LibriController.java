package com.frrok.bibloteka.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frrok.bibloteka.model.Libri;
import com.frrok.bibloteka.model.LibriRequest;
import com.frrok.bibloteka.repository.LibriRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/librat")
public class LibriController {

    private final LibriRepository libriRepository;

    public LibriController(LibriRepository libriRepository) {
        this.libriRepository = libriRepository;
    }

    @GetMapping
    public List<Libri> listoLibrat() {
        return libriRepository.gjejTeGjithe();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libri> gjejLibrin(
            @PathVariable("id") int id
    ) {
        return ResponseEntity.of(
                libriRepository.gjejSipasId(id)
        );
    }

    @PostMapping
    public ResponseEntity<Libri> shtoLibrin(
            @Valid @RequestBody LibriRequest kerkesa
    ) {
        Libri libri = libriRepository.shto(
                kerkesa.titulli().strip(),
                kerkesa.permbledhja(),
                kerkesa.gjuha().strip()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(libri);
    }
}