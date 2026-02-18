package com.nextfit.nutrition.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Article ou vidéo de conseil nutritionnel (hydratation, compléments, fibres...).
 */
@Entity
@Table(name = "articles_nutrition")
public class ArticleNutrition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 500)
    private String resume;

    @Column(length = 10000)
    private String contenu;

    @Enumerated(EnumType.STRING)
    private TypeContenu typeContenu;

    /** URL de la vidéo si typeContenu == VIDEO */
    private String urlVideo;

    /** URL de l'image de couverture */
    private String urlImageCouverture;

    /** Auteur de l'article (ex: diététicien partenaire) */
    private String auteur;

    private LocalDate datePublication;

    @ElementCollection
    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CategorieNutrition categorie;

    /** Indique si l'article est réservé aux abonnés VIP */
    private boolean reserveVip = false;

    // ---- Constructeurs ----

    public ArticleNutrition() {}

    public ArticleNutrition(String titre, String resume, TypeContenu typeContenu, CategorieNutrition categorie) {
        this.titre = titre;
        this.resume = resume;
        this.typeContenu = typeContenu;
        this.categorie = categorie;
        this.datePublication = LocalDate.now();
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public TypeContenu getTypeContenu() { return typeContenu; }
    public void setTypeContenu(TypeContenu typeContenu) { this.typeContenu = typeContenu; }

    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }

    public String getUrlImageCouverture() { return urlImageCouverture; }
    public void setUrlImageCouverture(String urlImageCouverture) { this.urlImageCouverture = urlImageCouverture; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public CategorieNutrition getCategorie() { return categorie; }
    public void setCategorie(CategorieNutrition categorie) { this.categorie = categorie; }

    public boolean isReserveVip() { return reserveVip; }
    public void setReserveVip(boolean reserveVip) { this.reserveVip = reserveVip; }

    // ---- Enums ----

    public enum TypeContenu {
        ARTICLE, VIDEO, INFOGRAPHIE
    }

    public enum CategorieNutrition {
        HYDRATATION,
        COMPLEMENTS_ALIMENTAIRES,
        FIBRES,
        GESTION_FRINGALES,
        PROTEINES,
        GLUCIDES,
        LIPIDES,
        MICRONUTRIMENTS,
        AVANT_SPORT,
        APRES_SPORT
    }
}
