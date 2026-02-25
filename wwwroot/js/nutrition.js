/**
 * NextFit — Module Nutrition
 * JavaScript principal : navigation, appels API, rendu dynamique.
 */

const API = '/api';

// ---- Auth : vérification de session ----
const _storedUser = localStorage.getItem('nf_user');
if (!_storedUser) {
    window.location.href = '/auth.html';
}
const SESSION_USER = JSON.parse(_storedUser || '{}');
let UTILISATEUR_ID = SESSION_USER.id || 1;

let currentUser = null;

// ---- Initialisation navbar ----
document.addEventListener('DOMContentLoaded', () => {
    const nameEl  = document.getElementById('nav-username');
    const badgeEl = document.getElementById('nav-badge');
    if (nameEl)  nameEl.textContent  = SESSION_USER.prenom + ' ' + (SESSION_USER.nom?.[0] || '') + '.';
    if (badgeEl && SESSION_USER.abonnement) {
        badgeEl.textContent = SESSION_USER.abonnement;
        badgeEl.style.display = 'inline';
    }

    document.getElementById('btn-logout')?.addEventListener('click', () => {
        localStorage.removeItem('nf_user');
        window.location.href = '/auth.html';
    });
});

/* =========================================================
   NAVIGATION PAR ONGLETS
   ========================================================= */

document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', e => {
        e.preventDefault();
        const section = link.dataset.section;
        showSection(section);
    });
});

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));

    const target = document.getElementById(sectionId);
    if (target) {
        target.classList.add('active');
        target.classList.remove('hidden');
    }

    const activeLink = document.querySelector(`[data-section="${sectionId}"]`);
    if (activeLink) activeLink.classList.add('active');

    // Charger le contenu à la demande
    if (sectionId === 'plans') chargerPlans();
    if (sectionId === 'tracker') initTracker();
    if (sectionId === 'conseils') chargerArticles();
    if (sectionId === 'consultation') chargerConsultations();
}

/* =========================================================
   QUESTIONNAIRE
   ========================================================= */

// Sélection des radio cards
document.querySelectorAll('.radio-card').forEach(card => {
    card.addEventListener('click', () => {
        const name = card.querySelector('input').name;
        document.querySelectorAll(`.radio-card input[name="${name}"]`)
            .forEach(i => i.closest('.radio-card').classList.remove('selected'));
        card.classList.add('selected');
        card.querySelector('input').checked = true;
    });
});

// IMC en temps réel
document.getElementById('tailleCm').addEventListener('input', calculerIMC);
document.getElementById('poidsKg').addEventListener('input', calculerIMC);

function calculerIMC() {
    const taille = parseFloat(document.getElementById('tailleCm').value);
    const poids  = parseFloat(document.getElementById('poidsKg').value);
    const imcEl  = document.getElementById('imc-value');
    const catEl  = document.getElementById('imc-categorie');

    if (!taille || !poids || taille < 100 || poids < 30) {
        imcEl.textContent = '—';
        catEl.textContent = '';
        catEl.className   = 'imc-categorie';
        return;
    }

    const imc = poids / ((taille / 100) ** 2);
    imcEl.textContent = imc.toFixed(1);

    let cat, cls;
    if      (imc < 18.5) { cat = 'Insuffisance';  cls = 'imc-maigre';   }
    else if (imc < 25)   { cat = 'Poids normal';   cls = 'imc-normal';   }
    else if (imc < 30)   { cat = 'Surpoids';       cls = 'imc-surpoids'; }
    else                 { cat = 'Obésité';         cls = 'imc-obesite';  }

    catEl.textContent = cat;
    catEl.className   = `imc-categorie ${cls}`;
}

async function chargerProfilUtilisateur() {
    try {
        currentUser = await apiGet(`${API}/utilisateurs/${UTILISATEUR_ID}`);
        if (currentUser.tailleCm) document.getElementById('tailleCm').value = currentUser.tailleCm;
        if (currentUser.poidsKg)  document.getElementById('poidsKg').value  = currentUser.poidsKg;
        calculerIMC();
    } catch (err) {
        console.warn('Profil non chargé :', err.message);
    }
}

