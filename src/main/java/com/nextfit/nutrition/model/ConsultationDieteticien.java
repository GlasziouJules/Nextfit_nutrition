package com.nextfit.nutrition.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Réservation d'une consultation en ligne avec un diététicien partenaire.
 * Incluse dans l'abonnement VIP, option payante pour les autres.
 */
@Entity
@Table(name = "consultations_dieteticien")
public class ConsultationDieteticien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @NotNull
    private LocalDateTime dateHeure;

    /** Durée de la consultation en minutes (30 ou 60) */
    private int dureeMins = 30;

    @Enumerated(EnumType.STRING)
    private StatutConsultation statut = StatutConsultation.EN_ATTENTE;

    /** Nom complet du diététicien assigné */
    private String nomDieteticien;

    /** Lien de visioconférence généré */
    private String lienVisio;

    /** Notes de suivi post-consultation */
    @Column(length = 3000)
    private String notesConsultation;

    /** Indique si cette consultation est offerte (abonnement VIP) */
    private boolean incluse = false;

    /** Prix facturé si non incluse (en euros) */
    private double prixEuros = 45.0;

    // ---- Constructeurs ----

    public ConsultationDieteticien() {}

    public ConsultationDieteticien(Utilisateur utilisateur, LocalDateTime dateHeure) {
        this.utilisateur = utilisateur;
        this.dateHeure = dateHeure;
        this.incluse = utilisateur.getTypeAbonnement() == Utilisateur.TypeAbonnement.VIP;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public int getDureeMins() { return dureeMins; }
    public void setDureeMins(int dureeMins) { this.dureeMins = dureeMins; }

    public StatutConsultation getStatut() { return statut; }
    public void setStatut(StatutConsultation statut) { this.statut = statut; }

    public String getNomDieteticien() { return nomDieteticien; }
    public void setNomDieteticien(String nomDieteticien) { this.nomDieteticien = nomDieteticien; }

    public String getLienVisio() { return lienVisio; }
    public void setLienVisio(String lienVisio) { this.lienVisio = lienVisio; }

    public String getNotesConsultation() { return notesConsultation; }
    public void setNotesConsultation(String notesConsultation) { this.notesConsultation = notesConsultation; }

    public boolean isIncluse() { return incluse; }
    public void setIncluse(boolean incluse) { this.incluse = incluse; }

    public double getPrixEuros() { return prixEuros; }
    public void setPrixEuros(double prixEuros) { this.prixEuros = prixEuros; }

    // ---- Enum ----

    public enum StatutConsultation {
        EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
    }
}
