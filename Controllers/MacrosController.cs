using Microsoft.AspNetCore.Mvc;
using NextfitNutrition.Models;
using NextfitNutrition.Services;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/utilisateurs/{utilisateurId}/macros")]
public class MacrosController : ControllerBase
{
    private readonly MacroTrackingService _service;

    public MacrosController(MacroTrackingService service) => _service = service;

    /// <summary>POST /api/utilisateurs/1/macros — enregistre un aliment.</summary>
    [HttpPost]
    public async Task<IActionResult> Enregistrer(long utilisateurId,
                                                   [FromBody] EntreeMacro entree)
    {
        try
        {
            var result = await _service.EnregistrerAsync(utilisateurId, entree);
            return Created($"/api/utilisateurs/{utilisateurId}/macros/{result.Id}", result);
        }
        catch (ArgumentException ex) { return BadRequest(new { erreur = ex.Message }); }
    }

    /// <summary>GET /api/utilisateurs/1/macros?date=2024-03-15</summary>
    [HttpGet]
    public async Task<IActionResult> GetParJour(long utilisateurId,
                                                 [FromQuery] string? date)
    {
        var jour = date is not null
            ? DateOnly.Parse(date)
            : DateOnly.FromDateTime(DateTime.Today);
        return Ok(await _service.GetParJourAsync(utilisateurId, jour));
    }

    /// <summary>GET /api/utilisateurs/1/macros/resume?date=2024-03-15</summary>
    [HttpGet("resume")]
    public async Task<IActionResult> Resume(long utilisateurId,
                                             [FromQuery] string? date)
    {
        var jour = date is not null
            ? DateOnly.Parse(date)
            : DateOnly.FromDateTime(DateTime.Today);
        return Ok(await _service.ResumeJourneeAsync(utilisateurId, jour));
    }

    /// <summary>GET /api/utilisateurs/1/macros/historique?debut=...&amp;fin=...</summary>
    [HttpGet("historique")]
    public async Task<IActionResult> Historique(long utilisateurId,
                                                  [FromQuery] string debut,
                                                  [FromQuery] string fin)
        => Ok(await _service.GetParPeriodeAsync(utilisateurId,
                DateOnly.Parse(debut), DateOnly.Parse(fin)));

    /// <summary>DELETE /api/utilisateurs/1/macros/42</summary>
    [HttpDelete("{entreeId}")]
    public async Task<IActionResult> Supprimer(long utilisateurId, long entreeId)
    {
        await _service.SupprimerAsync(entreeId);
        return NoContent();
    }
}