function changeNbRepas(delta) {
    const input = document.getElementById('nombreRepasParJour');
    const display = document.getElementById('nb-repas-display');
    let val = parseInt(input.value) + delta;
    val = Math.max(2, Math.min(6, val));
    input.value = val;
    display.textContent = val;
}

document.getElementById('form-questionnaire').addEventListener('submit', async e => {
    e.preventDefault();

    const objectifEl = document.querySelector('input[name="objectif"]:checked');
    if (!objectifEl) { showToast('Veuillez sélectionner un objectif.', 'error'); return; }

    const taille = parseFloat(document.getElementById('tailleCm').value) || null;
    const poids  = parseFloat(document.getElementById('poidsKg').value)  || null;

    // 1. Mettre à jour taille/poids dans le profil
    if (currentUser && (taille || poids)) {
        try {
            currentUser = await apiPut(`${API}/utilisateurs/${UTILISATEUR_ID}`, {
                ...currentUser,
                tailleCm: taille ?? currentUser.tailleCm,
                poidsKg:  poids  ?? currentUser.poidsKg
            });
        } catch (err) {
            console.warn('Mise à jour profil :', err.message);
        }
    }

    const payload = {
        allergiqueGluten:    document.getElementById('allergiqueGluten').checked,
        allergiqueLactose:   document.getElementById('allergiqueLactose').checked,
        allergiqueNoix:      document.getElementById('allergiqueNoix').checked,
        allergiqueOeufs:     document.getElementById('allergiqueOeufs').checked,
        allergiqueFruitsMer: document.getElementById('allergiqueFruitsMer').checked,
        autresAllergies:     document.getElementById('autresAllergies').value,
        preference:          document.getElementById('preference').value,
        objectif:            objectifEl.value,
        niveauActivite:      document.getElementById('niveauActivite').value,
        nombreRepasParJour:  parseInt(document.getElementById('nombreRepasParJour').value)
    };

    try {
        // 2. Sauvegarder le questionnaire (calcule les calories côté serveur)
        const data = await apiPost(`${API}/utilisateurs/${UTILISATEUR_ID}/questionnaire`, payload);
        afficherResultatCalories(data);

        // 3. Récupérer le plan suggéré
        try {
            const plan = await apiGet(`${API}/plans-repas/suggestion?utilisateurId=${UTILISATEUR_ID}`);
            afficherPlanSuggere(plan);
        } catch {
            afficherPlanSuggere(null);
        }

        showToast('Questionnaire enregistré ! Votre plan a été personnalisé.', 'success');
    } catch (err) {
        showToast('Erreur lors de la sauvegarde : ' + err.message, 'error');
    }
});

function getMacrosRatios(objectif) {
    const ratios = {
        'PRISE_DE_MASSE': { p: 0.30, g: 0.50, l: 0.20 },
        'SECHE':          { p: 0.40, g: 0.30, l: 0.30 },
        'PERTE_DE_POIDS': { p: 0.35, g: 0.35, l: 0.30 }
    };
    return ratios[objectif] || { p: 0.25, g: 0.50, l: 0.25 };
}

