using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Data;
using NextfitNutrition.Services;

var builder = WebApplication.CreateBuilder(args);

// ---- Contrôleurs REST avec sérialisation JSON ----
builder.Services.AddControllers().AddJsonOptions(opts =>
{
    // camelCase pour les propriétés JSON (ex: CaloriesKcal → caloriesKcal)
    opts.JsonSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
    // Énumérations sérialisées en texte (ex: PRISE_DE_MASSE au lieu de 0)
    opts.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter());
    // Gestion des références circulaires (ex: Utilisateur ↔ EntreeMacro)
    opts.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;
    opts.JsonSerializerOptions.WriteIndented = false;
});

// ---- Base de données InMemory (pas besoin d'installer SQL Server) ----
builder.Services.AddDbContext<NutritionDbContext>(opt =>
    opt.UseInMemoryDatabase("nextfit_nutrition"));

// ---- Services métier ----
builder.Services.AddScoped<QuestionnaireService>();
builder.Services.AddScoped<PlanRepasService>();
builder.Services.AddScoped<MacroTrackingService>();
builder.Services.AddScoped<ArticleService>();
builder.Services.AddScoped<ConsultationService>();
builder.Services.AddScoped<DataInitializer>();

// ---- CORS (autorise toutes les origines pour le dev) ----
builder.Services.AddCors(opts =>
    opts.AddDefaultPolicy(p => p.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));

var app = builder.Build();

app.UseCors();

// Sert les fichiers statiques du dossier wwwroot/ (index.html, css, js)
app.UseDefaultFiles();
app.UseStaticFiles();

app.MapControllers();

// ---- Initialisation des données de démo ----
using (var scope = app.Services.CreateScope())
{
    var seeder = scope.ServiceProvider.GetRequiredService<DataInitializer>();
    await seeder.SeedAsync();
}

app.Run();
