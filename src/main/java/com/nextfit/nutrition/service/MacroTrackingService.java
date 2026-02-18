package com.nextfit.nutrition.service;

import com.nextfit.nutrition.model.EntreeMacro;
import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.EntreeMacroRepository;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import com.nextfit.nutrition.repository.QuestionnaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de suivi des macronutriments quotidiens.
 * Permet d'enregistrer les repas et de consulter la progression
 * par rapport aux objectifs caloriques et nutritionnels.
 */
@Service
@Transactional
public class MacroTrackingService {

    private final EntreeMacroRepository entreeMacroRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final QuestionnaireRepository questionnaireRepo;

    public MacroTrackingService(EntreeMacroRepository entreeMacroRepo,
                                  UtilisateurRepository utilisateurRepo,
                                  QuestionnaireRepository questionnaireRepo) {
        this.entreeMacroRepo = entreeMacroRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.questionnaireRepo = questionnaireRepo;
    }

    /** Enregistre une nouvelle entrée de macro */
    public EntreeMacro enregistrerEntree(Long utilisateurId, EntreeMacro entree) {
        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId));
        entree.setUtilisateur(utilisateur);
        if (entree.getDate() == null) {
            entree.setDate(LocalDate.now());
        }
        return entreeMacroRepo.save(entree);
    }

    /** Supprime une entrée */
    public void supprimerEntree(Long entreeId) {
        entreeMacroRepo.deleteById(entreeId);
    }

    /** Entrées du jour pour un utilisateur */
    @Transactional(readOnly = true)
    public List<EntreeMacro> entreesParJour(Long utilisateurId, LocalDate date) {
        return entreeMacroRepo.findByUtilisateurIdAndDateOrderByHeureAsc(utilisateurId, date);
    }

    /** Entrées sur une période */
    @Transactional(readOnly = true)
    public List<EntreeMacro> entreesParPeriode(Long utilisateurId, LocalDate debut, LocalDate fin) {
        return entreeMacroRepo.findByUtilisateurIdAndDateBetweenOrderByDateAscHeureAsc(utilisateurId, debut, fin);
    }

    /**
     * Résumé nutritionnel d'une journée pour un utilisateur.
     * Retourne les totaux et le pourcentage d'avancement par rapport aux objectifs.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> resumeJournee(Long utilisateurId, LocalDate date) {
        double calories  = entreeMacroRepo.sumCaloriesParJour(utilisateurId, date);
        double proteines = entreeMacroRepo.sumProteinesParJour(utilisateurId, date);
        double glucides  = entreeMacroRepo.sumGlucidesParJour(utilisateurId, date);
        double lipides   = entreeMacroRepo.sumLipidesParJour(utilisateurId, date);

        // Objectifs depuis le questionnaire
        Optional<QuestionnaireAlimentaire> questionnaire = questionnaireRepo.findByUtilisateurId(utilisateurId);
        int caloriesCibles = questionnaire.map(q -> q.getCaloriesCiblesKcal() != null
                ? q.getCaloriesCiblesKcal() : 2000).orElse(2000);

        // Répartition des macros recommandée selon l'objectif
        double[] macrosCibles = calculerMacrosCibles(caloriesCibles,
                questionnaire.map(QuestionnaireAlimentaire::getObjectif)
                        .orElse(QuestionnaireAlimentaire.ObjectifSportif.EQUILIBRE));

        // Map.of() est limité à 10 entrées en Java 11 — utilisation de HashMap
        Map<String, Object> result = new HashMap<>();
        result.put("date",               date.toString());
        result.put("caloriesConsommees", Math.round(calories * 10.0) / 10.0);
        result.put("caloriesCibles",     caloriesCibles);
        result.put("pourcentageCalories", caloriesCibles > 0 ? Math.min(100, (int)(calories / caloriesCibles * 100)) : 0);
        result.put("proteinesG",         Math.round(proteines * 10.0) / 10.0);
        result.put("proteinesCiblesG",   macrosCibles[0]);
        result.put("glucidesG",          Math.round(glucides * 10.0) / 10.0);
        result.put("glucidesCiblesG",    macrosCibles[1]);
        result.put("lipidesG",           Math.round(lipides * 10.0) / 10.0);
        result.put("lipidesCiblesG",     macrosCibles[2]);
        return result;
    }

    /**
     * Calcule les cibles de macros en grammes selon l'objectif sportif.
     * Retourne [proteines, glucides, lipides].
     */
    private double[] calculerMacrosCibles(int caloriesCibles,
                                           QuestionnaireAlimentaire.ObjectifSportif objectif) {
        // Répartitions caloriques : Protéines(4kcal/g), Glucides(4kcal/g), Lipides(9kcal/g)
        double ratioP, ratioG, ratioL;
        switch (objectif) {
            case PRISE_DE_MASSE:
                ratioP = 0.30; ratioG = 0.50; ratioL = 0.20; break;
            case SECHE:
                ratioP = 0.40; ratioG = 0.30; ratioL = 0.30; break;
            case PERTE_DE_POIDS:
                ratioP = 0.35; ratioG = 0.35; ratioL = 0.30; break;
            default:
                ratioP = 0.25; ratioG = 0.50; ratioL = 0.25; break;
        }
        return new double[]{
                Math.round(caloriesCibles * ratioP / 4),
                Math.round(caloriesCibles * ratioG / 4),
                Math.round(caloriesCibles * ratioL / 9)
        };
    }
}
