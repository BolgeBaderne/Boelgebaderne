// Hent event ID fra URL path eller brug default
function getEventIdFromUrl() {
    const pathParts = window.location.pathname.split('/');
    const lastPart = pathParts[pathParts.length - 1];
    const idFromPath = parseInt(lastPart);

    // Hvis sidste del af path er et nummer, brug det
    if (!isNaN(idFromPath)) {
        console.log(`Event ID fra path: ${idFromPath}`);
        return idFromPath;
    }

    // Ellers prøv query parameter: event.html?id=1
    const params = new URLSearchParams(window.location.search);
    const idFromQuery = params.get("id");

    if (idFromQuery) {
        console.log(`Event ID fra query: ${idFromQuery}`);
        return parseInt(idFromQuery);
    }

    console.log('Bruger default event ID: 1');
    return 1;
}

const eventId = getEventIdFromUrl();
const apiUrl = `/api/events/${eventId}`;

console.log(`Henter event med ID: ${eventId} fra ${apiUrl}`);

const els = {
    title: document.getElementById("event-title"),
    status: document.getElementById("event-status"),
    startTime: document.getElementById("event-starttime"),
    durationHero: document.getElementById("event-duration"),

    description: document.getElementById("event-description"),
    gusName: document.getElementById("gusmester-name"),
    gusImage: document.getElementById("gusmester-image"),

    infoDuration: document.getElementById("info-duration"),
    infoCapacity: document.getElementById("info-capacity"),
    infoBookings: document.getElementById("info-bookings"),
    infoAvailable: document.getElementById("info-available"),
    infoPrice: document.getElementById("info-price"),

    errorBox: document.getElementById("error-box"),
    errorMessage: document.getElementById("error-message"),

    bookBtn: document.getElementById("book-button")
};

function formatMinutes(min) {
    return `${min} min`;
}

function formatPrice(price) {
    return `${Number(price || 0).toFixed(0)} kr.`;
}

function showError(message) {
    els.errorBox.classList.remove("hidden");
    els.errorMessage.textContent = message;
}

function updatePage(data) {
    // title
    els.title.textContent = data.title || "Saunagus event";

    // status (hvis du har status i DTO)
    els.status.textContent = data.status || "Event";

    // startTime hvis API sender LocalDateTime string "2025-12-10T19:00:00"
    if (data.startTime) {
        const [date, time] = String(data.startTime).split("T");
        els.startTime.textContent = `${date} kl. ${time?.substring(0, 5)}`;
    } else {
        els.startTime.textContent = "";
    }

    // duration
    els.durationHero.textContent = formatMinutes(data.durationMinutes ?? 0);
    els.description.textContent = data.description ?? "";

    // gusmester (kan være forskellige feltnavne afhængig af DTO)
    els.gusName.textContent =
        data.saunagusMasterName ??
        data.gusmesterName ??
        "Ukendt gusmester";

    const img =
        data.saunagusMasterImageUrl ??
        data.gusmesterImageUrl ??
        "";

    els.gusImage.src = img || "https://images.pexels.com/photos/111085/pexels-photo-111085.jpeg?auto=compress&cs=tinysrgb&w=800";

    // side-info
    els.infoDuration.textContent = formatMinutes(data.durationMinutes ?? 0);
    els.infoCapacity.textContent = `${data.capacity ?? "-"} pladser`;
    els.infoBookings.textContent = `${data.currentBookings ?? 0} tilmeldte`;
    els.infoAvailable.textContent = `${data.availableSpots ?? 0} ledige`;
    els.infoPrice.textContent = formatPrice(data.price ?? 0);

    // demo booking-knap
    els.bookBtn.addEventListener("click", () => {
        alert("Demo: booking er ikke implementeret i #152 🙂");
    });
}

async function loadEvent() {
    try {
        const res = await fetch(apiUrl);

        if (!res.ok) {
            // prøv at læse JSON fejl (fra din GlobalExceptionHandler)
            let msg = `Kunne ikke hente event. Status: ${res.status}`;
            try {
                const body = await res.json();
                msg = body.error || body.message || msg;
            } catch {}
            throw new Error(msg);
        }

        const data = await res.json();
        updatePage(data);

    } catch (err) {
        console.error(err);
        showError(err.message || "Noget gik galt ved hentning af eventet.");
    }
}

document.addEventListener("DOMContentLoaded", loadEvent);
