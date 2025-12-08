// ------------------------------
// Mobile navigation toggle
// ------------------------------
document.addEventListener("DOMContentLoaded", () => {
    const header = document.querySelector(".site-header");
    const toggle = document.querySelector(".nav-toggle");
    const nav = document.querySelector(".main-nav");

    toggle.addEventListener("click", () => {
        // Læs om menuen er åben
        const isOpen = toggle.getAttribute("aria-expanded") === "true";

        // Skift aria-expanded
        toggle.setAttribute("aria-expanded", String(!isOpen));

        // Toggle .is-open på header (styres af CSS)
        header.classList.toggle("is-open");

        // Når menuen åbner → sæt fokus på første link (tilgængelighed)
        if (!isOpen) {
            const firstLink = nav.querySelector("a");
            if (firstLink) firstLink.focus();
        }
    });
});

const navToggle = document.querySelector(".nav-toggle");
const header = document.querySelector(".site-header");

navToggle.addEventListener("click", () => {
    const isOpen = header.classList.toggle("is-open");
    navToggle.setAttribute("aria-expanded", isOpen);
    navToggle.setAttribute("aria-label", isOpen ? "Luk menu" : "Åbn menu");
});
