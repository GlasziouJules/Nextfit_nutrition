using System.ComponentModel.DataAnnotations;

namespace NextfitNutrition.Models;

/// <summary>
/// Réservation d'une consultation en ligne avec le diététicien partenaire.
/// Incluse dans l'abonnement VIP, option payante pour les autres.
/// </summary>
public class ConsultationDieteticien
{
    public long Id { get; set; }

    public long         UtilisateurId { get; set; }
    public Utilisateur? Utilisateur   { get; set; }

    [Required] public DateTime DateHeure { get; set; }

    /// <summary>Durée de la consultation en minutes (30 ou 60).</summary>
    public int DureeMins { get; set; } = 30;

    public StatutConsultation Statut       { get; set; } = StatutConsultation.EN_ATTENTE;
    public string?            NomDieteticien { get; set; }
    public string?            LienVisio      { get; set; }
    public string?            NotesConsultation { get; set; }

    /// <summary>true si incluse dans l'abonnement (VIP).</summary>
    public bool   Incluse    { get; set; }
    public double PrixEuros  { get; set; } = 45.0;

    public enum StatutConsultation { EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE }
}
