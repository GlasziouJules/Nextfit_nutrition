package com.nextfit.nutrition.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un repas (petit-déjeuner, déjeuner, dîner, collation) dans un plan.
 */
@Entity
@Table(name = "repas")
public class Repas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_repas_id")
    private PlanRepas planRepas;

    @Enumerated(EnumType.STRING)
    private TypeRepas typeRepas;

    private String nom;

    private String description;

    private int caloriesKcal;
    private double proteinesG;
    private double glucidesG;
    private double lipidesG;
    private double fibresG;

    @ElementCollection
    @CollectionTable(name = "repas_ingredients", joinColumns = @JoinColumn(name = "repas_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    /** Instructions de préparation */
    @Column(length = 2000)
    private String preparation;

    /** Temps de préparation en minutes */
    private int tempsPreparationMin;

    // ---- Constructeurs ----

    public Repas() {}

    public Repas(TypeRepas typeRepas, String nom, String description) {
        this.typeRepas = typeRepas;
        this.nom = nom;
        this.description = description;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlanRepas getPlanRepas() { return planRepas; }
    public void setPlanRepas(PlanRepas planRepas) { this.planRepas = planRepas; }

    public TypeRepas getTypeRepas() { return typeRepas; }
    public void setTypeRepas(TypeRepas typeRepas) { this.typeRepas = typeRepas; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(int caloriesKcal) { this.caloriesKcal = caloriesKcal; }

    public double getProteinesG() { return proteinesG; }
    public void setProteinesG(double proteinesG) { this.proteinesG = proteinesG; }

    public double getGlucidesG() { return glucidesG; }
    public void setGlucidesG(double glucidesG) { this.glucidesG = glucidesG; }

    public double getLipidesG() { return lipidesG; }
    public void setLipidesG(double lipidesG) { this.lipidesG = lipidesG; }

    public double getFibresG() { return fibresG; }
    public void setFibresG(double fibresG) { this.fibresG = fibresG; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public String getPreparation() { return preparation; }
    public void setPreparation(String preparation) { this.preparation = preparation; }

    public int getTempsPreparationMin() { return tempsPreparationMin; }
    public void setTempsPreparationMin(int tempsPreparationMin) { this.tempsPreparationMin = tempsPreparationMin; }

    public enum TypeRepas {
        PETIT_DEJEUNER,
        DEJEUNER,
        DINER,
        COLLATION_MATIN,
        COLLATION_APRES_MIDI,
        COLLATION_SOIR
    }
}
