package com.nextfit.nutrition.service;

import com.nextfit.nutrition.model.PlanRepas;
import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.model.Repas;
import com.nextfit.nutrition.repository.PlanRepasRepository;
import com.nextfit.nutrition.repository.QuestionnaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des plans de repas personnalisés.
 * Sélectionne le plan le plus adapté à l'objectif et aux préférences du membre.
 */
@Service
@Transactional(readOnly = true)
public class PlanRepasService {

    private final PlanRepasRepository planRepasRepo;
    private final QuestionnaireRepository questionnaireRepo;

    public PlanRepasService(PlanRepasRepository planRepasRepo,
                             QuestionnaireRepository questionnaireRepo) {
        this.planRepasRepo = planRepasRepo;
        this.questionnaireRepo = questionnaireRepo;
    }

    /** Retourne tous les plans disponibles */
    public List<PlanRepas> listerTousLesPlans() {
        return planRepasRepo.findAll();
    }

    /** Plan par identifiant */
    public Optional<PlanRepas> trouverParId(Long id) {
        return planRepasRepo.findById(id);
    }

    /** Plans filtrés par objectif sportif */
    public List<PlanRepas> listerParObjectif(QuestionnaireAlimentaire.ObjectifSportif objectif) {
        return planRepasRepo.findByObjectif(objectif);
    }

    /**
     * Suggère le plan le mieux adapté à un utilisateur en se basant
     * sur son questionnaire (objectif + préférence alimentaire).
     */
    public Optional<PlanRepas> suggererPlanPourUtilisateur(Long utilisateurId) {
        return questionnaireRepo.findByUtilisateurId(utilisateurId)
                .flatMap(q -> {
                    List<PlanRepas> plans = planRepasRepo.findByObjectifAndPreference(
                            q.getObjectif(), q.getPreference());
                    if (!plans.isEmpty()) {
                        return Optional.of(plans.get(0));
                    }
                    // Fallback : plan par objectif seulement
                    List<PlanRepas> parObjectif = planRepasRepo.findByObjectif(q.getObjectif());
                    return parObjectif.isEmpty() ? Optional.empty() : Optional.of(parObjectif.get(0));
                });
    }

    @Transactional
    public PlanRepas sauvegarder(PlanRepas plan) {
        // Lier chaque repas à ce plan
        if (plan.getRepas() != null) {
            plan.getRepas().forEach(r -> r.setPlanRepas(plan));
        }
        return planRepasRepo.save(plan);
    }

    @Transactional
    public void supprimer(Long id) {
        planRepasRepo.deleteById(id);
    }
}
