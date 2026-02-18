using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Services;

/// <summary>
/// Service de gestion des consultations diététicien en ligne.
/// Les abonnés VIP ont leurs consultations incluses.
/// </summary>
public class ConsultationService
{
    private readonly NutritionDbContext _ctx;

    public ConsultationService(NutritionDbContext ctx) => _ctx = ctx;

    /// <summary>Réserve un créneau. Vérifie que le créneau n'est pas déjà pris.</summary>
    public async Task<ConsultationDieteticien> ReserverAsync(long utilisateurId,
                                                               DateTime dateHeure,
                                                               int dureeMins = 30)
    {
        if (await _ctx.Consultations.AnyAsync(c => c.DateHeure == dateHeure))
            throw new InvalidOperationException($"Ce créneau est déjà réservé : {dateHeure:g}");

        var utilisateur = await _ctx.Utilisateurs.FindAsync(utilisateurId)
            ?? throw new ArgumentException($"Utilisateur introuvable : {utilisateurId}");

        bool estVip = utilisateur.TypeAbonnement == Utilisateur.TypeAbonnement.VIP;

        var consultation = new ConsultationDieteticien
        {
            UtilisateurId   = utilisateurId,
            DateHeure       = dateHeure,
            DureeMins       = dureeMins,
            Statut          = ConsultationDieteticien.StatutConsultation.CONFIRMEE,
            NomDieteticien  = "Dr. Marie Dupont — Diététicienne agréée",
            LienVisio       = $"https://visio.nextfit.fr/session/{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}",
            Incluse         = estVip,
            PrixEuros       = estVip ? 0.0 : 45.0
        };

        _ctx.Consultations.Add(consultation);
        await _ctx.SaveChangesAsync();
        return consultation;
    }

    /// <summary>Annule une consultation.</summary>
    public async Task<ConsultationDieteticien> AnnulerAsync(long consultationId)
    {
        var consultation = await _ctx.Consultations.FindAsync(consultationId)
            ?? throw new ArgumentException($"Consultation introuvable : {consultationId}");

        if (consultation.Statut == ConsultationDieteticien.StatutConsultation.TERMINEE)
            throw new InvalidOperationException("Impossible d'annuler une consultation déjà terminée.");

        consultation.Statut = ConsultationDieteticien.StatutConsultation.ANNULEE;
        await _ctx.SaveChangesAsync();
        return consultation;
    }

    public async Task<List<ConsultationDieteticien>> GetParUtilisateurAsync(long utilisateurId)
        => await _ctx.Consultations
            .Where(c => c.UtilisateurId == utilisateurId)
            .OrderByDescending(c => c.DateHeure)
            .ToListAsync();

    public async Task<List<ConsultationDieteticien>> GetProchainesAsync(long utilisateurId)
        => await _ctx.Consultations
            .Where(c => c.UtilisateurId == utilisateurId && c.DateHeure > DateTime.Now)
            .OrderBy(c => c.DateHeure)
            .ToListAsync();

    public async Task<ConsultationDieteticien?> GetByIdAsync(long id)
        => await _ctx.Consultations.FindAsync(id);
}
