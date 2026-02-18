namespace NextfitNutrition.Models;

/// <summary>Un repas (petit-déjeuner, déjeuner, dîner, collation) dans un plan.</summary>
public class Repas
{
    public long Id { get; set; }

    public long      PlanRepasId { get; set; }
    public PlanRepas? PlanRepas  { get; set; }

    public TypeRepas TypeRepas   { get; set; }
    public string    Nom         { get; set; } = string.Empty;
    public string    Description { get; set; } = string.Empty;

    public int    CaloriesKcal       { get; set; }
    public double ProteinesG         { get; set; }
    public double GlucidesG          { get; set; }
    public double LipidesG           { get; set; }
    public double FibresG            { get; set; }

    /// <summary>Liste des ingrédients (stockée en JSON dans la BDD).</summary>
    public List<string> Ingredients  { get; set; } = new();

    public string? Preparation         { get; set; }
    public int     TempsPreparationMin { get; set; }

    public enum TypeRepas
    {
        PETIT_DEJEUNER,
        DEJEUNER,
        DINER,
        COLLATION_MATIN,
        COLLATION_APRES_MIDI,
        COLLATION_SOIR
    }
}
