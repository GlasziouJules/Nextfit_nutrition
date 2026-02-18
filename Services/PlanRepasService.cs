using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Services;

/// <summary>Service de gestion des plans de repas personnalisés.</summary>
public class PlanRepasService
{
    private readonly NutritionDbContext _ctx;

    public PlanRepasService(NutritionDbContext ctx) => _ctx = ctx;

    public async Task<List<PlanRepas>> GetAllAsync()
        => await _ctx.PlansRepas.Include(p => p.Repas).ToListAsync();

    public async Task<PlanRepas?> GetByIdAsync(long id)
        => await _ctx.PlansRepas.Include(p => p.Repas).FirstOrDefaultAsync(p => p.Id == id);

    public async Task<List<PlanRepas>> GetByObjectifAsync(QuestionnaireAlimentaire.ObjectifSportif objectif)
        => await _ctx.PlansRepas.Include(p => p.Repas)
                                .Where(p => p.Objectif == objectif)
                                .ToListAsync();

    /// <summary>Suggère le plan le plus adapté selon le questionnaire de l'utilisateur.</summary>
    public async Task<PlanRepas?> SuggererPourUtilisateurAsync(long utilisateurId)
    {
        var questionnaire = await _ctx.Questionnaires
            .FirstOrDefaultAsync(q => q.UtilisateurId == utilisateurId);

        if (questionnaire == null) return null;

        // Plan idéal : même objectif ET même préférence
        var plan = await _ctx.PlansRepas.Include(p => p.Repas)
            .FirstOrDefaultAsync(p => p.Objectif == questionnaire.Objectif
                                   && p.Preference == questionnaire.Preference);

        // Fallback : même objectif seulement
        plan ??= await _ctx.PlansRepas.Include(p => p.Repas)
            .FirstOrDefaultAsync(p => p.Objectif == questionnaire.Objectif);

        return plan;
    }
}