function afficherResultatCalories(questionnaire) {
    const cal      = questionnaire.caloriesCiblesKcal || 2000;
    const objectif = questionnaire.objectif || 'EQUILIBRE';
    const r        = getMacrosRatios(objectif);

    document.getElementById('calories-value').textContent = cal.toLocaleString('fr-FR');

    document.getElementById('proteines-cibles').textContent = Math.round(cal * r.p / 4) + ' g';
    document.getElementById('glucides-cibles').textContent  = Math.round(cal * r.g / 4) + ' g';
    document.getElementById('lipides-cibles').textContent   = Math.round(cal * r.l / 9) + ' g';

    const resultCard = document.getElementById('calories-result');
    resultCard.classList.remove('hidden');
    resultCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function afficherPlanSuggere(plan) {
    const container = document.getElementById('plan-suggere');
    const content   = document.getElementById('plan-suggere-content');

    if (!plan) { container.classList.add('hidden'); return; }

    const objectifLabel = {
        'PRISE_DE_MASSE': '💪 Prise de masse',
        'SECHE':          '🔥 Sèche',
        'EQUILIBRE':      '⚖️ Équilibre',
        'PERTE_DE_POIDS': '📉 Perte de poids',
        'MAINTIEN':       '✅ Maintien'
    }[plan.objectif] || plan.objectif;

    content.innerHTML = `
        <div class="plan-suggere-card">
            <span class="plan-objectif-badge">${objectifLabel}</span>
            <h4>${plan.nom}</h4>
            <p>${plan.description || ''}</p>
            <div class="plan-macros-mini">
                <strong>${plan.caloriesTotalesKcal} kcal/jour</strong>
                <span class="macro-tag p">P: ${plan.proteinesG}g</span>
                <span class="macro-tag g">G: ${plan.glucidesG}g</span>
                <span class="macro-tag l">L: ${plan.lipidesG}g</span>
            </div>
        </div>`;

    container.classList.remove('hidden');
}

/* =========================================================
   PLANS DE REPAS
   ========================================================= */

async function chargerPlans() {
    const container = document.getElementById('plans-container');

    try {
        // D'abord, essayer de suggérer un plan selon le questionnaire
        let plans;
        try {
            const suggestion = await apiGet(`${API}/plans-repas/suggestion?utilisateurId=${UTILISATEUR_ID}`);
            plans = [suggestion];
        } catch {
            // Fallback : charger tous les plans
            plans = await apiGet(`${API}/plans-repas`);
        }

        if (!plans || plans.length === 0) {
            container.innerHTML = '<p class="empty-state">Aucun plan disponible pour le moment.</p>';
            return;
        }

        container.innerHTML = plans.map(plan => renderPlanCard(plan)).join('');

        // Écouter les clics
        container.querySelectorAll('.plan-card').forEach(card => {
            card.addEventListener('click', () => ouvrirPlanDetail(card.dataset.planId));
        });

    } catch (err) {
        container.innerHTML = `<p class="empty-state">Erreur de chargement : ${err.message}</p>`;
    }
}

function renderPlanCard(plan) {
    const objectifLabel = {
        'PRISE_DE_MASSE': '💪 Prise de masse',
        'SECHE':          '🔥 Sèche',
        'EQUILIBRE':      '⚖️ Équilibre',
        'PERTE_DE_POIDS': '📉 Perte de poids',
        'MAINTIEN':       '✅ Maintien'
    }[plan.objectif] || plan.objectif;

    return `
    <div class="plan-card" data-plan-id="${plan.id}">
        <div class="plan-card-header">
            <span class="plan-objectif-badge">${objectifLabel}</span>
            <span class="plan-calories">${plan.caloriesTotalesKcal} kcal</span>
        </div>
        <h3>${plan.nom}</h3>
        <p>${plan.description || ''}</p>
        <div class="plan-macros-mini">
            <span class="macro-tag p">P: ${plan.proteinesG}g</span>
            <span class="macro-tag g">G: ${plan.glucidesG}g</span>
            <span class="macro-tag l">L: ${plan.lipidesG}g</span>
        </div>
    </div>`;
}

async function ouvrirPlanDetail(planId) {
    const detailSection = document.getElementById('plan-detail');
    const detailContent = document.getElementById('plan-detail-content');
    detailContent.innerHTML = '<p class="loading-placeholder">Chargement…</p>';
    detailSection.classList.remove('hidden');
    detailSection.scrollIntoView({ behavior: 'smooth' });

    try {
        const plan = await apiGet(`${API}/plans-repas/${planId}`);
        detailContent.innerHTML = renderPlanDetail(plan);
    } catch (err) {
        detailContent.innerHTML = `<p class="empty-state">Erreur : ${err.message}</p>`;
    }
}

function renderPlanDetail(plan) {
    const repasIcons = {
        'PETIT_DEJEUNER':        '<i class="ph ph-sun-horizon"></i>',
        'DEJEUNER':              '<i class="ph ph-sun"></i>',
        'DINER':                 '<i class="ph ph-moon"></i>',
        'COLLATION_MATIN':       '<i class="ph ph-apple"></i>',
        'COLLATION_APRES_MIDI':  '<i class="ph ph-carrot"></i>',
        'COLLATION_SOIR':        '<i class="ph ph-coffee"></i>'
    };

    const repasLabels = {
        'PETIT_DEJEUNER':       'Petit-déjeuner',
        'DEJEUNER':             'Déjeuner',
        'DINER':                'Dîner',
        'COLLATION_MATIN':      'Collation matin',
        'COLLATION_APRES_MIDI': 'Collation après-midi',
        'COLLATION_SOIR':       'Collation soir'
    };

    const repasHtml = (plan.repas || []).map(r => `
        <div class="repas-item">
            <span class="repas-type-icon">${repasIcons[r.typeRepas] || '<i class="ph ph-fork-knife"></i>'}</span>
            <div class="repas-info">
                <h4>${repasLabels[r.typeRepas] || r.typeRepas} — ${r.nom}</h4>
                <p>${r.description || ''}</p>
                ${r.ingredients && r.ingredients.length > 0
                    ? `<p><strong>Ingrédients :</strong> ${r.ingredients.join(', ')}</p>` : ''}
                ${r.preparation
                    ? `<p><em>${r.preparation}</em></p>` : ''}
                <div class="repas-macros">
                    <span class="macro-tag p">P: ${r.proteinesG}g</span>
                    <span class="macro-tag g">G: ${r.glucidesG}g</span>
                    <span class="macro-tag l">L: ${r.lipidesG}g</span>
                    <span>${r.caloriesKcal} kcal</span>
                    ${r.tempsPreparationMin ? `<span><i class="ph ph-timer"></i> ${r.tempsPreparationMin} min</span>` : ''}
                </div>
            </div>
        </div>`).join('');

    return `
        <h2>${plan.nom}</h2>
        <p style="color: var(--text-muted); margin: 0.5rem 0 1.5rem;">${plan.description || ''}</p>
        <div class="plan-macros-mini" style="margin-bottom:1.5rem; font-size:0.9rem; gap:0.75rem;">
            <strong>${plan.caloriesTotalesKcal} kcal/jour</strong>
            <span class="macro-tag p">Protéines: ${plan.proteinesG}g</span>
            <span class="macro-tag g">Glucides: ${plan.glucidesG}g</span>
            <span class="macro-tag l">Lipides: ${plan.lipidesG}g</span>
        </div>
        <div class="repas-list">${repasHtml || '<p class="empty-state">Aucun repas dans ce plan.</p>'}</div>`;
}

function closePlanDetail() {
    document.getElementById('plan-detail').classList.add('hidden');
}

/* =========================================================
   MACRO TRACKER
   ========================================================= */

function initTracker() {
    const dateInput = document.getElementById('tracker-date');
    if (!dateInput.value) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }
    chargerBilan();
    chargerEntrees();
}

