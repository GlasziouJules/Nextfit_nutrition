package com.nextfit.nutrition.controller;

import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST pour la gestion des membres NextFit.
 */
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepo;

    public UtilisateurController(UtilisateurRepository utilisateurRepo) {
        this.utilisateurRepo = utilisateurRepo;
    }

    @GetMapping
    public List<Utilisateur> listerTous() {
        return utilisateurRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> trouverParId(@PathVariable Long id) {
        return utilisateurRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur creer(@Valid @RequestBody Utilisateur utilisateur) {
        if (utilisateurRepo.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");
        }
        return utilisateurRepo.save(utilisateur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifier(@PathVariable Long id,
                                                  @Valid @RequestBody Utilisateur modifications) {
        return utilisateurRepo.findById(id).map(u -> {
            u.setPrenom(modifications.getPrenom());
            u.setNom(modifications.getNom());
            u.setTailleCm(modifications.getTailleCm());
            u.setPoidsKg(modifications.getPoidsKg());
            u.setDateNaissance(modifications.getDateNaissance());
            u.setTypeAbonnement(modifications.getTypeAbonnement());
            return ResponseEntity.ok(utilisateurRepo.save(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!utilisateurRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        utilisateurRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
