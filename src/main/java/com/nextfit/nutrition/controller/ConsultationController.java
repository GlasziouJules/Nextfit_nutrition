package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.ConsultationDieteticien;
import com.nextfit.nutrition.service.ConsultationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST pour les consultations diététicien en ligne.
 * Les membres VIP ont leurs consultations incluses dans l'abonnement.
 */
@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    /**
     * Réserve une consultation.
     * POST /api/consultations
     * Body: { "utilisateurId": 1, "dateHeure": "2024-04-15T10:00:00", "dureeMins": 30 }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationDieteticien reserver(@RequestBody Map<String, Object> body) {
        Long utilisateurId = Long.valueOf(body.get("utilisateurId").toString());
        LocalDateTime dateHeure = LocalDateTime.parse(body.get("dateHeure").toString());
        int dureeMins = body.containsKey("dureeMins")
                ? Integer.parseInt(body.get("dureeMins").toString()) : 30;

        return consultationService.reserverConsultation(utilisateurId, dateHeure, dureeMins);
    }

    /**
     * Consultations d'un utilisateur.
     * GET /api/consultations?utilisateurId=1
     */
    @GetMapping
    public List<ConsultationDieteticien> listerParUtilisateur(@RequestParam Long utilisateurId) {
        return consultationService.consultationsUtilisateur(utilisateurId);
    }

    /**
     * Prochaines consultations d'un utilisateur.
     * GET /api/consultations/prochaines?utilisateurId=1
     */
    @GetMapping("/prochaines")
    public List<ConsultationDieteticien> prochaines(@RequestParam Long utilisateurId) {
        return consultationService.prochainesConsultations(utilisateurId);
    }

    /**
     * Détail d'une consultation.
     * GET /api/consultations/42
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDieteticien> trouverParId(@PathVariable Long id) {
        return consultationService.trouverParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Annule une consultation.
     * DELETE /api/consultations/42
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ConsultationDieteticien> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.annulerConsultation(id));
    }
}
