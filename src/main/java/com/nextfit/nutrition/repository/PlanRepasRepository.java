package com.nextfit.nutrition.repository;

import com.nextfit.nutrition.model.PlanRepas;
import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepasRepository extends JpaRepository<PlanRepas, Long> {

    List<PlanRepas> findByObjectif(QuestionnaireAlimentaire.ObjectifSportif objectif);

    List<PlanRepas> findByObjectifAndPreference(
            QuestionnaireAlimentaire.ObjectifSportif objectif,
            QuestionnaireAlimentaire.PreferenceAlimentaire preference
    );
}
