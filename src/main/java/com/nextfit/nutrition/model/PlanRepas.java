package com.nextfit.nutrition.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan de repas personnalisé généré en fonction du questionnaire de l'utilisateur.
 * Contient plusieurs repas répartis sur une journée.
 */
@Entity
@Table(name = "plans_repas")
public class PlanRepas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private QuestionnaireAlimentaire.ObjectifSportif objectif;

    @Enumerated(EnumType.STRING)
    private QuestionnaireAlimentaire.PreferenceAlimentaire preference;

    /** Calories totales approximatives de la journée (kcal) */
    private int caloriesTotalesKcal;

    /** Protéines totales en grammes */
    private double proteinesG;

    /** Glucides totaux en grammes */
    private double glucidesG;

    /** Lipides totaux en grammes */
    private double lipidesG;

    /** Fibres totales en grammes */
    private double fibresG;

    @OneToMany(mappedBy = "planRepas", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("typeRepas ASC")
    private List<Repas> repas = new ArrayList<>();

    // ---- Constructeurs ----

    public PlanRepas() {}

    public PlanRepas(String nom, String description,
                     QuestionnaireAlimentaire.ObjectifSportif objectif,
                     QuestionnaireAlimentaire.PreferenceAlimentaire preference) {
        this.nom = nom;
        this.description = description;
        this.objectif = objectif;
        this.preference = preference;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public QuestionnaireAlimentaire.ObjectifSportif getObjectif() { return objectif; }
    public void setObjectif(QuestionnaireAlimentaire.ObjectifSportif objectif) { this.objectif = objectif; }

    public QuestionnaireAlimentaire.PreferenceAlimentaire getPreference() { return preference; }
    public void setPreference(QuestionnaireAlimentaire.PreferenceAlimentaire preference) { this.preference = preference; }

    public int getCaloriesTotalesKcal() { return caloriesTotalesKcal; }
    public void setCaloriesTotalesKcal(int caloriesTotalesKcal) { this.caloriesTotalesKcal = caloriesTotalesKcal; }

    public double getProteinesG() { return proteinesG; }
    public void setProteinesG(double proteinesG) { this.proteinesG = proteinesG; }

    public double getGlucidesG() { return glucidesG; }
    public void setGlucidesG(double glucidesG) { this.glucidesG = glucidesG; }

    public double getLipidesG() { return lipidesG; }
    public void setLipidesG(double lipidesG) { this.lipidesG = lipidesG; }

    public double getFibresG() { return fibresG; }
    public void setFibresG(double fibresG) { this.fibresG = fibresG; }

    public List<Repas> getRepas() { return repas; }
    public void setRepas(List<Repas> repas) { this.repas = repas; }
}
