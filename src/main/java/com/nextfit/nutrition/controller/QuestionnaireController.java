package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.service.QuestionnaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST pour le questionnaire alimentaire.
 * POST /api/utilisateurs/{id}/questionnaire  → soumettre/mettre à jour le questionnaire
 * GET  /api/utilisateurs/{id}/questionnaire  → consulter le questionnaire actuel
 */
@RestController
@RequestMapping("/api/utilisateurs/{utilisateurId}/questionnaire")
@CrossOrigin(origins = "*")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @GetMapping
    public ResponseEntity<QuestionnaireAlimentaire> consulter(@PathVariable Long utilisateurId) {
        return questionnaireService.trouverParUtilisateur(utilisateurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public QuestionnaireAlimentaire soumettre(@PathVariable Long utilisateurId,
                                               @RequestBody QuestionnaireAlimentaire questionnaire) {
        return questionnaireService.sauvegarderQuestionnaire(utilisateurId, questionnaire);
    }
}
