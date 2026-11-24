package com.frrok.bibloteka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.frrok.bibloteka.repository.LibriRepository;

@Controller
public class KatalogController {

    private final LibriRepository libriRepository;

    public KatalogController(LibriRepository libriRepository) {
        this.libriRepository = libriRepository;
    }

    @GetMapping({"/", "/katalogu"})
    public String shfaqKatalogun(Model model) {
        model.addAttribute(
                "librat",
                libriRepository.gjejTeGjithe()
        );

        return "katalogu";
    }
}