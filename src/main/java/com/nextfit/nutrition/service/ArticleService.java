package com.nextfit.nutrition.service;

import com.nextfit.nutrition.model.ArticleNutrition;
import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.ArticleNutritionRepository;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des articles et vidéos de conseils nutritionnels.
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleNutritionRepository articleRepo;
    private final UtilisateurRepository utilisateurRepo;

    public ArticleService(ArticleNutritionRepository articleRepo,
                           UtilisateurRepository utilisateurRepo) {
        this.articleRepo = articleRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    /** Articles accessibles à un membre donné selon son abonnement */
    public List<ArticleNutrition> listerArticlesAccessibles(Long utilisateurId) {
        return utilisateurRepo.findById(utilisateurId)
                .filter(u -> u.getTypeAbonnement() == Utilisateur.TypeAbonnement.VIP
                          || u.getTypeAbonnement() == Utilisateur.TypeAbonnement.PREMIUM)
                .map(u -> articleRepo.findAllByOrderByDatePublicationDesc())
                .orElse(articleRepo.findByReserveVipFalseOrderByDatePublicationDesc());
    }

    /** Tous les articles publics (non-VIP) */
    public List<ArticleNutrition> listerArticlesPublics() {
        return articleRepo.findByReserveVipFalseOrderByDatePublicationDesc();
    }

    /** Filtrer par catégorie */
    public List<ArticleNutrition> listerParCategorie(ArticleNutrition.CategorieNutrition categorie) {
        return articleRepo.findByCategorieOrderByDatePublicationDesc(categorie);
    }

    /** Filtrer par type (article ou vidéo) */
    public List<ArticleNutrition> listerParType(ArticleNutrition.TypeContenu typeContenu) {
        return articleRepo.findByTypeContenuOrderByDatePublicationDesc(typeContenu);
    }

    public Optional<ArticleNutrition> trouverParId(Long id) {
        return articleRepo.findById(id);
    }

    @Transactional
    public ArticleNutrition sauvegarder(ArticleNutrition article) {
        return articleRepo.save(article);
    }

    @Transactional
    public void supprimer(Long id) {
        articleRepo.deleteById(id);
    }
}
