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

        // Facteur activité physique (switch Java 11 classique)
        double facteurActivite;
        switch (questionnaire.getNiveauActivite()) {
            case SEDENTAIRE:        facteurActivite = 1.2;   break;
            case LEGEREMENT_ACTIF:  facteurActivite = 1.375; break;
            case TRES_ACTIF:        facteurActivite = 1.725; break;
            case EXTREMEMENT_ACTIF: facteurActivite = 1.9;   break;
            default:                facteurActivite = 1.55;  // MODEREMENT_ACTIF
        }

        double caloriesEntretien = metabolismeBase * facteurActivite;

        // Ajustement selon l'objectif
        double ajustement;
        switch (questionnaire.getObjectif()) {
            case PRISE_DE_MASSE: ajustement = +300; break;
            case SECHE:          ajustement = -400; break;
            case PERTE_DE_POIDS: ajustement = -500; break;
            default:             ajustement = 0;    // EQUILIBRE, MAINTIEN
        }

        return (int) Math.round(caloriesEntretien + ajustement);
    }
}
