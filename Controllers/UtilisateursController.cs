using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/utilisateurs")]
public class UtilisateursController : ControllerBase
{
    private readonly NutritionDbContext _ctx;

    public UtilisateursController(NutritionDbContext ctx) => _ctx = ctx;

    [HttpGet]
    public async Task<List<Utilisateur>> GetAll()
        => await _ctx.Utilisateurs.ToListAsync();

    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(long id)
    {
        var u = await _ctx.Utilisateurs.FindAsync(id);
        return u is null ? NotFound() : Ok(u);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] Utilisateur utilisateur)
    {
        if (await _ctx.Utilisateurs.AnyAsync(u => u.Email == utilisateur.Email))
            return Conflict(new { erreur = "Un compte existe déjà avec cet email." });

        _ctx.Utilisateurs.Add(utilisateur);
        await _ctx.SaveChangesAsync();
        return CreatedAtAction(nameof(GetById), new { id = utilisateur.Id }, utilisateur);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> Update(long id, [FromBody] Utilisateur modifications)
    {
        var u = await _ctx.Utilisateurs.FindAsync(id);
        if (u is null) return NotFound();

        u.Prenom          = modifications.Prenom;
        u.Nom             = modifications.Nom;
        u.TailleCm        = modifications.TailleCm;
        u.PoidsKg         = modifications.PoidsKg;
        u.DateNaissance   = modifications.DateNaissance;
        u.Abonnement      = modifications.Abonnement;
        await _ctx.SaveChangesAsync();
        return Ok(u);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        var u = await _ctx.Utilisateurs.FindAsync(id);
        if (u is null) return NotFound();
        _ctx.Utilisateurs.Remove(u);
        await _ctx.SaveChangesAsync();
        return NoContent();
    }
}
