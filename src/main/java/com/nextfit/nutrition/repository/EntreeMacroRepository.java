package com.nextfit.nutrition.repository;

import com.nextfit.nutrition.model.EntreeMacro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntreeMacroRepository extends JpaRepository<EntreeMacro, Long> {

    /** Toutes les entrées d'un utilisateur pour une date donnée */
    List<EntreeMacro> findByUtilisateurIdAndDateOrderByHeureAsc(Long utilisateurId, LocalDate date);

    /** Toutes les entrées d'un utilisateur entre deux dates */
    List<EntreeMacro> findByUtilisateurIdAndDateBetweenOrderByDateAscHeureAsc(
            Long utilisateurId, LocalDate debut, LocalDate fin);

    /** Somme des calories pour un utilisateur sur une journée */
    @Query("SELECT COALESCE(SUM(e.caloriesKcal), 0) FROM EntreeMacro e " +
           "WHERE e.utilisateur.id = :userId AND e.date = :date")
    double sumCaloriesParJour(@Param("userId") Long userId, @Param("date") LocalDate date);

    /** Somme des protéines pour un utilisateur sur une journée */
    @Query("SELECT COALESCE(SUM(e.proteinesG), 0) FROM EntreeMacro e " +
           "WHERE e.utilisateur.id = :userId AND e.date = :date")
    double sumProteinesParJour(@Param("userId") Long userId, @Param("date") LocalDate date);

    /** Somme des glucides pour un utilisateur sur une journée */
    @Query("SELECT COALESCE(SUM(e.glucidesG), 0) FROM EntreeMacro e " +
           "WHERE e.utilisateur.id = :userId AND e.date = :date")
    double sumGlucidesParJour(@Param("userId") Long userId, @Param("date") LocalDate date);

    /** Somme des lipides pour un utilisateur sur une journée */
    @Query("SELECT COALESCE(SUM(e.lipidesG), 0) FROM EntreeMacro e " +
           "WHERE e.utilisateur.id = :userId AND e.date = :date")
    double sumLipidesParJour(@Param("userId") Long userId, @Param("date") LocalDate date);
}
