using System.ComponentModel.DataAnnotations;

namespace NextfitNutrition.Models;

public enum TypeAbonnement { STANDARD, PREMIUM, VIP }

/// <summary>Représente un membre de la salle de sport NextFit.</summary>
public class Utilisateur
{
    public long Id { get; set; }

    [Required] public string Prenom { get; set; } = string.Empty;
    [Required] public string Nom    { get; set; } = string.Empty;

    [Required, EmailAddress]
    public string Email { get; set; } = string.Empty;

    public DateOnly? DateNaissance { get; set; }

    /// <summary>Taille en centimètres.</summary>
    public double? TailleCm { get; set; }

    /// <summary>Poids en kilogrammes.</summary>
    public double? PoidsKg { get; set; }

    public TypeAbonnement Abonnement { get; set; } = TypeAbonnement.STANDARD;

    /// <summary>Hash SHA-256 du mot de passe (hex).</summary>
    public string? MotDePasseHash { get; set; }

    // --- Navigation EF Core ---
    public QuestionnaireAlimentaire? Questionnaire { get; set; }
    public List<EntreeMacro>          EntreesMacros { get; set; } = new();
    public List<ConsultationDieteticien> Consultations { get; set; } = new();
}
