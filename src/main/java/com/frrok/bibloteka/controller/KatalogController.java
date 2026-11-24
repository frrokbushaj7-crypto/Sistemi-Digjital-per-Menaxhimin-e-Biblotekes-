package com.frrok.bibloteka.controller;
import org.springframework.security.core.Authentication;

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
        public String shfaqKatalogun(
                Model model,
                Authentication authentication
        ) {
            model.addAttribute(
                    "librat",
                    libriRepository.gjejTeGjithe()
            );

            model.addAttribute(
                    "email",
                    authentication == null
                            ? null
                            : authentication.getName()
            );

            boolean bibliotekar = authentication != null
                    && authentication.getAuthorities().stream()
                            .anyMatch(autoritet ->
                                    "ROLE_BIBLIOTEKAR".equals(
                                            autoritet.getAuthority()
                                    )
                            );

            model.addAttribute("bibliotekar", bibliotekar);

            return "katalogu";
        }
    
        @GetMapping("/hyrja")
        public String shfaqHyrjen() {
            return "hyrja";
        }

        @GetMapping("/librat/shto")
        public String shfaqFormularinELibrit() {
            return "shto-librin";
        }
}