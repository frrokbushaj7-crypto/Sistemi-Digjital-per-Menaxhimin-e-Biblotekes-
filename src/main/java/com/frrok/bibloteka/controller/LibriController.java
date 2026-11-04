package com.frrok.bibloteka.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frrok.bibloteka.model.Libri;
import com.frrok.bibloteka.repository.LibriRepository;

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
}