using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Services;

/// <summary>
/// Service gérant le questionnaire alimentaire.
/// Calcule les calories cibles via la formule de Harris-Benedict.
/// </summary>
public class QuestionnaireService
{
    private readonly NutritionDbContext _ctx;

    public QuestionnaireService(NutritionDbContext ctx) => _ctx = ctx;

    public async Task<QuestionnaireAlimentaire?> GetByUtilisateurAsync(long utilisateurId)
        => await _ctx.Questionnaires.FirstOrDefaultAsync(q => q.UtilisateurId == utilisateurId);

    /// <summary>Crée ou remplace le questionnaire d'un utilisateur et recalcule ses calories cibles.</summary>
    public async Task<QuestionnaireAlimentaire> SauvegarderAsync(long utilisateurId,
                                                                   QuestionnaireAlimentaire questionnaire)
    {
        var utilisateur = await _ctx.Utilisateurs.FindAsync(utilisateurId)
            ?? throw new ArgumentException($"Utilisateur introuvable : {utilisateurId}");

        // Supprimer l'ancien questionnaire s'il existe
        var ancien = await _ctx.Questionnaires.FirstOrDefaultAsync(q => q.UtilisateurId == utilisateurId);
        if (ancien != null) _ctx.Questionnaires.Remove(ancien);

        questionnaire.UtilisateurId = utilisateurId;
        questionnaire.CaloriesCiblesKcal = CalculerCaloriesCibles(utilisateur, questionnaire);

        _ctx.Questionnaires.Add(questionnaire);
        await _ctx.SaveChangesAsync();
        return questionnaire;
    }

    /// <summary>
    /// Formule Harris-Benedict + facteur d'activité (PAL) + ajustement objectif.
    /// </summary>
    private static int CalculerCaloriesCibles(Utilisateur u, QuestionnaireAlimentaire q)
    {
        double poids  = u.PoidsKg  ?? 70.0;
        double taille = u.TailleCm ?? 170.0;
        int    age    = u.DateNaissance.HasValue
            ? DateTime.Today.Year - u.DateNaissance.Value.Year
            : 30;

        // Métabolisme de base (Harris-Benedict)
        double mb = 88.36 + (13.4 * poids) + (4.8 * taille) - (5.7 * age);

        double pal = q.NiveauActivite switch
        {
            QuestionnaireAlimentaire.NiveauActivite.SEDENTAIRE        => 1.2,
            QuestionnaireAlimentaire.NiveauActivite.LEGEREMENT_ACTIF  => 1.375,
            QuestionnaireAlimentaire.NiveauActivite.TRES_ACTIF        => 1.725,
            QuestionnaireAlimentaire.NiveauActivite.EXTREMEMENT_ACTIF => 1.9,
            _                                                          => 1.55 // MODEREMENT_ACTIF
        };

        double ajustement = q.Objectif switch
        {
            QuestionnaireAlimentaire.ObjectifSportif.PRISE_DE_MASSE => +300,
            QuestionnaireAlimentaire.ObjectifSportif.SECHE          => -400,
            QuestionnaireAlimentaire.ObjectifSportif.PERTE_DE_POIDS => -500,
            _                                                        => 0
        };

        return (int)Math.Round(mb * pal + ajustement);
    }
}
