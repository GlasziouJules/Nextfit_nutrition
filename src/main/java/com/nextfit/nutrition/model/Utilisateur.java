package com.nextfit.nutrition.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un membre de la salle de sport NextFit.
 */
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String prenom;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate dateNaissance;

    /** Taille en centimètres */
    private Double tailleCm;

    /** Poids en kilogrammes */
    private Double poidsKg;

    @Enumerated(EnumType.STRING)
    private TypeAbonnement typeAbonnement = TypeAbonnement.STANDARD;

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuestionnaireAlimentaire questionnaire;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntreeMacro> entreesMacros = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultationDieteticien> consultations = new ArrayList<>();

    // ---- Constructeurs ----

    public Utilisateur() {}

    public Utilisateur(String prenom, String nom, String email) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Double getTailleCm() { return tailleCm; }
    public void setTailleCm(Double tailleCm) { this.tailleCm = tailleCm; }

    public Double getPoidsKg() { return poidsKg; }
    public void setPoidsKg(Double poidsKg) { this.poidsKg = poidsKg; }

    public TypeAbonnement getTypeAbonnement() { return typeAbonnement; }
    public void setTypeAbonnement(TypeAbonnement typeAbonnement) { this.typeAbonnement = typeAbonnement; }

    public QuestionnaireAlimentaire getQuestionnaire() { return questionnaire; }
    public void setQuestionnaire(QuestionnaireAlimentaire questionnaire) { this.questionnaire = questionnaire; }

    public List<EntreeMacro> getEntreesMacros() { return entreesMacros; }
    public void setEntreesMacros(List<EntreeMacro> entreesMacros) { this.entreesMacros = entreesMacros; }

    public List<ConsultationDieteticien> getConsultations() { return consultations; }
    public void setConsultations(List<ConsultationDieteticien> consultations) { this.consultations = consultations; }

    public enum TypeAbonnement {
        STANDARD, PREMIUM, VIP
    }
}
