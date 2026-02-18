package com.nextfit.nutrition.repository;

import com.nextfit.nutrition.model.ArticleNutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleNutritionRepository extends JpaRepository<ArticleNutrition, Long> {

    List<ArticleNutrition> findByCategorieOrderByDatePublicationDesc(ArticleNutrition.CategorieNutrition categorie);

    List<ArticleNutrition> findByTypeContenuOrderByDatePublicationDesc(ArticleNutrition.TypeContenu typeContenu);

    /** Articles accessibles à tous (non réservés VIP) */
    List<ArticleNutrition> findByReserveVipFalseOrderByDatePublicationDesc();

    /** Tous les articles (VIP inclus) */
    List<ArticleNutrition> findAllByOrderByDatePublicationDesc();
}
