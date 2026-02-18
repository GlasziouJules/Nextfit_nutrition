package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.EntreeMacro;
import com.nextfit.nutrition.service.MacroTrackingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST pour le suivi des macronutriments.
 * Permet l'enregistrement des repas et la consultation des bilans journaliers.
 */
@RestController
@RequestMapping("/api/utilisateurs/{utilisateurId}/macros")
@CrossOrigin(origins = "*")
public class MacroTrackingController {

    private final MacroTrackingService macroTrackingService;

    public MacroTrackingController(MacroTrackingService macroTrackingService) {
        this.macroTrackingService = macroTrackingService;
    }

    /**
     * Enregistre un aliment/repas consommé.
     * POST /api/utilisateurs/1/macros
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntreeMacro enregistrer(@PathVariable Long utilisateurId,
                                    @RequestBody EntreeMacro entree) {
        return macroTrackingService.enregistrerEntree(utilisateurId, entree);
    }

    /**
     * Liste les entrées du jour (ou d'une date donnée).
     * GET /api/utilisateurs/1/macros?date=2024-03-15
     */
    @GetMapping
    public List<EntreeMacro> listerParJour(
            @PathVariable Long utilisateurId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return macroTrackingService.entreesParJour(utilisateurId,
                date != null ? date : LocalDate.now());
    }

    /**
     * Résumé nutritionnel journalier avec progression par rapport aux objectifs.
     * GET /api/utilisateurs/1/macros/resume?date=2024-03-15
     */
    @GetMapping("/resume")
    public Map<String, Object> resumeJournee(
            @PathVariable Long utilisateurId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return macroTrackingService.resumeJournee(utilisateurId,
                date != null ? date : LocalDate.now());
    }

    /**
     * Historique sur une période.
     * GET /api/utilisateurs/1/macros/historique?debut=2024-03-01&fin=2024-03-31
     */
    @GetMapping("/historique")
    public List<EntreeMacro> historique(
            @PathVariable Long utilisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return macroTrackingService.entreesParPeriode(utilisateurId, debut, fin);
    }

    /**
     * Supprime une entrée.
     * DELETE /api/utilisateurs/1/macros/42
     */
    @DeleteMapping("/{entreeId}")
    public ResponseEntity<Void> supprimer(@PathVariable Long utilisateurId,
                                           @PathVariable Long entreeId) {
        macroTrackingService.supprimerEntree(entreeId);
        return ResponseEntity.noContent().build();
    }
}
