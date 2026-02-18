package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.PlanRepas;
import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.service.PlanRepasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST pour les plans de repas personnalisés.
 */
@RestController
@RequestMapping("/api/plans-repas")
@CrossOrigin(origins = "*")
public class PlanRepasController {

    private final PlanRepasService planRepasService;

    public PlanRepasController(PlanRepasService planRepasService) {
        this.planRepasService = planRepasService;
    }

    /** Liste tous les plans disponibles */
    @GetMapping
    public List<PlanRepas> listerTous() {
        return planRepasService.listerTousLesPlans();
    }

    /** Plan par identifiant */
    @GetMapping("/{id}")
    public ResponseEntity<PlanRepas> trouverParId(@PathVariable Long id) {
        return planRepasService.trouverParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Plans filtrés par objectif */
    @GetMapping("/objectif/{objectif}")
    public List<PlanRepas> parObjectif(
            @PathVariable QuestionnaireAlimentaire.ObjectifSportif objectif) {
        return planRepasService.listerParObjectif(objectif);
    }

    /**
     * Suggère le meilleur plan pour un utilisateur selon son questionnaire.
     * GET /api/plans-repas/suggestion?utilisateurId=1
     */
    @GetMapping("/suggestion")
    public ResponseEntity<PlanRepas> suggestion(@RequestParam Long utilisateurId) {
        return planRepasService.suggererPlanPourUtilisateur(utilisateurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
