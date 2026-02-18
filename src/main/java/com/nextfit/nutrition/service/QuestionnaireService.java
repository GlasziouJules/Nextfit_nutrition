package com.nextfit.nutrition.service;

import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.QuestionnaireRepository;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service gérant le questionnaire alimentaire des membres.
 * Calcule automatiquement les calories cibles (formule de Harris-Benedict).
 */
@Service
@Transactional
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepo;
    private final UtilisateurRepository utilisateurRepo;

    public QuestionnaireService(QuestionnaireRepository questionnaireRepo,
                                 UtilisateurRepository utilisateurRepo) {
        this.questionnaireRepo = questionnaireRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    /**
     * Crée ou met à jour le questionnaire d'un utilisateur.
     * Recalcule les calories cibles automatiquement.
     */
    public QuestionnaireAlimentaire sauvegarderQuestionnaire(Long utilisateurId,
                                                              QuestionnaireAlimentaire questionnaire) {
        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId));

        questionnaire.setUtilisateur(utilisateur);

        // Calculer les calories cibles
        int caloriesCibles = calculerCaloriesCibles(utilisateur, questionnaire);
        questionnaire.setCaloriesCiblesKcal(caloriesCibles);

        // Supprimer l'ancien questionnaire si existant
        questionnaireRepo.findByUtilisateurId(utilisateurId)
                .ifPresent(ancien -> questionnaireRepo.delete(ancien));

        return questionnaireRepo.save(questionnaire);
    }

    @Transactional(readOnly = true)
    public Optional<QuestionnaireAlimentaire> trouverParUtilisateur(Long utilisateurId) {
        return questionnaireRepo.findByUtilisateurId(utilisateurId);
    }

    /**
     * Calcule les calories journalières cibles à l'aide de la formule de Harris-Benedict
     * et du facteur d'activité physique (PAL), puis ajuste selon l'objectif sportif.
     */
    private int calculerCaloriesCibles(Utilisateur utilisateur, QuestionnaireAlimentaire questionnaire) {
        double poids = utilisateur.getPoidsKg() != null ? utilisateur.getPoidsKg() : 70.0;
        double taille = utilisateur.getTailleCm() != null ? utilisateur.getTailleCm() : 170.0;

        // Âge estimé si date de naissance non renseignée
        int age = 30;
        if (utilisateur.getDateNaissance() != null) {
            age = java.time.LocalDate.now().getYear() - utilisateur.getDateNaissance().getYear();
        }

        // Métabolisme de base (formule Harris-Benedict, version homme par défaut)
        double metabolismeBase = 88.36 + (13.4 * poids) + (4.8 * taille) - (5.7 * age);

        // Facteur activité physique
        double facteurActivite = switch (questionnaire.getNiveauActivite()) {
            case SEDENTAIRE            -> 1.2;
            case LEGEREMENT_ACTIF      -> 1.375;
            case MODEREMENT_ACTIF      -> 1.55;
            case TRES_ACTIF            -> 1.725;
            case EXTREMEMENT_ACTIF     -> 1.9;
        };

        double caloriesEntretien = metabolismeBase * facteurActivite;

        // Ajustement selon l'objectif
        double ajustement = switch (questionnaire.getObjectif()) {
            case PRISE_DE_MASSE  -> +300;
            case SECHE           -> -400;
            case PERTE_DE_POIDS  -> -500;
            case EQUILIBRE       -> 0;
            case MAINTIEN        -> 0;
        };

        return (int) Math.round(caloriesEntretien + ajustement);
    }
}
