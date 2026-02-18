package com.nextfit.nutrition.service;

import com.nextfit.nutrition.model.ConsultationDieteticien;
import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.ConsultationRepository;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des consultations diététicien en ligne.
 * Les abonnés VIP bénéficient de consultations incluses,
 * les autres membres paient 45€ par séance.
 */
@Service
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepo;
    private final UtilisateurRepository utilisateurRepo;

    public ConsultationService(ConsultationRepository consultationRepo,
                                UtilisateurRepository utilisateurRepo) {
        this.consultationRepo = consultationRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    /**
     * Réserve une consultation pour un membre.
     * Vérifie que le créneau n'est pas déjà pris.
     */
    public ConsultationDieteticien reserverConsultation(Long utilisateurId, LocalDateTime dateHeure,
                                                         int dureeMins) {
        if (consultationRepo.existsByDateHeure(dateHeure)) {
            throw new IllegalStateException("Ce créneau est déjà réservé : " + dateHeure);
        }

        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId));

        ConsultationDieteticien consultation = new ConsultationDieteticien(utilisateur, dateHeure);
        consultation.setDureeMins(dureeMins);

        // Abonnés VIP : consultation offerte
        boolean estVip = utilisateur.getTypeAbonnement() == Utilisateur.TypeAbonnement.VIP;
        consultation.setIncluse(estVip);
        consultation.setPrixEuros(estVip ? 0.0 : 45.0);

        // Assigner un diététicien (logique simplifiée)
        consultation.setNomDieteticien("Dr. Marie Dupont - Diététicienne agréée");
        consultation.setLienVisio("https://visio.nextfit.fr/session/" + System.currentTimeMillis());
        consultation.setStatut(ConsultationDieteticien.StatutConsultation.CONFIRMEE);

        return consultationRepo.save(consultation);
    }

    /** Annule une consultation */
    public ConsultationDieteticien annulerConsultation(Long consultationId) {
        ConsultationDieteticien consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable : " + consultationId));

        if (consultation.getStatut() == ConsultationDieteticien.StatutConsultation.TERMINEE) {
            throw new IllegalStateException("Impossible d'annuler une consultation déjà terminée.");
        }

        consultation.setStatut(ConsultationDieteticien.StatutConsultation.ANNULEE);
        return consultationRepo.save(consultation);
    }

    /** Consultations d'un utilisateur */
    @Transactional(readOnly = true)
    public List<ConsultationDieteticien> consultationsUtilisateur(Long utilisateurId) {
        return consultationRepo.findByUtilisateurIdOrderByDateHeureDesc(utilisateurId);
    }

    /** Prochaines consultations d'un utilisateur */
    @Transactional(readOnly = true)
    public List<ConsultationDieteticien> prochainesConsultations(Long utilisateurId) {
        return consultationRepo.findByUtilisateurIdAndDateHeureAfterOrderByDateHeureAsc(
                utilisateurId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Optional<ConsultationDieteticien> trouverParId(Long id) {
        return consultationRepo.findById(id);
    }
}
