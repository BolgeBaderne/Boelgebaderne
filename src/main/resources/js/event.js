// Hent event-id fra URL'en: event-details?id=1
const params = new URLSearchParams(window.location.search);
const eventId = params.get("id") || 1;


const apiUrl = `/admin/events/${eventId}`;

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
};

function formatMinutes(min) {
    return `${min} min`;
}

function formatPrice(price) {
    return `${price.toFixed(0)} kr.`;
}

function updatePage(data) {
    els.title.textContent = data.title || "Saunagus event";

    // Hvis du har status i DTO, brug data.status, ellers bare “Event”
    els.status.textContent = data.status || "Event";

    if (data.startTime) {
        const [date, time] = data.startTime.split("T");
        els.startTime.textContent = `${date} kl. ${time?.substring(0, 5)}`;
    } else {
        els.startTime.textContent = "";
    }

    els.durationHero.textContent = formatMinutes(data.durationMinutes ?? 0);
    els.description.textContent = data.description ?? "";

    els.gusName.textContent = data.gusmesterName ?? data.saunagusMasterName ?? "Ukendt gusmester";

    if (data.gusmesterImageUrl || data.saunagusMasterImageUrl) {
        els.gusImage.src = data.gusmesterImageUrl || data.saunagusMasterImageUrl;
    } else {
        els.gusImage.src =
            "https://images.pexels.com/photos/111085/pexels-photo-111085.jpeg?auto=compress&cs=tinysrgb&w=800";
    }

    els.infoDuration.textContent = formatMinutes(data.durationMinutes ?? 0);
    els.infoCapacity.textContent = `${data.capacity ?? "-"} pladser`;
    els.infoBookings.textContent = `${data.currentBookings ?? 0} tilmeldte`;
    els.infoAvailable.textContent = `${data.availableSpots ?? 0} ledige`;
    els.infoPrice.textContent = formatPrice(data.price ?? 0);
}

function showError(message) {
    els.errorBox.classList.remove("hidden");
    els.errorMessage.textContent = message;
}

fetch(apiUrl)
    .then(res => {
        if (!res.ok) {
            return res.json()
                .catch(() => {
                    throw new Error("Noget gik galt ved hentning af eventet.");
                })
                .then(body => {
                    throw new Error(body.error || "Det valgte event findes ikke.");
                });
        }
        return res.json();
    })
    .then(updatePage)
    .catch(err => {
        console.error(err);
        showError(err.message);
    });