async function chargerBilan() {
    const date = document.getElementById('tracker-date').value;
    try {
        const bilan = await apiGet(`${API}/utilisateurs/${UTILISATEUR_ID}/macros/resume?date=${date}`);
        mettreAJourBilan(bilan);
    } catch (err) {
        console.warn('Bilan non disponible:', err.message);
    }
}

function mettreAJourBilan(bilan) {
    const conso   = bilan.caloriesConsommees  || 0;
    const cibles  = bilan.caloriesCibles      || 2000;
    const pct     = bilan.pourcentageCalories || 0;
    const reste   = Math.max(0, cibles - conso);

    document.getElementById('calories-consommees').textContent = Math.round(conso);
    document.getElementById('calories-restantes').textContent  = `${Math.round(reste)} restantes`;

    // Anneau SVG : circonférence = 2π × 52 ≈ 327
    const circumference = 327;
    const dash = Math.min(pct / 100 * circumference, circumference);
    document.getElementById('ring-calories').setAttribute('stroke-dasharray', `${dash} ${circumference - dash}`);

    // Barres macros
    const p  = bilan.proteinesG      || 0;
    const pc = bilan.proteinesCiblesG || 100;
    const g  = bilan.glucidesG       || 0;
    const gc = bilan.glucidesCiblesG  || 200;
    const l  = bilan.lipidesG        || 0;
    const lc = bilan.lipidesCiblesG   || 70;

    document.getElementById('proteines-txt').textContent = `${p}g / ${pc}g`;
    document.getElementById('glucides-txt').textContent  = `${g}g / ${gc}g`;
    document.getElementById('lipides-txt').textContent   = `${l}g / ${lc}g`;

    document.getElementById('bar-proteines').style.width = `${Math.min(100, p/pc*100)}%`;
    document.getElementById('bar-glucides').style.width  = `${Math.min(100, g/gc*100)}%`;
    document.getElementById('bar-lipides').style.width   = `${Math.min(100, l/lc*100)}%`;
}

