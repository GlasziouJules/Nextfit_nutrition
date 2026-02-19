using NextfitNutrition.Models;

namespace NextfitNutrition.Data;

/// <summary>Initialise la base de données avec des données de démonstration.</summary>
public class DataInitializer
{
    private readonly NutritionDbContext _ctx;

    public DataInitializer(NutritionDbContext ctx) => _ctx = ctx;

    public async Task SeedAsync()
    {
        if (_ctx.Utilisateurs.Any()) return; // Déjà initialisé

        await SeedUtilisateurs();
        await SeedPlansRepas();
        await SeedArticles();
        await _ctx.SaveChangesAsync();
    }

    // =================================================================
    // UTILISATEURS
    // =================================================================
    private async Task SeedUtilisateurs()
    {
        _ctx.Utilisateurs.AddRange(
            new Utilisateur
            {
                Prenom = "Thomas", Nom = "Dupont",
                Email = "thomas.dupont@nextfit.fr",
                TailleCm = 180, PoidsKg = 82,
                DateNaissance = new DateOnly(1995, 6, 15),
                TypeAbonnement = TypeAbonnement.VIP
            },
            new Utilisateur
            {
                Prenom = "Sarah", Nom = "Martin",
                Email = "sarah.martin@nextfit.fr",
                TailleCm = 165, PoidsKg = 62,
                DateNaissance = new DateOnly(1999, 3, 22),
                TypeAbonnement = TypeAbonnement.PREMIUM
            }
        );
        await _ctx.SaveChangesAsync();
    }

