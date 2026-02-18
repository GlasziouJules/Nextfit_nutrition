package com.nextfit.nutrition;

import com.nextfit.nutrition.model.QuestionnaireAlimentaire;
import com.nextfit.nutrition.model.Utilisateur;
import com.nextfit.nutrition.repository.UtilisateurRepository;
import com.nextfit.nutrition.service.QuestionnaireService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NutritionApplicationTests {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Test
    void contextLoads() {
        // Vérifie que le contexte Spring Boot démarre correctement
        assertThat(utilisateurRepo).isNotNull();
    }

    @Test
    void creationUtilisateur_OK() {
        Utilisateur u = new Utilisateur("Jean", "Dupont", "jean.test@nextfit.fr");
        u.setTailleCm(175.0);
        u.setPoidsKg(75.0);
        Utilisateur sauve = utilisateurRepo.save(u);
        assertThat(sauve.getId()).isNotNull();
        assertThat(sauve.getEmail()).isEqualTo("jean.test@nextfit.fr");
    }

    @Test
    void questionnaire_calculeCaloriesCibles() {
        // Créer un utilisateur
        Utilisateur u = new Utilisateur("Marie", "Test", "marie.test@nextfit.fr");
        u.setTailleCm(165.0);
        u.setPoidsKg(60.0);
        utilisateurRepo.save(u);

        // Remplir un questionnaire
        QuestionnaireAlimentaire q = new QuestionnaireAlimentaire();
        q.setObjectif(QuestionnaireAlimentaire.ObjectifSportif.EQUILIBRE);
        q.setPreference(QuestionnaireAlimentaire.PreferenceAlimentaire.OMNIVORE);
        q.setNiveauActivite(QuestionnaireAlimentaire.NiveauActivite.MODEREMENT_ACTIF);
        q.setNombreRepasParJour(3);

        QuestionnaireAlimentaire sauve = questionnaireService.sauvegarderQuestionnaire(u.getId(), q);

        // Les calories doivent être calculées (>0)
        assertThat(sauve.getCaloriesCiblesKcal()).isGreaterThan(1200);
        assertThat(sauve.getCaloriesCiblesKcal()).isLessThan(4000);
    }
}
