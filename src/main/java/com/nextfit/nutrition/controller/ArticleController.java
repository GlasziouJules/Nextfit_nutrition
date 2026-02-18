package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.ArticleNutrition;
import com.nextfit.nutrition.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST pour les conseils nutritionnels (articles & vidéos).
 */
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * Tous les articles publics.
     * GET /api/articles
     */
    @GetMapping
    public List<ArticleNutrition> listerPublics() {
        return articleService.listerArticlesPublics();
    }

    /**
     * Articles accessibles selon l'abonnement de l'utilisateur.
     * GET /api/articles?utilisateurId=1
     */
    @GetMapping(params = "utilisateurId")
    public List<ArticleNutrition> listerPourUtilisateur(@RequestParam Long utilisateurId) {
        return articleService.listerArticlesAccessibles(utilisateurId);
    }

    /** Article par identifiant */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleNutrition> trouverParId(@PathVariable Long id) {
        return articleService.trouverParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Filtrer par catégorie.
     * GET /api/articles/categorie/HYDRATATION
     */
    @GetMapping("/categorie/{categorie}")
    public List<ArticleNutrition> parCategorie(
            @PathVariable ArticleNutrition.CategorieNutrition categorie) {
        return articleService.listerParCategorie(categorie);
    }

    /**
     * Filtrer par type (ARTICLE ou VIDEO).
     * GET /api/articles/type/VIDEO
     */
    @GetMapping("/type/{type}")
    public List<ArticleNutrition> parType(@PathVariable ArticleNutrition.TypeContenu type) {
        return articleService.listerParType(type);
    }
}
