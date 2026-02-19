namespace NextfitNutrition.Models;

/// <summary>Article ou vidéo de conseil nutritionnel.</summary>
public class ArticleNutrition
{
    public enum TypeContenu { ARTICLE, VIDEO, INFOGRAPHIE }

    public enum CategorieNutrition
    {
        HYDRATATION, COMPLEMENTS_ALIMENTAIRES, FIBRES, GESTION_FRINGALES,
        PROTEINES, GLUCIDES, LIPIDES, MICRONUTRIMENTS, AVANT_SPORT, APRES_SPORT
    }

    public long Id { get; set; }

    public string  Titre              { get; set; } = string.Empty;
    public string  Resume             { get; set; } = string.Empty;
    public string? Contenu            { get; set; }
    public TypeContenu    TypeContenu { get; set; }
    public CategorieNutrition Categorie { get; set; }

    public string? UrlVideo           { get; set; }
    public string? UrlImageCouverture { get; set; }
    public string? Auteur             { get; set; }

    public DateOnly DatePublication   { get; set; } = DateOnly.FromDateTime(DateTime.Today);

    public List<string> Tags          { get; set; } = new();

    /// <summary>true = réservé aux abonnés VIP ou Premium.</summary>
    public bool ReserveVip            { get; set; }
}
