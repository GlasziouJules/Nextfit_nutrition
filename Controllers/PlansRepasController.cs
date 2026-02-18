using Microsoft.AspNetCore.Mvc;
using NextfitNutrition.Models;
using NextfitNutrition.Services;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/plans-repas")]
public class PlansRepasController : ControllerBase
{
    private readonly PlanRepasService _service;

    public PlansRepasController(PlanRepasService service) => _service = service;

    /// <summary>GET /api/plans-repas — liste tous les plans.</summary>
    [HttpGet]
    public async Task<IActionResult> GetAll()
        => Ok(await _service.GetAllAsync());

    /// <summary>GET /api/plans-repas/{id}</summary>
    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(long id)
    {
        var plan = await _service.GetByIdAsync(id);
        return plan is null ? NotFound() : Ok(plan);
    }

    /// <summary>GET /api/plans-repas/objectif/{objectif}</summary>
    [HttpGet("objectif/{objectif}")]
    public async Task<IActionResult> ByObjectif(QuestionnaireAlimentaire.ObjectifSportif objectif)
        => Ok(await _service.GetByObjectifAsync(objectif));

    /// <summary>GET /api/plans-repas/suggestion?utilisateurId=1</summary>
    [HttpGet("suggestion")]
    public async Task<IActionResult> Suggestion([FromQuery] long utilisateurId)
    {
        var plan = await _service.SuggererPourUtilisateurAsync(utilisateurId);
        return plan is null ? NotFound() : Ok(plan);
    }
}