async function chargerEntrees() {
    const date = document.getElementById('tracker-date')?.value
        || new Date().toISOString().split('T')[0];
    try {
        const entrees = await apiGet(`${API}/utilisateurs/${UTILISATEUR_ID}/macros?date=${date}`);
        renderEntrees(entrees);
    } catch {
        // silencieux
    }
}

function renderEntrees(entrees) {
    const container = document.getElementById('entrees-list');
    if (!entrees || entrees.length === 0) {
        container.innerHTML = '<p class="empty-state">Aucun aliment enregistré.</p>';
        return;
    }

    const icons = {
        'PETIT_DEJEUNER':       '<i class="ph ph-sun-horizon"></i>',
        'DEJEUNER':             '<i class="ph ph-sun"></i>',
        'DINER':                '<i class="ph ph-moon"></i>',
        'COLLATION_MATIN':      '<i class="ph ph-apple"></i>',
        'COLLATION_APRES_MIDI': '<i class="ph ph-carrot"></i>',
        'COLLATION_SOIR':       '<i class="ph ph-coffee"></i>'
    };

    container.innerHTML = entrees.map(e => `
        <div class="entree-item">
            <span class="entree-repas-icon">${icons[e.typeRepas] || '<i class="ph ph-fork-knife"></i>'}</span>
            <div class="entree-info">
                <strong>${e.nomAliment}</strong>
                <span>${e.quantiteG}g — P:${e.proteinesG}g G:${e.glucidesG}g L:${e.lipidesG}g</span>
                ${e.notes ? `<span>${e.notes}</span>` : ''}
            </div>
            <span class="entree-calories">${Math.round(e.caloriesKcal)} kcal</span>
            <button class="btn-delete-entree" onclick="supprimerEntree(${e.id})" title="Supprimer">✕</button>
        </div>`).join('');
}

document.getElementById('form-macro').addEventListener('submit', async e => {
    e.preventDefault();

    const payload = {
        date:         document.getElementById('tracker-date')?.value
                      || new Date().toISOString().split('T')[0],
        typeRepas:    document.getElementById('typeRepas-macro').value,
        nomAliment:   document.getElementById('nomAliment').value,
        quantiteG:    parseFloat(document.getElementById('quantiteG').value) || 0,
        caloriesKcal: parseFloat(document.getElementById('caloriesKcal').value) || 0,
        proteinesG:   parseFloat(document.getElementById('proteinesG').value) || 0,
        glucidesG:    parseFloat(document.getElementById('glucidesG').value) || 0,
        lipidesG:     parseFloat(document.getElementById('lipidesG').value) || 0,
        notes:        document.getElementById('notesMacro').value
    };

    try {
        await apiPost(`${API}/utilisateurs/${UTILISATEUR_ID}/macros`, payload);
        e.target.reset();
        document.getElementById('tracker-date').value = new Date().toISOString().split('T')[0];
        await chargerBilan();
        await chargerEntrees();
        showToast('Aliment enregistré !', 'success');
    } catch (err) {
        showToast('Erreur : ' + err.message, 'error');
    }
});

async function supprimerEntree(id) {
    try {
        await apiDelete(`${API}/utilisateurs/${UTILISATEUR_ID}/macros/${id}`);
        await chargerBilan();
        await chargerEntrees();
        showToast('Entrée supprimée.', 'success');
    } catch (err) {
        showToast('Erreur : ' + err.message, 'error');
    }
}

/* =========================================================
   ARTICLES / CONSEILS
   ========================================================= */

