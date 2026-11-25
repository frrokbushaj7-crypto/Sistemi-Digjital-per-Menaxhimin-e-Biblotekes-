package com.frrok.bibloteka.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.frrok.bibloteka.service.RezervimService;

@Controller
public class RezervimWebController {

    private final RezervimService rezervimService;

    public RezervimWebController(RezervimService rezervimService) {
        this.rezervimService = rezervimService;
    }

    @GetMapping("/bibliotekari/rezervimet")
    public String shfaqRezervimet(
            Model model,
            Principal principal
    ) {
        model.addAttribute(
                "rezervimet",
                rezervimService.listoPerBibliotekarin(
                        principal.getName()
                )
        );

        return "rezervimet-bibliotekari";
    }
    @GetMapping("/lexuesi/rezervimet")
public String shfaqRezervimetELexuesit(
        Model model,
        Principal principal
) {
    model.addAttribute(
            "rezervimet",
            rezervimService.listoPerLexuesin(
                    principal.getName()
            )
    );

    return "rezervimet-lexuesi";
}
}