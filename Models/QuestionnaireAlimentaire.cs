namespace NextfitNutrition.Models;

/// <summary>
/// Questionnaire alimentaire rempli par le membre pour personnaliser son plan nutritionnel.
/// Regroupe allergies, préférences et objectifs sportifs.
/// </summary>
public class QuestionnaireAlimentaire
{
    public long Id { get; set; }

    public long UtilisateurId { get; set; }
    public Utilisateur? Utilisateur { get; set; }

    // --- Allergies ---
    public bool AllergiqueGluten    { get; set; }
    public bool AllergiqueLactose   { get; set; }
    public bool AllergiqueNoix      { get; set; }
    public bool AllergiqueOeufs     { get; set; }
    public bool AllergiqueFruitsMer { get; set; }
    public string? AutresAllergies  { get; set; }

    // --- Préférences et objectifs ---
    public PreferenceAlimentaire Preference     { get; set; } = PreferenceAlimentaire.OMNIVORE;
    public ObjectifSportif       Objectif       { get; set; } = ObjectifSportif.EQUILIBRE;
    public NiveauActivite        NiveauActivite { get; set; } = NiveauActivite.MODEREMENT_ACTIF;

    public int  NombreRepasParJour  { get; set; } = 3;
    public int? CaloriesCiblesKcal  { get; set; }

    // --- Énumérations ---
    public enum PreferenceAlimentaire
    {
        OMNIVORE, VEGETARIEN, VEGAN, PESCETARIEN, FLEXITARIEN
    }

    public enum ObjectifSportif
    {
        PRISE_DE_MASSE, SECHE, EQUILIBRE, PERTE_DE_POIDS, MAINTIEN
    }

    public enum NiveauActivite
    {
        SEDENTAIRE, LEGEREMENT_ACTIF, MODEREMENT_ACTIF, TRES_ACTIF, EXTREMEMENT_ACTIF
    }
}
