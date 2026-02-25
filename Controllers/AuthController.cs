using System.Security.Cryptography;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Models;

namespace NextfitNutrition.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly NutritionDbContext _ctx;

    public AuthController(NutritionDbContext ctx) => _ctx = ctx;

    // ---------------------------------------------------------------
    // POST /api/auth/login
    // Body: { "email": "...", "motDePasse": "..." }
    // ---------------------------------------------------------------
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Email) || string.IsNullOrWhiteSpace(dto.MotDePasse))
            return BadRequest(new { message = "Email et mot de passe requis." });

        var hash = HashPassword(dto.MotDePasse);
        var user = await _ctx.Utilisateurs
            .FirstOrDefaultAsync(u => u.Email == dto.Email && u.MotDePasseHash == hash);

        if (user == null)
            return Unauthorized(new { message = "Email ou mot de passe incorrect." });

        return Ok(new
        {
            id         = user.Id,
            prenom     = user.Prenom,
            nom        = user.Nom,
            email      = user.Email,
            abonnement = user.Abonnement.ToString()
        });
    }

    // ---------------------------------------------------------------
    // POST /api/auth/register
    // Body: { "prenom": "...", "nom": "...", "email": "...", "motDePasse": "..." }
    // ---------------------------------------------------------------
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Prenom) || string.IsNullOrWhiteSpace(dto.Nom)
            || string.IsNullOrWhiteSpace(dto.Email) || string.IsNullOrWhiteSpace(dto.MotDePasse))
            return BadRequest(new { message = "Tous les champs sont requis." });

        if (dto.MotDePasse.Length < 6)
            return BadRequest(new { message = "Le mot de passe doit contenir au moins 6 caractères." });

        if (await _ctx.Utilisateurs.AnyAsync(u => u.Email == dto.Email))
            return Conflict(new { message = "Cet email est déjà utilisé." });

        var user = new Utilisateur
        {
            Prenom          = dto.Prenom,
            Nom             = dto.Nom,
            Email           = dto.Email,
            MotDePasseHash  = HashPassword(dto.MotDePasse),
            Abonnement      = TypeAbonnement.STANDARD
        };

        _ctx.Utilisateurs.Add(user);
        await _ctx.SaveChangesAsync();

        return Created($"/api/utilisateurs/{user.Id}", new
        {
            id         = user.Id,
            prenom     = user.Prenom,
            nom        = user.Nom,
            email      = user.Email,
            abonnement = user.Abonnement.ToString()
        });
    }

    // ---------------------------------------------------------------
    // Utilitaire : hash SHA-256 du mot de passe
    // ---------------------------------------------------------------
    public static string HashPassword(string password)
    {
        byte[] bytes = SHA256.HashData(Encoding.UTF8.GetBytes(password));
        return Convert.ToHexString(bytes).ToLower();
    }
}

// ---------------------------------------------------------------
// DTOs
// ---------------------------------------------------------------
public record LoginDto(string Email, string MotDePasse);
public record RegisterDto(string Prenom, string Nom, string Email, string MotDePasse);
