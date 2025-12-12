// -------------------------------------------
// TRANSLATION DICTIONARY
// -------------------------------------------
const translations = {
    da: {
        // Navbar menu items
        "nav.about": "Om os",
        "nav.membership": "Medlemskab",
        "nav.booking": "Booking",
        "nav.contact": "Kontakt",

        // Navbar action buttons
        "actions.signup": "Bliv medlem",
        "actions.login": "Log ind",

        // Hero section
        "hero.title": "Bølgebaderne",
        "hero.subtitle": "Et fællesskab for helårsbadere ved Kalvebod Bølge i København",
        "hero.ctaPrimary": "Bliv medlem",
        "hero.ctaSecondary": "Læs mere",

        // Intro section
        "intro.title": "Velkommen til Bølgebaderne",
        "intro.text": "Velkommen til Bølgebaderne, en forening og et fællesskab for helårsbadere ved Kalvebod Bølge i København.\n\nBølgebaderne er en forening af passionerede københavnere, der vinterbader ved Bølgen. Vi har stiftet en forening med det formål at være med til at skabe et positivt og inkluderende fællesskab ved Bølgen. Vi vil gerne have, at alle har adgang til stedet og til at blive inspireret af vinterbadning og det skønne sammenhold på tværs alle befolkningsgrupper.",
        "intro.card1.title": "Fællesskab & nærvær",
        "intro.card1.text": "Oplev stemningen og det stærke fællesskab blandt helårsbadere.",
        "intro.card2.title": "Sauna & velvære",
        "intro.card2.text": "Vores saunafaciliteter holder dig varm – året rundt.",
        "intro.card3.title": "Bliv en del af os",
        "intro.card3.text": "Som medlem får du adgang til saunaen, booking og fælles aktiviteter.",
    },

    en: {
        // Navbar menu items
        "nav.about": "About",
        "nav.membership": "Membership",
        "nav.booking": "Booking",
        "nav.contact": "Contact",

        // Navbar action buttons
        "actions.signup": "Sign up",
        "actions.login": "Log in",

        // Hero section
        "hero.title": "Bølgebaderne",
        "hero.subtitle": "A community of year-round swimmers at Kalvebod Bølge in Copenhagen",
        "hero.ctaPrimary": "Become a member",
        "hero.ctaSecondary": "Learn more",

        // Intro section
        "intro.title": "Welcome to Bølgebaderne",
        "intro.text": "Welcome to Bølgebaderne, an association and community of year-round swimmers at Kalvebod Bølge in Copenhagen.\n\nBølgebaderne is an association of passionate Copenhageners who swim in the winter at Bølgen. We founded the association to help build a positive and inclusive community at the Bølge. We want everyone to have access to the place and to be inspired by winter swimming and the wonderful fellowship across all communities.",
        "intro.card1.title": "Community & togetherness",
        "intro.card1.text": "Experience the atmosphere and strong community among year-round swimmers.",
        "intro.card2.title": "Sauna & wellbeing",
        "intro.card2.text": "Our sauna facilities keep you warm – all year round.",
        "intro.card3.title": "Join us",
        "intro.card3.text": "As a member you get access to the sauna, booking and community events.",
    }
};

// -------------------------------------------
// UPDATE TEXT BASED ON SELECTED LANGUAGE
// -------------------------------------------
function updateLanguage(lang) {
    const elements = document.querySelectorAll("[data-i18n]");

    elements.forEach(el => {
        const key = el.getAttribute("data-i18n");
        const translation = translations[lang][key];

        if (translation) {
            el.textContent = translation;
        }
    });

    // Save choice
    localStorage.setItem("lang", lang);
}

// -------------------------------------------
// LANGUAGE SWITCH BUTTON HANDLER
// -------------------------------------------
document.querySelectorAll(".lang-switch").forEach(button => {
    button.addEventListener("click", () => {
        const selectedLang = button.getAttribute("data-lang"); // "da" or "en"
        updateLanguage(selectedLang);
    });
});

// Load saved language (default = dansk)
const savedLang = localStorage.getItem("lang") || "da";
updateLanguage(savedLang);

