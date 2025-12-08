// -------------------------------------------
// 1. TRANSLATION DICTIONARY
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
    }
};

// -------------------------------------------
// 2. UPDATE TEXT BASED ON SELECTED LANGUAGE
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

    // gem valg
    localStorage.setItem("lang", lang);
}

// -------------------------------------------
// 3. LANGUAGE SWITCH BUTTON HANDLER
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