let tousLesArticles = [];

async function chargerArticles() {
    const grid = document.getElementById('articles-grid');
    try {
        tousLesArticles = await apiGet(`${API}/articles?utilisateurId=${UTILISATEUR_ID}`);
        renderArticles(tousLesArticles);
    } catch (err) {
        grid.innerHTML = `<p class="empty-state">Erreur : ${err.message}</p>`;
    }
}

function renderArticles(articles) {
    const grid = document.getElementById('articles-grid');
    if (!articles || articles.length === 0) {
        grid.innerHTML = '<p class="empty-state" style="grid-column:1/-1">Aucun article disponible.</p>';
        return;
    }

    const imageCategorie = {
        'HYDRATATION':              'https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=400&h=160&fit=crop',
        'COMPLEMENTS_ALIMENTAIRES': 'https://images.unsplash.com/photo-1512069772995-ec65ed45afd6?w=400&h=160&fit=crop',
        'FIBRES':                   'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=160&fit=crop',
        'GESTION_FRINGALES':        'https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=400&h=160&fit=crop',
        'PROTEINES':                'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=400&h=160&fit=crop',
        'GLUCIDES':                 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400&h=160&fit=crop',
        'LIPIDES':                  'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400&h=160&fit=crop',
        'AVANT_SPORT':              'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400&h=160&fit=crop',
        'APRES_SPORT':              'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=160&fit=crop',
        'MICRONUTRIMENTS':          'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=160&fit=crop'
    };
    const defaultImg = 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=160&fit=crop';

    grid.innerHTML = articles.map(a => `
        <div class="article-card">
            <div class="article-thumb" style="padding:0;overflow:hidden;">
                <img src="${imageCategorie[a.categorie] || defaultImg}"
                     alt="${a.titre}"
                     style="width:100%;height:100%;object-fit:cover;display:block;"
                     onerror="this.style.display='none'">
            </div>
            <div class="article-card-body">
                <div class="article-meta">
                    <span class="badge-type ${(a.typeContenu || '').toLowerCase()}">
                        ${a.typeContenu === 'VIDEO' ? 'Vidéo' : 'Article'}
                    </span>
                    ${a.reserveVip ? '<span class="badge-vip-small">VIP</span>' : ''}
                    <span class="article-date">${formatDate(a.datePublication)}</span>
                </div>
                <h4>${a.titre}</h4>
                <p>${a.resume || ''}</p>
                ${a.auteur ? `<small style="color:var(--text-muted)">Par ${a.auteur}</small>` : ''}
            </div>
        </div>`).join('');
}

function filtrerArticles(type) {
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    event.target.classList.add('active');

    if (type === 'tous') {
        renderArticles(tousLesArticles);
    } else {
        renderArticles(tousLesArticles.filter(a => a.typeContenu === type));
    }
}

function filtrerCategorie(categorie) {
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    event.target.classList.add('active');
    renderArticles(tousLesArticles.filter(a => a.categorie === categorie));
}

/* =========================================================
   CONSULTATIONS
   ========================================================= */

async function chargerConsultations() {
    // Pré-remplir la date minimale (demain)
    const demain = new Date();
    demain.setDate(demain.getDate() + 1);
    const dateInput = document.getElementById('consult-date');
    if (!dateInput.value) {
        dateInput.value = demain.toISOString().split('T')[0];
    }
    dateInput.min = demain.toISOString().split('T')[0];

    try {
        const consultations = await apiGet(`${API}/consultations?utilisateurId=${UTILISATEUR_ID}`);
        renderConsultations(consultations);
    } catch {
        // silencieux si pas encore de consultations
    }
}

