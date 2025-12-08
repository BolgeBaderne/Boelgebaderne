// Hent event-id fra URL'en: event.html?id=1
const params = new URLSearchParams(window.location.search);
const eventId = params.get("id") || 1;

const apiUrl = `/api/events/${eventId}`;

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
    // tilpas til dine faktiske DTO-felter
    els.title.textContent = data.title || "Saunagus event";

    els.status.textContent = "Event"; // evt. data.status hvis I har det

    if (data.startTime) {
        const [date, time] = data.startTime.split("T");
        els.startTime.textContent = `${date} kl. ${time?.substring(0, 5)}`;
    } else {
        els.startTime.textContent = "";
    }

    els.durationHero.textContent = formatMinutes(data.durationMinutes ?? 0);
    els.description.textContent = data.description ?? "";

    els.gusName.textContent = data.saunagusMasterName ?? "Ukendt gusmester";

    if (data.saunagusMasterImageUrl) {
        els.gusImage.src = data.saunagusMasterImageUrl;
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

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id") || 1;

    try {
        const response = await fetch(`/api/events/${id}`);
        if (!response.ok) {
            throw new Error("Kunne ikke hente event " + id);
        }

        const e = await response.json();

        // Tilpas IDs til dine rigtige HTML-elementer
        document.querySelector("#event-title").textContent = e.title;
        document.querySelector("#event-description").textContent = e.description;
        document.querySelector("#gusmester-name").textContent = e.gusmesterName;
        document.querySelector("#duration").textContent = e.durationMinutes + " min";
        document.querySelector("#capacity").textContent = e.capacity;
        document.querySelector("#current-bookings").textContent = e.currentBookings;
        document.querySelector("#available-spots").textContent = e.availableSpots;
        document.querySelector("#price").textContent = e.price + " kr.";

    } catch (err) {
        console.error(err);
        document.querySelector("#event-description").textContent =
            "Kunne ikke hente event-data.";
    }
});
