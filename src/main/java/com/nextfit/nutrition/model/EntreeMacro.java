package com.nextfit.nutrition.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entrée de suivi des macronutriments : enregistrement d'un aliment/repas consommé
 * par un utilisateur à une date et heure donnée.
 */
@Entity
@Table(name = "entrees_macros")
public class EntreeMacro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @NotNull
    private LocalDate date;

    private LocalTime heure;

    @Enumerated(EnumType.STRING)
    private Repas.TypeRepas typeRepas;

    @NotBlank
    private String nomAliment;

    /** Quantité consommée en grammes */
    private double quantiteG;

    /** Calories pour la quantité saisie */
    private double caloriesKcal;

    /** Protéines en grammes */
    private double proteinesG;

    /** Glucides en grammes */
    private double glucidesG;

    /** Lipides en grammes */
    private double lipidesG;

    /** Fibres en grammes */
    private double fibresG;

    /** Notes libres (ex: "post-entraînement", "avant course") */
    private String notes;

    // ---- Constructeurs ----

    public EntreeMacro() {}

    public EntreeMacro(Utilisateur utilisateur, LocalDate date, String nomAliment) {
        this.utilisateur = utilisateur;
        this.date = date;
        this.nomAliment = nomAliment;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }

    public Repas.TypeRepas getTypeRepas() { return typeRepas; }
    public void setTypeRepas(Repas.TypeRepas typeRepas) { this.typeRepas = typeRepas; }

    public String getNomAliment() { return nomAliment; }
    public void setNomAliment(String nomAliment) { this.nomAliment = nomAliment; }

    public double getQuantiteG() { return quantiteG; }
    public void setQuantiteG(double quantiteG) { this.quantiteG = quantiteG; }

    public double getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(double caloriesKcal) { this.caloriesKcal = caloriesKcal; }

    public double getProteinesG() { return proteinesG; }
    public void setProteinesG(double proteinesG) { this.proteinesG = proteinesG; }

    public double getGlucidesG() { return glucidesG; }
    public void setGlucidesG(double glucidesG) { this.glucidesG = glucidesG; }

    public double getLipidesG() { return lipidesG; }
    public void setLipidesG(double lipidesG) { this.lipidesG = lipidesG; }

    public double getFibresG() { return fibresG; }
    public void setFibresG(double fibresG) { this.fibresG = fibresG; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
