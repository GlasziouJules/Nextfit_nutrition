using Microsoft.AspNetCore.Mvc;
using NextfitNutrition.Models;
using NextfitNutrition.Services;

namespace NextfitNutrition.Controllers;

/// <summary>
/// GET  /api/utilisateurs/{id}/questionnaire
/// POST /api/utilisateurs/{id}/questionnaire
/// </summary>
[ApiController]
[Route("api/utilisateurs/{utilisateurId}/questionnaire")]
public class QuestionnaireController : ControllerBase
{
    private readonly QuestionnaireService _service;

    public QuestionnaireController(QuestionnaireService service) => _service = service;

    [HttpGet]
    public async Task<IActionResult> Get(long utilisateurId)
    {
        var q = await _service.GetByUtilisateurAsync(utilisateurId);
        return q is null ? NotFound() : Ok(q);
    }

    [HttpPost]
    public async Task<IActionResult> Post(long utilisateurId,
                                          [FromBody] QuestionnaireAlimentaire questionnaire)
    {
        try
        {
            var result = await _service.SauvegarderAsync(utilisateurId, questionnaire);
            return Ok(result);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { erreur = ex.Message });
        }
    }
}
