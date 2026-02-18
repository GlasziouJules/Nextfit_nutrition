using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Services;

/// <summary>Service de suivi des macronutriments quotidiens.</summary>
public class MacroTrackingService
{
    private readonly NutritionDbContext _ctx;

    public MacroTrackingService(NutritionDbContext ctx) => _ctx = ctx;

    public async Task<EntreeMacro> EnregistrerAsync(long utilisateurId, EntreeMacro entree)
    {
        _ = await _ctx.Utilisateurs.FindAsync(utilisateurId)
            ?? throw new ArgumentException($"Utilisateur introuvable : {utilisateurId}");

        entree.UtilisateurId = utilisateurId;
        if (entree.Date == default) entree.Date = DateOnly.FromDateTime(DateTime.Today);

        _ctx.EntreesMacros.Add(entree);
        await _ctx.SaveChangesAsync();
        return entree;
    }

    public async Task SupprimerAsync(long entreeId)
    {
        var entree = await _ctx.EntreesMacros.FindAsync(entreeId);
        if (entree != null)
        {
            _ctx.EntreesMacros.Remove(entree);
            await _ctx.SaveChangesAsync();
        }
    }

    public async Task<List<EntreeMacro>> GetParJourAsync(long utilisateurId, DateOnly date)
        => await _ctx.EntreesMacros
            .Where(e => e.UtilisateurId == utilisateurId && e.Date == date)
            .OrderBy(e => e.Heure)
            .ToListAsync();

    public async Task<List<EntreeMacro>> GetParPeriodeAsync(long utilisateurId, DateOnly debut, DateOnly fin)
        => await _ctx.EntreesMacros
            .Where(e => e.UtilisateurId == utilisateurId && e.Date >= debut && e.Date <= fin)
            .OrderBy(e => e.Date).ThenBy(e => e.Heure)
            .ToListAsync();

    /// <summary>Bilan nutritionnel d'une journée avec progression par rapport aux objectifs.</summary>
    public async Task<Dictionary<string, object>> ResumeJourneeAsync(long utilisateurId, DateOnly date)
    {
        var entrees = await GetParJourAsync(utilisateurId, date);

        double calories  = entrees.Sum(e => e.CaloriesKcal);
        double proteines = entrees.Sum(e => e.ProteinesG);
        double glucides  = entrees.Sum(e => e.GlucidesG);
        double lipides   = entrees.Sum(e => e.LipidesG);

        var questionnaire = await _ctx.Questionnaires
            .FirstOrDefaultAsync(q => q.UtilisateurId == utilisateurId);

        int caloriesCibles = questionnaire?.CaloriesCiblesKcal ?? 2000;
        var objectif = questionnaire?.Objectif ?? QuestionnaireAlimentaire.ObjectifSportif.EQUILIBRE;
        var (p, g, l) = CalculerMacrosCibles(caloriesCibles, objectif);

        int pourcentage = caloriesCibles > 0
            ? (int)Math.Min(100, calories / caloriesCibles * 100) : 0;

        return new Dictionary<string, object>
        {
            ["date"]               = date.ToString("yyyy-MM-dd"),
            ["caloriesConsommees"] = Math.Round(calories,  1),
            ["caloriesCibles"]     = caloriesCibles,
            ["pourcentageCalories"] = pourcentage,
            ["proteinesG"]         = Math.Round(proteines, 1),
            ["proteinesCiblesG"]   = p,
            ["glucidesG"]          = Math.Round(glucides,  1),
            ["glucidesCiblesG"]    = g,
            ["lipidesG"]           = Math.Round(lipides,   1),
            ["lipidesCiblesG"]     = l
        };
    }

    /// <summary>Cibles en grammes [proteines, glucides, lipides] selon l'objectif.</summary>
    private static (double p, double g, double l) CalculerMacrosCibles(
        int calories, QuestionnaireAlimentaire.ObjectifSportif objectif)
    {
        var (ratioP, ratioG, ratioL) = objectif switch
        {
            QuestionnaireAlimentaire.ObjectifSportif.PRISE_DE_MASSE => (0.30, 0.50, 0.20),
            QuestionnaireAlimentaire.ObjectifSportif.SECHE          => (0.40, 0.30, 0.30),
            QuestionnaireAlimentaire.ObjectifSportif.PERTE_DE_POIDS => (0.35, 0.35, 0.30),
            _                                                        => (0.25, 0.50, 0.25)
        };
        return (
            Math.Round(calories * ratioP / 4),
            Math.Round(calories * ratioG / 4),
            Math.Round(calories * ratioL / 9)
        );
    }
}
