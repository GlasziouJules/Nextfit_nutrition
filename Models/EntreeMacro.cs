using System.ComponentModel.DataAnnotations;

namespace NextfitNutrition.Models;

/// <summary>Entrée de suivi macronutriments : un aliment consommé par un membre.</summary>
public class EntreeMacro
{
    public long Id { get; set; }

    public long         UtilisateurId { get; set; }
    public Utilisateur? Utilisateur   { get; set; }

    [Required] public DateOnly Date { get; set; } = DateOnly.FromDateTime(DateTime.Today);

    public TimeOnly?       Heure     { get; set; }
    public TypeRepas TypeRepas { get; set; }

    [Required] public string NomAliment { get; set; } = string.Empty;

    /// <summary>Quantité consommée en grammes.</summary>
    public double QuantiteG    { get; set; }
    public double CaloriesKcal { get; set; }
    public double ProteinesG   { get; set; }
    public double GlucidesG    { get; set; }
    public double LipidesG     { get; set; }
    public double FibresG      { get; set; }

    public string? Notes { get; set; }
}
