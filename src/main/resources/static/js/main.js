document.addEventListener("DOMContentLoaded", () => {
    const header = document.querySelector(".site-header");
    const toggle = document.querySelector(".nav-toggle");
    const nav = document.querySelector(".main-nav");
    const closeBtn = document.querySelector(".close-menu");
    const overlay = document.querySelector(".menu-overlay");
    const menuLinks = nav.querySelectorAll("a");

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
// 4. TODAY'S SAUNA HOURS (DYNAMIC)
// -------------------------------------------

const gusSchedule = {
    mandag:     { medlem: "14-18", åben: "19-21" },
    tirsdag:    { medlem: "06-10", åben: "17-20" },
    onsdag:     { medlem: "14-18", åben: "19-21" },
    torsdag:    { medlem: "06-10", åben: "17-20" },
    fredag:     { medlem: "14-18", åben: "18-21" },
    lørdag:     { medlem: "09-12", åben: "12-15" },
    søndag:     { medlem: "09-12", åben: "12-15" }
};

const weekdays = [
    "søndag","mandag","tirsdag","onsdag","torsdag","fredag","lørdag"
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
