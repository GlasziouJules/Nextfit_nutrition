package com.nextfit.nutrition.repository;

import com.nextfit.nutrition.model.ConsultationDieteticien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<ConsultationDieteticien, Long> {

    List<ConsultationDieteticien> findByUtilisateurIdOrderByDateHeureDesc(Long utilisateurId);

    List<ConsultationDieteticien> findByStatutOrderByDateHeureAsc(ConsultationDieteticien.StatutConsultation statut);

    /** Vérifie si un créneau est déjà réservé */
    boolean existsByDateHeure(LocalDateTime dateHeure);

    /** Consultations à venir pour un utilisateur */
    List<ConsultationDieteticien> findByUtilisateurIdAndDateHeureAfterOrderByDateHeureAsc(
            Long utilisateurId, LocalDateTime maintenant);
}
