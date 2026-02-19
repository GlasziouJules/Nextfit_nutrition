namespace NextfitNutrition.Models;

/// <summary>Plan de repas personnalisé adapté à un objectif et une préférence alimentaire.</summary>
public class PlanRepas
{
    public long Id { get; set; }

    public string Nom         { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;

    public ObjectifSportif       Objectif   { get; set; }
    public PreferenceAlimentaire Preference { get; set; }

    /// <summary>Calories totales de la journée (kcal).</summary>
    public int CaloriesTotalesKcal { get; set; }

    public double ProteinesG { get; set; }
    public double GlucidesG  { get; set; }
    public double LipidesG   { get; set; }
    public double FibresG    { get; set; }

    public List<Repas> Repas { get; set; } = new();
}