    // =================================================================
    // PLANS DE REPAS
    // =================================================================
    private Task SeedPlansRepas()
    {
        // ---- Plan 1 : Prise de masse (omnivore) ----
        var priseMasse = new PlanRepas
        {
            Nom = "Performance & Masse Musculaire",
            Description = "Plan riche en protéines pour soutenir la prise de masse musculaire avec un surplus calorique maîtrisé.",
            Objectif = ObjectifSportif.PRISE_DE_MASSE,
            Preference = PreferenceAlimentaire.OMNIVORE,
            CaloriesTotalesKcal = 2800,
            ProteinesG = 200, GlucidesG = 310, LipidesG = 75, FibresG = 35,
            Repas = new List<Repas>
            {
                new() {
                    TypeRepas = TypeRepas.PETIT_DEJEUNER,
                    Nom = "Porridge protéiné aux fruits rouges",
                    Description = "Petit-déjeuner riche en glucides complexes et protéines.",
                    CaloriesKcal = 520, ProteinesG = 35, GlucidesG = 65, LipidesG = 12, FibresG = 8,
                    Ingredients = new() { "80g flocons d'avoine", "1 scoop whey vanille (30g)", "200ml lait demi-écrémé", "100g fruits rouges", "1 c.s. graines de chia" },
                    Preparation = "Cuire l'avoine avec le lait. Hors feu, mélanger la whey. Garnir de fruits rouges et chia.",
                    TempsPreparationMin = 10
                },
                new() {
                    TypeRepas = TypeRepas.COLLATION_MATIN,
                    Nom = "Yaourt grec + amandes",
                    Description = "Collation riche en protéines et bonnes graisses.",
                    CaloriesKcal = 280, ProteinesG = 22, GlucidesG = 18, LipidesG = 10,
                    Ingredients = new() { "200g yaourt grec nature 0%", "30g amandes nature", "1 c.c. miel" },
                    TempsPreparationMin = 2
                },
                new() {
                    TypeRepas = TypeRepas.DEJEUNER,
                    Nom = "Salade de quinoa, poulet grillé et légumes de saison",
                    Description = "Déjeuner complet avec protéines de qualité, glucides complexes et vitamines.",
                    CaloriesKcal = 680, ProteinesG = 55, GlucidesG = 70, LipidesG = 18, FibresG = 10,
                    Ingredients = new() { "180g blanc de poulet", "100g quinoa cru", "80g concombre", "80g tomates cerises", "½ poivron", "30g feta", "2 c.s. huile d'olive" },
                    Preparation = "Cuire le quinoa. Griller le poulet. Mélanger tous les ingrédients et assaisonner.",
                    TempsPreparationMin = 20
                },
                new() {
                    TypeRepas = TypeRepas.COLLATION_APRES_MIDI,
                    Nom = "Shake post-entraînement BCAA & banane",
                    Description = "Récupération rapide avec protéines et glucides simples après la séance.",
                    CaloriesKcal = 340, ProteinesG = 35, GlucidesG = 38, LipidesG = 4,
                    Ingredients = new() { "1 scoop whey (30g)", "1 banane", "300ml eau froide", "BCAA Optimum Nutrition (5g)" },
                    Preparation = "Mixer tous les ingrédients. Consommer dans les 30 min après l'entraînement.",
                    TempsPreparationMin = 3
                },
                new() {
                    TypeRepas = TypeRepas.DINER,
                    Nom = "Filet de saumon, patate douce et brocolis vapeur",
                    Description = "Dîner équilibré riche en oméga-3, glucides complexes et fibres.",
                    CaloriesKcal = 620, ProteinesG = 48, GlucidesG = 55, LipidesG = 18, FibresG = 12,
                    Ingredients = new() { "200g filet de saumon", "250g patate douce", "200g brocolis", "1 c.s. huile d'olive", "herbes de Provence" },
                    Preparation = "Cuire le saumon au four (180°C, 15 min). Cuire la patate douce et brocolis vapeur.",
                    TempsPreparationMin = 25
                }
            }
        };

        // ---- Plan 2 : Sèche (omnivore) ----
        var seche = new PlanRepas
        {
            Nom = "Définition Musculaire — Sèche",
            Description = "Plan hypocalorique riche en protéines pour conserver la masse musculaire tout en éliminant la graisse.",
            Objectif = ObjectifSportif.SECHE,
            Preference = PreferenceAlimentaire.OMNIVORE,
            CaloriesTotalesKcal = 1800,
            ProteinesG = 180, GlucidesG = 135, LipidesG = 60, FibresG = 40,
            Repas = new List<Repas>
            {
                new() {
                    TypeRepas = TypeRepas.PETIT_DEJEUNER,
                    Nom = "Œufs brouillés, épinards et tartine de seigle",
                    Description = "Petit-déjeuner protéiné à faible indice glycémique.",
                    CaloriesKcal = 380, ProteinesG = 32, GlucidesG = 28, LipidesG = 14,
                    Ingredients = new() { "3 œufs entiers", "80g épinards frais", "2 tranches pain de seigle", "1 c.c. huile de coco" },
                    TempsPreparationMin = 10
                },
                new() {
                    TypeRepas = TypeRepas.DEJEUNER,
                    Nom = "Thon, légumineuses et crudités",
                    Description = "Déjeuner pauvre en graisses, riche en fibres et protéines maigres.",
                    CaloriesKcal = 500, ProteinesG = 52, GlucidesG = 40, LipidesG = 12,
                    Ingredients = new() { "160g thon au naturel", "100g lentilles cuites", "1 carotte râpée", "½ concombre", "2 c.s. vinaigre balsamique" },
                    TempsPreparationMin = 10
                },
                new() {
                    TypeRepas = TypeRepas.DINER,
                    Nom = "Poulet vapeur, haricots verts et riz complet",
                    Description = "Dîner léger riche en protéines et fibres pour favoriser la récupération.",
                    CaloriesKcal = 520, ProteinesG = 55, GlucidesG = 45, LipidesG = 12,
                    Ingredients = new() { "200g escalope de poulet", "200g haricots verts", "80g riz complet cru" },
                    TempsPreparationMin = 20
                }
            }
        };

        // ---- Plan 3 : Équilibre (végétarien) ----
        var equilibre = new PlanRepas
        {
            Nom = "Alimentation Équilibrée — Végétarien",
            Description = "Plan végétarien varié et nutritionnellement complet pour un mode de vie sain.",
            Objectif = ObjectifSportif.EQUILIBRE,
            Preference = PreferenceAlimentaire.VEGETARIEN,
            CaloriesTotalesKcal = 2100,
            ProteinesG = 90, GlucidesG = 270, LipidesG = 70, FibresG = 45,
            Repas = new List<Repas>
            {
                new() {
                    TypeRepas = TypeRepas.PETIT_DEJEUNER,
                    Nom = "Bowl açaï énergétique",
                    Description = "Petit-déjeuner veggie coloré, riche en antioxydants et fibres.",
                    CaloriesKcal = 480, ProteinesG = 15, GlucidesG = 70, LipidesG = 16,
                    Ingredients = new() { "200g açaï", "1 banane", "150ml lait d'amande", "30g granola", "1 c.s. beurre d'amande" },
                    TempsPreparationMin = 5
                },
                new() {
                    TypeRepas = TypeRepas.DEJEUNER,
                    Nom = "Curry de pois chiches, épinards et riz basmati",
                    Description = "Déjeuner végétarien complet avec protéines végétales et glucides complexes.",
                    CaloriesKcal = 620, ProteinesG = 28, GlucidesG = 95, LipidesG = 14,
                    Ingredients = new() { "200g pois chiches cuits", "150g épinards", "120g riz basmati", "200ml lait de coco allégé", "curry, tomates" },
                    TempsPreparationMin = 25
                },
                new() {
                    TypeRepas = TypeRepas.DINER,
                    Nom = "Omelette aux légumes et salade verte",
                    Description = "Dîner léger et protéiné, facile à préparer.",
                    CaloriesKcal = 440, ProteinesG = 30, GlucidesG = 25, LipidesG = 24,
                    Ingredients = new() { "4 œufs", "½ courgette", "½ poivron rouge", "30g gruyère râpé", "salade verte" },
                    TempsPreparationMin = 15
                }
            }
        };

        // ---- Plan 4 : Vegan — Prise de masse ----
        var veganMasse = new PlanRepas
        {
            Nom = "Force & Masse — 100% Vegan",
            Description = "Plan vegan complet pour la prise de masse, riche en protéines végétales.",
            Objectif = ObjectifSportif.PRISE_DE_MASSE,
            Preference = PreferenceAlimentaire.VEGAN,
            CaloriesTotalesKcal = 2700,
            ProteinesG = 155, GlucidesG = 320, LipidesG = 85, FibresG = 55,
            Repas = new List<Repas>
            {
                new() {
                    TypeRepas = TypeRepas.PETIT_DEJEUNER,
                    Nom = "Overnight oats protéinés (vegan)",
                    Description = "Avoine préparée la veille avec protéines végétales et fruits secs.",
                    CaloriesKcal = 540, ProteinesG = 32, GlucidesG = 72, LipidesG = 14,
                    Ingredients = new() { "80g flocons d'avoine", "1 scoop protéine végane pois", "250ml lait végétal", "2 c.s. beurre de cacahuète", "1 banane" },
                    Preparation = "Mélanger la veille et réfrigérer. Garnir le matin.",
                    TempsPreparationMin = 5
                },
                new() {
                    TypeRepas = TypeRepas.DEJEUNER,
                    Nom = "Buddha bowl tempeh, quinoa et avocat",
                    Description = "Déjeuner vegan complet avec protéines fermentées, bonnes graisses et fibres.",
                    CaloriesKcal = 720, ProteinesG = 45, GlucidesG = 80, LipidesG = 28,
                    Ingredients = new() { "150g tempeh", "100g quinoa", "½ avocat", "80g edamame", "carottes râpées", "graines de sésame" },
                    TempsPreparationMin = 20
                }
            }
        };

        _ctx.PlansRepas.AddRange(priseMasse, seche, equilibre, veganMasse);
        return Task.CompletedTask;
    }

