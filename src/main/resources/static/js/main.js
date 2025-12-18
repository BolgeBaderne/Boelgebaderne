document.addEventListener("DOMContentLoaded", () => {
    const header = document.querySelector(".site-header");
    const toggle = document.querySelector(".nav-toggle");
    const nav = document.querySelector(".main-nav");
    const closeBtn = document.querySelector(".close-menu");
    const overlay = document.querySelector(".menu-overlay");
    const menuLinks = nav.querySelectorAll("a");


    // Accessibility (aria labels)
    function openMenu() {
        header.classList.add("is-open");
        toggle.setAttribute("aria-expanded", "true");
        toggle.setAttribute("aria-label", "Luk menu");
    }

    function closeMenu() {
        header.classList.remove("is-open");
        toggle.setAttribute("aria-expanded", "false");
        toggle.setAttribute("aria-label", "Åbn menu");
    }

    // Toggle button
    toggle.addEventListener("click", () => {
        const isOpen = header.classList.contains("is-open");
        isOpen ? closeMenu() : openMenu();
    });

    // Close when clicking the X button
    closeBtn.addEventListener("click", () => {
        closeMenu();
    });

    // Close overlay when clicking outside menu
    overlay.addEventListener("click", () => {
        closeMenu();
    });

    // Close menu when clicking a navigation link
    menuLinks.forEach(link => {
        link.addEventListener("click", () => closeMenu());
    });
});

// -------------------------------------------
// TODAY'S SAUNA HOURS (DYNAMIC)
// -------------------------------------------

const gusSchedule = {
    mandag: {medlem: "7-11 & 16-21", åben: "-"},
    tirsdag: {medlem: "7-11 & 16-21", åben: "-"},
    onsdag: {medlem: "7-9", åben: "9-11 & 15-21"},
    torsdag: {medlem: "7-11 & 16-21", åben: "-"},
    fredag: {medlem: "7-11 & 16-21", åben: "-"},
    lørdag: {medlem: "7-11 & 16-21", åben: "11-15"},
    søndag: {medlem: "7-11 & 16-21", åben: "11-15"}
};

const weekdays = [
    "søndag", "mandag", "tirsdag", "onsdag", "torsdag", "fredag", "lørdag"
];

document.addEventListener("DOMContentLoaded", () => {

    const today = new Date();
    const dayName = weekdays[today.getDay()];

    const dayEl = document.getElementById("today-name");
    const memberEl = document.getElementById("member-hours");
    const openEl = document.getElementById("open-hours");

    if (dayEl && memberEl && openEl) {
        dayEl.textContent = dayName.charAt(0).toUpperCase() + dayName.slice(1);
        memberEl.textContent = gusSchedule[dayName].medlem;
        openEl.textContent = gusSchedule[dayName].åben;
    }
});

// -------------------------------------------
// GALLERY / SLIDER
// -------------------------------------------

const track = document.getElementById("galleryTrack");
const btnPrev = document.getElementById("galleryPrev");
const btnNext = document.getElementById("galleryNext");

let position = 0;
const itemWidth = 360 + 24; // image width + gap

if (track && btnPrev && btnNext) {
    btnNext.addEventListener("click", () => {
        if (position > -(track.children.length - 3) * itemWidth) {
            position -= itemWidth;
            track.style.transform = `translateX(${position}px)`;
        }
    });

    btnPrev.addEventListener("click", () => {
        if (position < 0) {
            position += itemWidth;
            track.style.transform = `translateX(${position}px)`;
        }
    });
}

// -------------------------------------------
// GUS MASTER SLIDER
// -------------------------------------------

const gusTrack = document.getElementById("gusTrack");
const gusPrev = document.getElementById("gusPrev");
const gusNext = document.getElementById("gusNext");

let gusPosition = 0;
const gusCardWidth = 180 + 16; // card width + gap

if (gusTrack && gusPrev && gusNext) {

    gusNext.addEventListener("click", () => {
        if (gusPosition > -(gusTrack.children.length - 3) * gusCardWidth) {
            gusPosition -= gusCardWidth;
            gusTrack.style.transform = `translateX(${gusPosition}px)`;
        }
    });

    gusPrev.addEventListener("click", () => {
        if (gusPosition < 0) {
            gusPosition += gusCardWidth;
            gusTrack.style.transform = `translateX(${gusPosition}px)`;
        }
    });

}

// -------------------------------------------
// SCROLL TO TOP BUTTON
// -------------------------------------------

const scrollBtn = document.getElementById("scrollTopBtn");

window.addEventListener("scroll", () => {
    if (window.scrollY > 400) {
        scrollBtn.classList.add("show");
    } else {
        scrollBtn.classList.remove("show");
    }
});

scrollBtn.addEventListener("click", () => {
    window.scrollTo({top: 0, behavior: "smooth"});
});

// -------------------------------------------
// LOAD POSTS
// -------------------------------------------

function loadPosts() {
    fetch("/api/posts")
        .then(res => res.json())
        .then(posts => {
            const container = document.querySelector(".dashboard-section .updates-card-update");
            container.innerHTML = "";

            posts.forEach(p => {
                container.innerHTML += `
          <article class="updates-card-update">
            <p><strong>${p.author}:</strong> ${p.content}</p>
            <p><i>Dato: ${p.createdAt}</i></p>
          </article>`;
            });
        });
}