function renderConsultations(consultations) {
    const container = document.getElementById('mes-consultations');
    if (!consultations || consultations.length === 0) {
        container.innerHTML = '<p class="empty-state">Aucune consultation programmée.</p>';
        return;
    }

    const statutLabel = {
        'EN_ATTENTE': 'En attente',
        'CONFIRMEE':  'Confirmée',
        'ANNULEE':    'Annulée',
        'TERMINEE':   'Terminée'
    };

    container.innerHTML = consultations.slice(0, 5).map(c => {
        const date = new Date(c.dateHeure);
        const jour = date.getDate().toString().padStart(2, '0');
        const mois = date.toLocaleDateString('fr-FR', { month: 'short' });
        const heure = date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
        const statut = (c.statut || '').toLowerCase().replace('_', '');

        return `
        <div class="consultation-item">
            <div class="consult-date">
                <span class="consult-day">${jour}</span>
                <span class="consult-month">${mois}</span>
            </div>
            <div class="consult-info">
                <strong>${c.nomDieteticien || 'Diététicienne partenaire'}</strong>
                <span>${heure} — ${c.dureeMins} min</span>
                ${c.lienVisio && c.statut === 'CONFIRMEE'
                    ? `<a href="${c.lienVisio}" class="link-visio" target="_blank">🔗 Rejoindre la visio</a>`
                    : ''}
            </div>
            <span class="badge-statut ${statut}">${statutLabel[c.statut] || c.statut}</span>
            ${c.statut === 'CONFIRMEE' || c.statut === 'EN_ATTENTE'
                ? `<button class="btn btn-outline" style="font-size:0.75rem;padding:0.25rem 0.5rem;"
                     onclick="annulerConsultation(${c.id})">Annuler</button>` : ''}
        </div>`;
    }).join('');
}

// Sélection des créneaux
document.querySelectorAll('.slot-btn:not(.disabled)').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        document.getElementById('selected-slot').value = btn.dataset.time;
    });
});

// Sélection de la durée
document.querySelectorAll('.duree-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.duree-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        document.getElementById('selected-duree').value = btn.dataset.duree;
    });
});

document.getElementById('form-consultation').addEventListener('submit', async e => {
    e.preventDefault();

    const date  = document.getElementById('consult-date').value;
    const heure = document.getElementById('selected-slot').value;
    const duree = parseInt(document.getElementById('selected-duree').value);

    if (!date || !heure) {
        showToast('Veuillez sélectionner une date et un créneau.', 'error');
        return;
    }

    const dateHeure = `${date}T${heure}:00`;

    try {
        await apiPost(`${API}/consultations`, {
            utilisateurId: UTILISATEUR_ID,
            dateHeure:     dateHeure,
            dureeMins:     duree
        });
        showToast('Consultation confirmée ! Vous recevrez un email de confirmation.', 'success');
        await chargerConsultations();
    } catch (err) {
        showToast('Erreur : ' + err.message, 'error');
    }
});

async function annulerConsultation(id) {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette consultation ?')) return;
    try {
        await apiDelete(`${API}/consultations/${id}`);
        showToast('Consultation annulée.', 'success');
        await chargerConsultations();
    } catch (err) {
        showToast('Erreur : ' + err.message, 'error');
    }
}

/* =========================================================
   UTILITAIRES
   ========================================================= */

async function apiGet(url) {
    const res = await fetch(url);
    if (!res.ok) {
        const err = await res.json().catch(() => ({ erreur: res.statusText }));
        throw new Error(err.erreur || res.statusText);
    }
    return res.json();
}

async function apiPost(url, body) {
    const res = await fetch(url, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify(body)
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({ erreur: res.statusText }));
        throw new Error(err.erreur || res.statusText);
    }
    return res.json();
}

async function apiPut(url, body) {
    const res = await fetch(url, {
        method:  'PUT',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify(body)
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({ erreur: res.statusText }));
        throw new Error(err.erreur || res.statusText);
    }
    return res.json();
}

async function apiDelete(url) {
    const res = await fetch(url, { method: 'DELETE' });
    if (!res.ok && res.status !== 204) {
        const err = await res.json().catch(() => ({ erreur: res.statusText }));
        throw new Error(err.erreur || res.statusText);
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type}`;
    setTimeout(() => { toast.classList.add('hidden'); }, 4000);
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
        day: 'numeric', month: 'short', year: 'numeric'
    });
}

/* =========================================================
   INITIALISATION AU CHARGEMENT
   ========================================================= */

document.addEventListener('DOMContentLoaded', () => {
    showSection('questionnaire');
    chargerProfilUtilisateur();
});
