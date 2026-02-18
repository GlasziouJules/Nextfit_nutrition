using Microsoft.EntityFrameworkCore;
using NextfitNutrition.Models;

namespace NextfitNutrition.Data;

/// <summary>Contexte EF Core — point d'accès à la base de données InMemory.</summary>
public class NutritionDbContext : DbContext
{
    public NutritionDbContext(DbContextOptions<NutritionDbContext> options) : base(options) { }

    public DbSet<Utilisateur>            Utilisateurs  => Set<Utilisateur>();
    public DbSet<QuestionnaireAlimentaire> Questionnaires => Set<QuestionnaireAlimentaire>();
    public DbSet<PlanRepas>              PlansRepas    => Set<PlanRepas>();
    public DbSet<Repas>                  RepasItems    => Set<Repas>();
    public DbSet<EntreeMacro>            EntreesMacros => Set<EntreeMacro>();
    public DbSet<ArticleNutrition>       Articles      => Set<ArticleNutrition>();
    public DbSet<ConsultationDieteticien> Consultations => Set<ConsultationDieteticien>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Un utilisateur a un seul questionnaire
        modelBuilder.Entity<Utilisateur>()
            .HasOne(u => u.Questionnaire)
            .WithOne(q => q.Utilisateur)
            .HasForeignKey<QuestionnaireAlimentaire>(q => q.UtilisateurId);

        // Un utilisateur a plusieurs entrées macros
        modelBuilder.Entity<Utilisateur>()
            .HasMany(u => u.EntreesMacros)
            .WithOne(e => e.Utilisateur)
            .HasForeignKey(e => e.UtilisateurId);

        // Un utilisateur a plusieurs consultations
        modelBuilder.Entity<Utilisateur>()
            .HasMany(u => u.Consultations)
            .WithOne(c => c.Utilisateur)
            .HasForeignKey(c => c.UtilisateurId);

        // Un plan de repas a plusieurs repas
        modelBuilder.Entity<PlanRepas>()
            .HasMany(p => p.Repas)
            .WithOne(r => r.PlanRepas)
            .HasForeignKey(r => r.PlanRepasId);

        // Email unique pour les utilisateurs
        modelBuilder.Entity<Utilisateur>()
            .HasIndex(u => u.Email)
            .IsUnique();
    }
}
