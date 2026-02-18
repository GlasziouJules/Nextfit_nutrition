using Microsoft.AspNetCore.Mvc;
using NextfitNutrition.Services;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/consultations")]
public class ConsultationsController : ControllerBase
{
    private readonly ConsultationService _service;

    public ConsultationsController(ConsultationService service) => _service = service;

    /// <summary>
    /// POST /api/consultations
    /// Body: { "utilisateurId": 1, "dateHeure": "2024-04-15T10:00:00", "dureeMins": 30 }
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> Reserver([FromBody] ReservationRequest req)
    {
        try
        {
            var result = await _service.ReserverAsync(req.UtilisateurId, req.DateHeure, req.DureeMins);
            return Created($"/api/consultations/{result.Id}", result);
        }
        catch (ArgumentException ex)    { return BadRequest(new { erreur = ex.Message }); }
        catch (InvalidOperationException ex) { return Conflict(new { erreur = ex.Message }); }
    }

    /// <summary>GET /api/consultations?utilisateurId=1</summary>
    [HttpGet]
    public async Task<IActionResult> Get([FromQuery] long utilisateurId)
        => Ok(await _service.GetParUtilisateurAsync(utilisateurId));

    /// <summary>GET /api/consultations/prochaines?utilisateurId=1</summary>
    [HttpGet("prochaines")]
    public async Task<IActionResult> Prochaines([FromQuery] long utilisateurId)
        => Ok(await _service.GetProchainesAsync(utilisateurId));

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(long id)
    {
        var c = await _service.GetByIdAsync(id);
        return c is null ? NotFound() : Ok(c);
    }

    /// <summary>DELETE /api/consultations/42 — annule la consultation.</summary>
    [HttpDelete("{id}")]
    public async Task<IActionResult> Annuler(long id)
    {
        try
        {
            return Ok(await _service.AnnulerAsync(id));
        }
        catch (ArgumentException ex)     { return NotFound(new { erreur = ex.Message }); }
        catch (InvalidOperationException ex) { return Conflict(new { erreur = ex.Message }); }
    }

    /// <summary>DTO pour la réservation.</summary>
    public record ReservationRequest(long UtilisateurId, DateTime DateHeure, int DureeMins = 30);
}