    // =================================================================
    // ARTICLES
    // =================================================================
    private Task SeedArticles()
    {
        _ctx.Articles.AddRange(
            new ArticleNutrition
            {
                Titre = "L'hydratation : votre alliée performance #1",
                Resume = "Découvrez pourquoi boire suffisamment d'eau est aussi important que votre entraînement.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.HYDRATATION,
                Auteur = "Dr. Marie Dupont, Diététicienne agréée",
                Contenu = "L'eau représente 60% de notre poids corporel. Une déshydratation de seulement 2% entraîne une baisse de performance de 10 à 20%. Buvez 500 ml à 1L d'eau par heure pendant l'entraînement.",
                Tags = new() { "eau", "performance", "récupération" },
                DatePublication = new DateOnly(2024, 1, 15),
                ReserveVip = false
            },
            new ArticleNutrition
            {
                Titre = "Whey Myprotein vs BCAA Optimum Nutrition : que choisir ?",
                Resume = "Guide comparatif pour choisir les meilleurs compléments selon vos objectifs sportifs.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.COMPLEMENTS_ALIMENTAIRES,
                Auteur = "Coach Nutrition NextFit",
                Contenu = "La whey protéine (Myprotein) est idéale post-entraînement. Les BCAA (Optimum Nutrition) sont utiles pendant l'effort pour limiter le catabolisme musculaire.",
                Tags = new() { "whey", "BCAA", "myprotein", "compléments" },
                DatePublication = new DateOnly(2024, 2, 3),
                ReserveVip = false
            },
            new ArticleNutrition
            {
                Titre = "Les compléments alimentaires expliqués en 5 minutes",
                Resume = "Vidéo récapitulative sur les principaux compléments : whey, créatine, BCAA, vitamines.",
                TypeContenu = TypeContenu.VIDEO,
                Categorie = CategorieNutrition.COMPLEMENTS_ALIMENTAIRES,
                Auteur = "NextFit TV",
                DatePublication = new DateOnly(2024, 2, 20),
                ReserveVip = false
            },
            new ArticleNutrition
            {
                Titre = "Les fibres alimentaires : pourquoi et comment en manger plus ?",
                Resume = "Les fibres sont essentielles pour la santé digestive, la satiété et la performance sportive.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.FIBRES,
                Auteur = "Dr. Marie Dupont, Diététicienne agréée",
                Contenu = "L'OMS recommande 25 à 35g de fibres par jour. Meilleures sources : légumineuses, graines de chia, avoine, amandes, légumes verts.",
                Tags = new() { "fibres", "digestion", "satiété" },
                DatePublication = new DateOnly(2024, 3, 1),
                ReserveVip = false
            },
            new ArticleNutrition
            {
                Titre = "5 stratégies infaillibles pour gérer les fringales",
                Resume = "Craquez-vous souvent pour des aliments sucrés ? Voici comment reprendre le contrôle.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.GESTION_FRINGALES,
                Auteur = "Coach Nutrition NextFit",
                Contenu = "1. Augmenter les protéines. 2. Manger toutes les 3-4h. 3. Boire un verre d'eau. 4. Avoir des collations saines. 5. Manger lentement.",
                Tags = new() { "fringales", "satiété", "grignotage" },
                DatePublication = new DateOnly(2024, 3, 10),
                ReserveVip = false
            },
            new ArticleNutrition
            {
                Titre = "Plan nutritionnel personnalisé : analyse complète sur 4 semaines",
                Resume = "Programme exclusif VIP avec suivi hebdomadaire et ajustements selon vos résultats.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.PROTEINES,
                Auteur = "Dr. Marie Dupont, Diététicienne agréée",
                DatePublication = new DateOnly(2024, 3, 15),
                ReserveVip = true
            },
            new ArticleNutrition
            {
                Titre = "Que manger avant une séance de musculation ?",
                Resume = "Le bon repas pré-entraînement peut faire toute la différence sur vos performances.",
                TypeContenu = TypeContenu.ARTICLE,
                Categorie = CategorieNutrition.AVANT_SPORT,
                Auteur = "Coach Nutrition NextFit",
                Contenu = "Le repas pré-entraînement doit être pris 1h30-2h avant. Il doit contenir des glucides complexes et des protéines.",
                Tags = new() { "pré-entraînement", "énergie", "performance" },
                DatePublication = new DateOnly(2024, 3, 20),
                ReserveVip = false
            }
        );
        return Task.CompletedTask;
    }
}
