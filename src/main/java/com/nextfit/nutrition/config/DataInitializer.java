package com.nextfit.nutrition.config;

import com.nextfit.nutrition.model.*;
import com.nextfit.nutrition.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Initialise la base de données avec des données de démonstration au démarrage.
 * Crée un utilisateur de test, des plans de repas et des articles nutritionnels.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepo;
    private final PlanRepasRepository planRepasRepo;
    private final ArticleNutritionRepository articleRepo;

    public DataInitializer(UtilisateurRepository utilisateurRepo,
                            PlanRepasRepository planRepasRepo,
                            ArticleNutritionRepository articleRepo) {
        this.utilisateurRepo = utilisateurRepo;
        this.planRepasRepo   = planRepasRepo;
        this.articleRepo     = articleRepo;
    }

    @Override
    public void run(String... args) {
        creerUtilisateurDemo();
        creerPlansRepas();
        creerArticles();
    }

    // ====================================================================
    // UTILISATEUR DE DÉMO
    // ====================================================================

    private void creerUtilisateurDemo() {
        if (utilisateurRepo.existsByEmail("thomas.dupont@nextfit.fr")) return;

        Utilisateur thomas = new Utilisateur("Thomas", "Dupont", "thomas.dupont@nextfit.fr");
        thomas.setTailleCm(180.0);
        thomas.setPoidsKg(82.0);
        thomas.setDateNaissance(LocalDate.of(1995, 6, 15));
        thomas.setTypeAbonnement(Utilisateur.TypeAbonnement.VIP);
        utilisateurRepo.save(thomas);

        Utilisateur sarah = new Utilisateur("Sarah", "Martin", "sarah.martin@nextfit.fr");
        sarah.setTailleCm(165.0);
        sarah.setPoidsKg(62.0);
        sarah.setDateNaissance(LocalDate.of(1999, 3, 22));
        sarah.setTypeAbonnement(Utilisateur.TypeAbonnement.PREMIUM);
        utilisateurRepo.save(sarah);
    }

    // ====================================================================
    // PLANS DE REPAS
    // ====================================================================

    private void creerPlansRepas() {
        if (planRepasRepo.count() > 0) return;

        // ---- Plan 1 : Prise de masse (omnivore) ----
        PlanRepas priseMasse = new PlanRepas(
                "Performance & Masse Musculaire",
                "Plan riche en protéines pour soutenir la prise de masse musculaire avec un surplus calorique maîtrisé.",
                QuestionnaireAlimentaire.ObjectifSportif.PRISE_DE_MASSE,
                QuestionnaireAlimentaire.PreferenceAlimentaire.OMNIVORE
        );
        priseMasse.setCaloriesTotalesKcal(2800);
        priseMasse.setProteinesG(200);
        priseMasse.setGlucidesG(310);
        priseMasse.setLipidesG(75);
        priseMasse.setFibresG(35);

        Repas pd1 = new Repas(Repas.TypeRepas.PETIT_DEJEUNER,
                "Porridge protéiné aux fruits rouges",
                "Un petit-déjeuner riche en glucides complexes et protéines pour démarrer la journée.");
        pd1.setCaloriesKcal(520);
        pd1.setProteinesG(35);
        pd1.setGlucidesG(65);
        pd1.setLipidesG(12);
        pd1.setFibresG(8);
        pd1.setIngredients(List.of("80g flocons d'avoine", "1 scoop whey vanille (30g)",
                "200ml lait demi-écrémé", "100g fruits rouges", "1 c.s. graines de chia"));
        pd1.setPreparation("Cuire l'avoine avec le lait. Hors feu, mélanger la whey. Garnir de fruits rouges et chia.");
        pd1.setTempsPreparationMin(10);
        pd1.setPlanRepas(priseMasse);

        Repas col1 = new Repas(Repas.TypeRepas.COLLATION_MATIN,
                "Yaourt grec + amandes",
                "Collation riche en protéines et bonnes graisses.");
        col1.setCaloriesKcal(280);
        col1.setProteinesG(22);
        col1.setGlucidesG(18);
        col1.setLipidesG(10);
        col1.setIngredients(List.of("200g yaourt grec nature 0%", "30g amandes nature", "1 c.c. miel"));
        col1.setTempsPreparationMin(2);
        col1.setPlanRepas(priseMasse);

        Repas dej1 = new Repas(Repas.TypeRepas.DEJEUNER,
                "Salade de quinoa, poulet grillé et légumes de saison",
                "Un déjeuner complet avec protéines de qualité, glucides complexes et vitamines.");
        dej1.setCaloriesKcal(680);
        dej1.setProteinesG(55);
        dej1.setGlucidesG(70);
        dej1.setLipidesG(18);
        dej1.setFibresG(10);
        dej1.setIngredients(List.of("180g blanc de poulet", "100g quinoa cru", "80g concombre",
                "80g tomates cerises", "½ poivron", "30g feta", "2 c.s. huile d'olive", "jus de citron"));
        dej1.setPreparation("Cuire le quinoa. Griller le poulet. Mélanger tous les ingrédients et assaisonner.");
        dej1.setTempsPreparationMin(20);
        dej1.setPlanRepas(priseMasse);

        Repas col2 = new Repas(Repas.TypeRepas.COLLATION_APRES_MIDI,
                "Shake post-entraînement BCAA & banane",
                "Récupération rapide avec protéines et glucides simples après la séance.");
        col2.setCaloriesKcal(340);
        col2.setProteinesG(35);
        col2.setGlucidesG(38);
        col2.setLipidesG(4);
        col2.setIngredients(List.of("1 scoop whey (30g)", "1 banane", "300ml eau froide",
                "BCAA Optimum Nutrition (5g)"));
        col2.setPreparation("Mixer tous les ingrédients. Consommer dans les 30 minutes après l'entraînement.");
        col2.setTempsPreparationMin(3);
        col2.setPlanRepas(priseMasse);

        Repas din1 = new Repas(Repas.TypeRepas.DINER,
                "Filet de saumon, patate douce et brocolis vapeur",
                "Dîner équilibré riche en oméga-3, complexes glucidiques et fibres.");
        din1.setCaloriesKcal(620);
        din1.setProteinesG(48);
        din1.setGlucidesG(55);
        din1.setLipidesG(18);
        din1.setFibresG(12);
        din1.setIngredients(List.of("200g filet de saumon", "250g patate douce", "200g brocolis",
                "1 c.s. huile d'olive", "herbes de Provence", "sel, poivre, citron"));
        din1.setPreparation("Cuire le saumon au four (180°C, 15 min). Cuire la patate douce et les brocolis vapeur.");
        din1.setTempsPreparationMin(25);
        din1.setPlanRepas(priseMasse);

        priseMasse.setRepas(List.of(pd1, col1, dej1, col2, din1));
        planRepasRepo.save(priseMasse);

        // ---- Plan 2 : Sèche (omnivore) ----
        PlanRepas seche = new PlanRepas(
                "Définition Musculaire — Sèche",
                "Plan hypocalorique riche en protéines pour conserver la masse musculaire tout en éliminant la graisse.",
                QuestionnaireAlimentaire.ObjectifSportif.SECHE,
                QuestionnaireAlimentaire.PreferenceAlimentaire.OMNIVORE
        );
        seche.setCaloriesTotalesKcal(1800);
        seche.setProteinesG(180);
        seche.setGlucidesG(135);
        seche.setLipidesG(60);
        seche.setFibresG(40);

        Repas pdSeche = new Repas(Repas.TypeRepas.PETIT_DEJEUNER,
                "Œufs brouillés, épinards et tartine de seigle",
                "Petit-déjeuner protéiné à faible indice glycémique.");
        pdSeche.setCaloriesKcal(380);
        pdSeche.setProteinesG(32);
        pdSeche.setGlucidesG(28);
        pdSeche.setLipidesG(14);
        pdSeche.setIngredients(List.of("3 œufs entiers", "80g épinards frais", "2 tranches pain de seigle",
                "1 c.c. huile de coco", "sel, poivre, ail"));
        pdSeche.setTempsPreparationMin(10);
        pdSeche.setPlanRepas(seche);

        Repas dejSeche = new Repas(Repas.TypeRepas.DEJEUNER,
                "Thon, légumineuses et crudités",
                "Déjeuner pauvre en graisses saturées, riche en fibres et protéines maigres.");
        dejSeche.setCaloriesKcal(500);
        dejSeche.setProteinesG(52);
        dejSeche.setGlucidesG(40);
        dejSeche.setLipidesG(12);
        dejSeche.setIngredients(List.of("160g thon au naturel", "100g lentilles cuites", "1 carotte râpée",
                "½ concombre", "2 c.s. vinaigre balsamique", "1 c.s. moutarde"));
        dejSeche.setTempsPreparationMin(10);
        dejSeche.setPlanRepas(seche);

        Repas dinSeche = new Repas(Repas.TypeRepas.DINER,
                "Poulet vapeur, haricots verts et riz complet",
                "Dîner léger riche en protéines et fibres pour favoriser la récupération.");
        dinSeche.setCaloriesKcal(520);
        dinSeche.setProteinesG(55);
        dinSeche.setGlucidesG(45);
        dinSeche.setLipidesG(12);
        dinSeche.setIngredients(List.of("200g escalope de poulet", "200g haricots verts",
                "80g riz complet cru", "1 c.c. huile d'olive", "herbes fraîches"));
        dinSeche.setTempsPreparationMin(20);
        dinSeche.setPlanRepas(seche);

        seche.setRepas(List.of(pdSeche, dejSeche, dinSeche));
        planRepasRepo.save(seche);

        // ---- Plan 3 : Équilibre (végétarien) ----
        PlanRepas equilibre = new PlanRepas(
                "Alimentation Équilibrée — Végétarien",
                "Plan végétarien varié et nutritionnellement complet pour un mode de vie sain et durable.",
                QuestionnaireAlimentaire.ObjectifSportif.EQUILIBRE,
                QuestionnaireAlimentaire.PreferenceAlimentaire.VEGETARIEN
        );
        equilibre.setCaloriesTotalesKcal(2100);
        equilibre.setProteinesG(90);
        equilibre.setGlucidesG(270);
        equilibre.setLipidesG(70);
        equilibre.setFibresG(45);

        Repas pdEq = new Repas(Repas.TypeRepas.PETIT_DEJEUNER,
                "Bowl açaï énergétique",
                "Petit-déjeuner veggie coloré, riche en antioxydants et fibres.");
        pdEq.setCaloriesKcal(480);
        pdEq.setProteinesG(15);
        pdEq.setGlucidesG(70);
        pdEq.setLipidesG(16);
        pdEq.setIngredients(List.of("200g açaï", "1 banane", "150ml lait d'amande",
                "30g granola", "1 c.s. beurre d'amande", "fruits frais"));
        pdEq.setTempsPreparationMin(5);
        pdEq.setPlanRepas(equilibre);

        Repas dejEq = new Repas(Repas.TypeRepas.DEJEUNER,
                "Curry de pois chiches, épinards et riz basmati",
                "Déjeuner végétarien complet avec protéines végétales et glucides complexes.");
        dejEq.setCaloriesKcal(620);
        dejEq.setProteinesG(28);
        dejEq.setGlucidesG(95);
        dejEq.setLipidesG(14);
        dejEq.setIngredients(List.of("200g pois chiches cuits", "150g épinards", "120g riz basmati",
                "200ml lait de coco allégé", "1 c.s. curry", "tomates concassées"));
        dejEq.setTempsPreparationMin(25);
        dejEq.setPlanRepas(equilibre);

        Repas dinEq = new Repas(Repas.TypeRepas.DINER,
                "Omelette aux légumes et salade verte",
                "Dîner léger et protéiné, facile à préparer.");
        dinEq.setCaloriesKcal(440);
        dinEq.setProteinesG(30);
        dinEq.setGlucidesG(25);
        dinEq.setLipidesG(24);
        dinEq.setIngredients(List.of("4 œufs", "½ courgette", "½ poivron rouge",
                "30g gruyère râpé", "salade verte", "vinaigrette légère"));
        dinEq.setTempsPreparationMin(15);
        dinEq.setPlanRepas(equilibre);

        equilibre.setRepas(List.of(pdEq, dejEq, dinEq));
        planRepasRepo.save(equilibre);

        // ---- Plan 4 : Vegan — Prise de masse ----
        PlanRepas veganMasse = new PlanRepas(
                "Force & Masse — 100% Vegan",
                "Plan vegan complet pour la prise de masse, riche en protéines végétales et calories de qualité.",
                QuestionnaireAlimentaire.ObjectifSportif.PRISE_DE_MASSE,
                QuestionnaireAlimentaire.PreferenceAlimentaire.VEGAN
        );
        veganMasse.setCaloriesTotalesKcal(2700);
        veganMasse.setProteinesG(155);
        veganMasse.setGlucidesG(320);
        veganMasse.setLipidesG(85);
        veganMasse.setFibresG(55);

        Repas pdVegan = new Repas(Repas.TypeRepas.PETIT_DEJEUNER,
                "Overnight oats protéinés (vegan)",
                "Avoine préparée la veille avec protéines végétales et fruits secs.");
        pdVegan.setCaloriesKcal(540);
        pdVegan.setProteinesG(32);
        pdVegan.setGlucidesG(72);
        pdVegan.setLipidesG(14);
        pdVegan.setIngredients(List.of("80g flocons d'avoine", "1 scoop protéine végane pois",
                "250ml lait végétal", "2 c.s. beurre de cacahuète", "1 banane", "30g noix de cajou"));
        pdVegan.setPreparation("Mélanger la veille et réfrigérer. Garnir de banane et noix le matin.");
        pdVegan.setTempsPreparationMin(5);
        pdVegan.setPlanRepas(veganMasse);

        Repas dejVegan = new Repas(Repas.TypeRepas.DEJEUNER,
                "Buddha bowl tempeh, quinoa et avocat",
                "Déjeuner vegan complet avec protéines fermentées, bonnes graisses et fibres.");
        dejVegan.setCaloriesKcal(720);
        dejVegan.setProteinesG(45);
        dejVegan.setGlucidesG(80);
        dejVegan.setLipidesG(28);
        dejVegan.setIngredients(List.of("150g tempeh", "100g quinoa", "½ avocat",
                "80g edamame", "carottes râpées", "graines de sésame", "sauce tamari"));
        dejVegan.setTempsPreparationMin(20);
        dejVegan.setPlanRepas(veganMasse);

        veganMasse.setRepas(List.of(pdVegan, dejVegan));
        planRepasRepo.save(veganMasse);
    }

    // ====================================================================
    // ARTICLES
    // ====================================================================

    private void creerArticles() {
        if (articleRepo.count() > 0) return;

        // --- Hydratation ---
        ArticleNutrition hydratation = new ArticleNutrition(
                "L'hydratation : votre alliée performance #1",
                "Découvrez pourquoi boire suffisamment d'eau est aussi important que votre programme d'entraînement.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.HYDRATATION
        );
        hydratation.setAuteur("Dr. Marie Dupont, Diététicienne agréée");
        hydratation.setContenu("""
                L'eau représente 60% de notre poids corporel. Une déshydratation de seulement 2%
                entraîne une baisse de performance de 10 à 20%. Pendant l'entraînement, visez
                500 ml à 1L d'eau par heure selon l'intensité. N'attendez pas la soif, elle est
                déjà un signe de déshydratation.

                Conseils pratiques :
                - Buvez 500 ml d'eau au réveil
                - Ayez toujours une bouteille avec vous
                - Urines claires = bonne hydratation
                - Après l'entraînement : 150% des pertes en eau
                """);
        hydratation.setTags(List.of("eau", "performance", "récupération"));
        hydratation.setDatePublication(LocalDate.of(2024, 1, 15));
        articleRepo.save(hydratation);

        // --- Compléments alimentaires ---
        ArticleNutrition complements = new ArticleNutrition(
                "Whey Myprotein vs BCAA Optimum Nutrition : que choisir ?",
                "Guide comparatif pour choisir les meilleurs compléments selon vos objectifs sportifs.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.COMPLEMENTS_ALIMENTAIRES
        );
        complements.setAuteur("Coach Nutrition NextFit");
        complements.setContenu("""
                La whey protéine (Myprotein Impact Whey) est idéale post-entraînement pour la
                récupération musculaire. Les BCAA (Optimum Nutrition Gold Standard) sont utiles
                pendant l'entraînement pour limiter le catabolisme.

                Recommandations :
                - Whey : 30g dans les 30 min après la séance
                - BCAA : 5-10g pendant l'effort
                - Ne remplacent PAS une alimentation équilibrée
                - Choisissez des produits sans additifs inutiles
                """);
        complements.setTags(List.of("whey", "BCAA", "myprotein", "compléments"));
        complements.setDatePublication(LocalDate.of(2024, 2, 3));
        articleRepo.save(complements);

        // --- Vidéo BCAA ---
        ArticleNutrition videoComplements = new ArticleNutrition(
                "Les compléments alimentaires expliqués en 5 minutes",
                "Vidéo récapitulative sur les principaux compléments : whey, créatine, BCAA, vitamines.",
                ArticleNutrition.TypeContenu.VIDEO,
                ArticleNutrition.CategorieNutrition.COMPLEMENTS_ALIMENTAIRES
        );
        videoComplements.setUrlVideo("https://www.youtube.com/watch?v=exemple");
        videoComplements.setAuteur("NextFit TV");
        videoComplements.setDatePublication(LocalDate.of(2024, 2, 20));
        videoComplements.setReserveVip(false);
        articleRepo.save(videoComplements);

        // --- Fibres ---
        ArticleNutrition fibres = new ArticleNutrition(
                "Les fibres alimentaires : pourquoi et comment en manger plus ?",
                "Les fibres sont essentielles pour la santé digestive, la satiété et même la performance sportive.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.FIBRES
        );
        fibres.setAuteur("Dr. Marie Dupont, Diététicienne agréée");
        fibres.setContenu("""
                Les fibres alimentaires régulent la glycémie, favorisent le transit, augmentent
                la satiété et nourrissent le microbiote intestinal. L'OMS recommande 25 à 35g/jour.

                Meilleures sources :
                - Légumineuses (lentilles, pois chiches) : 8-9g/100g
                - Graines de chia : 34g/100g
                - Avoine : 10g/100g
                - Fruits secs (amandes, noix) : 10-15g/100g
                - Légumes verts (brocolis, épinards) : 2-5g/100g
                """);
        fibres.setTags(List.of("fibres", "digestion", "satiété", "microbiote"));
        fibres.setDatePublication(LocalDate.of(2024, 3, 1));
        articleRepo.save(fibres);

        // --- Gestion fringales ---
        ArticleNutrition fringales = new ArticleNutrition(
                "5 stratégies infaillibles pour gérer les fringales",
                "Craquez-vous souvent pour des aliments sucrés ou gras ? Voici comment reprendre le contrôle.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.GESTION_FRINGALES
        );
        fringales.setAuteur("Coach Nutrition NextFit");
        fringales.setContenu("""
                Les fringales sont souvent le signe d'un manque de protéines ou de fibres,
                d'un manque de sommeil ou de stress. Voici 5 stratégies efficaces :

                1. Augmenter les protéines (elles rassasient le plus longtemps)
                2. Manger toutes les 3-4 heures (éviter les hypoglycémies)
                3. Boire un grand verre d'eau (parfois la soif se confond avec la faim)
                4. Avoir des collations saines à portée (amandes, yaourt grec)
                5. Manger lentement et sans écrans (signal de satiété = 20 min)
                """);
        fringales.setTags(List.of("fringales", "satiété", "grignotage", "perte de poids"));
        fringales.setDatePublication(LocalDate.of(2024, 3, 10));
        articleRepo.save(fringales);

        // --- Article VIP ---
        ArticleNutrition vipArticle = new ArticleNutrition(
                "Plan nutritionnel personnalisé : analyse complète sur 4 semaines",
                "Programme exclusif VIP avec suivi hebdomadaire et ajustements selon vos résultats.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.PROTEINES
        );
        vipArticle.setAuteur("Dr. Marie Dupont, Diététicienne agréée");
        vipArticle.setReserveVip(true);
        vipArticle.setDatePublication(LocalDate.of(2024, 3, 15));
        articleRepo.save(vipArticle);

        // --- Avant sport ---
        ArticleNutrition avantSport = new ArticleNutrition(
                "Que manger avant une séance de musculation ?",
                "Le bon repas pré-entraînement peut faire toute la différence sur vos performances.",
                ArticleNutrition.TypeContenu.ARTICLE,
                ArticleNutrition.CategorieNutrition.AVANT_SPORT
        );
        avantSport.setAuteur("Coach Nutrition NextFit");
        avantSport.setContenu("""
                Le repas pré-entraînement doit être pris 1h30 à 2h avant la séance. Il doit
                contenir des glucides complexes pour l'énergie et des protéines pour protéger
                la masse musculaire.

                Idées repas pré-séance :
                - Riz + poulet + légumes (2h avant)
                - Avoine + whey + banane (1h avant)
                - Pain de seigle + œufs + avocat (1h30 avant)

                À éviter : aliments gras, fibres en excès, alcool.
                """);
        avantSport.setTags(List.of("pré-entraînement", "énergie", "performance"));
        avantSport.setDatePublication(LocalDate.of(2024, 3, 20));
        articleRepo.save(avantSport);
    }
}
