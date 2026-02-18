package com.nextfit.nutrition.repository;

import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireAlimentaire, Long> {

    Optional<QuestionnaireAlimentaire> findByUtilisateurId(Long utilisateurId);

    boolean existsByUtilisateurId(Long utilisateurId);
}
