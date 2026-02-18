package com.nextfit.nutrition.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Questionnaire alimentaire rempli par le membre pour personnaliser son plan nutritionnel.
 * Regroupe allergies, préférences et objectifs sportifs.
 */
@Entity
@Table(name = "questionnaires_alimentaires")
public class QuestionnaireAlimentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // --- Allergies ---
    private boolean allergiqueGluten = false;
    private boolean allergiqueLactose = false;
    private boolean allergiqueNoix = false;
    private boolean allergiqueOeufs = false;
    private boolean allergiqueFruitsMer = false;

    /** Autres allergies libres */
    private String autresAllergies;

    // --- Préférences alimentaires ---
    @Enumerated(EnumType.STRING)
    private PreferenceAlimentaire preference = PreferenceAlimentaire.OMNIVORE;

    // --- Objectif sportif ---
    @Enumerated(EnumType.STRING)
    private ObjectifSportif objectif = ObjectifSportif.EQUILIBRE;

    // --- Niveau d'activité ---
    @Enumerated(EnumType.STRING)
    private NiveauActivite niveauActivite = NiveauActivite.MODEREMENT_ACTIF;

    /** Nombre de repas souhaité par jour */
    private int nombreRepasParJour = 3;

    /** Calories cibles calculées automatiquement (kcal/jour) */
    private Integer caloriesCiblesKcal;

    // ---- Constructeurs ----

    public QuestionnaireAlimentaire() {}

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public boolean isAllergiqueGluten() { return allergiqueGluten; }
    public void setAllergiqueGluten(boolean allergiqueGluten) { this.allergiqueGluten = allergiqueGluten; }

    public boolean isAllergiqueLactose() { return allergiqueLactose; }
    public void setAllergiqueLactose(boolean allergiqueLactose) { this.allergiqueLactose = allergiqueLactose; }

    public boolean isAllergiqueNoix() { return allergiqueNoix; }
    public void setAllergiqueNoix(boolean allergiqueNoix) { this.allergiqueNoix = allergiqueNoix; }

    public boolean isAllergiqueOeufs() { return allergiqueOeufs; }
    public void setAllergiqueOeufs(boolean allergiqueOeufs) { this.allergiqueOeufs = allergiqueOeufs; }

    public boolean isAllergiqueFruitsMer() { return allergiqueFruitsMer; }
    public void setAllergiqueFruitsMer(boolean allergiqueFruitsMer) { this.allergiqueFruitsMer = allergiqueFruitsMer; }

    public String getAutresAllergies() { return autresAllergies; }
    public void setAutresAllergies(String autresAllergies) { this.autresAllergies = autresAllergies; }

    public PreferenceAlimentaire getPreference() { return preference; }
    public void setPreference(PreferenceAlimentaire preference) { this.preference = preference; }

    public ObjectifSportif getObjectif() { return objectif; }
    public void setObjectif(ObjectifSportif objectif) { this.objectif = objectif; }

    public NiveauActivite getNiveauActivite() { return niveauActivite; }
    public void setNiveauActivite(NiveauActivite niveauActivite) { this.niveauActivite = niveauActivite; }

    public int getNombreRepasParJour() { return nombreRepasParJour; }
    public void setNombreRepasParJour(int nombreRepasParJour) { this.nombreRepasParJour = nombreRepasParJour; }

    public Integer getCaloriesCiblesKcal() { return caloriesCiblesKcal; }
    public void setCaloriesCiblesKcal(Integer caloriesCiblesKcal) { this.caloriesCiblesKcal = caloriesCiblesKcal; }

    // ---- Enums ----

    public enum PreferenceAlimentaire {
        OMNIVORE, VEGETARIEN, VEGAN, PESCETARIEN, FLEXITARIEN
    }

    public enum ObjectifSportif {
        PRISE_DE_MASSE,
        SECHE,
        EQUILIBRE,
        PERTE_DE_POIDS,
        MAINTIEN
    }

    public enum NiveauActivite {
        SEDENTAIRE,
        LEGEREMENT_ACTIF,
        MODEREMENT_ACTIF,
        TRES_ACTIF,
        EXTREMEMENT_ACTIF
    }
}
