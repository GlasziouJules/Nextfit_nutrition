using Microsoft.AspNetCore.Mvc;
using NextfitNutrition.Models;
using NextfitNutrition.Services;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/articles")]
public class ArticlesController : ControllerBase
{
    private readonly ArticleService _service;

    public ArticlesController(ArticleService service) => _service = service;

    /// <summary>
    /// GET /api/articles                       — articles publics
    /// GET /api/articles?utilisateurId=1       — articles selon abonnement
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> Get([FromQuery] long? utilisateurId)
    {
        var articles = utilisateurId.HasValue
            ? await _service.GetPourUtilisateurAsync(utilisateurId.Value)
            : await _service.GetPublicsAsync();
        return Ok(articles);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(long id)
    {
        var a = await _service.GetByIdAsync(id);
        return a is null ? NotFound() : Ok(a);
    }

    /// <summary>GET /api/articles/categorie/HYDRATATION</summary>
    [HttpGet("categorie/{categorie}")]
    public async Task<IActionResult> ParCategorie(ArticleNutrition.CategorieNutrition categorie)
        => Ok(await _service.GetParCategorieAsync(categorie));

    /// <summary>GET /api/articles/type/VIDEO</summary>
    [HttpGet("type/{type}")]
    public async Task<IActionResult> ParType(ArticleNutrition.TypeContenu type)
        => Ok(await _service.GetParTypeAsync(type));
}
