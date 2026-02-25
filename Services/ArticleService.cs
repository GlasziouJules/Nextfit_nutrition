using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Services;

/// <summary>Service pour les articles et vidéos de conseils nutritionnels.</summary>
public class ArticleService
{
    private readonly NutritionDbContext _ctx;

    public ArticleService(NutritionDbContext ctx) => _ctx = ctx;

    /// <summary>Articles accessibles selon l'abonnement de l'utilisateur.</summary>
    public async Task<List<ArticleNutrition>> GetPourUtilisateurAsync(long utilisateurId)
    {
        var utilisateur = await _ctx.Utilisateurs.FindAsync(utilisateurId);
        bool estVipOuPremium = utilisateur?.Abonnement
            is TypeAbonnement.VIP or TypeAbonnement.PREMIUM;

        return estVipOuPremium
            ? await GetTousAsync()
            : await GetPublicsAsync();
    }

    public async Task<List<ArticleNutrition>> GetPublicsAsync()
        => await _ctx.Articles
            .Where(a => !a.ReserveVip)
            .OrderByDescending(a => a.DatePublication)
            .ToListAsync();

    public async Task<List<ArticleNutrition>> GetTousAsync()
        => await _ctx.Articles
            .OrderByDescending(a => a.DatePublication)
            .ToListAsync();

    public async Task<ArticleNutrition?> GetByIdAsync(long id)
        => await _ctx.Articles.FindAsync(id);

    public async Task<List<ArticleNutrition>> GetParCategorieAsync(CategorieNutrition categorie)
        => await _ctx.Articles
            .Where(a => a.Categorie == categorie)
            .OrderByDescending(a => a.DatePublication)
            .ToListAsync();

    public async Task<List<ArticleNutrition>> GetParTypeAsync(TypeContenu type)
        => await _ctx.Articles
            .Where(a => a.Format == type)
            .OrderByDescending(a => a.DatePublication)
            .ToListAsync();
}
