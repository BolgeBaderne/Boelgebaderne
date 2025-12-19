// event.js (clean version) — expects /api/events/{id} to return:
// { id, title, description, gusmesterName, gusmesterImageUrl, startTime,
//   durationMinutes, capacity, price, currentBookings, availableSpots, status }

(() => {
    // Priority 1: Check if eventId was injected by Thymeleaf
    let eventId = window.EVENT_ID;

    // Priority 2: Extract from URL path: /api/events/view/{id}
    if (!eventId) {
        const pathParts = window.location.pathname.split('/');
        const lastPart = pathParts[pathParts.length - 1];
        if (!isNaN(lastPart) && lastPart !== 'view' && lastPart !== '') {
            eventId = lastPart;
        }
    }

    // Priority 3: Try query parameter as fallback
    if (!eventId) {
        const params = new URLSearchParams(window.location.search);
        eventId = params.get("id") || "1";
    }

    const apiUrl = `/api/events/${encodeURIComponent(eventId)}`;

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

    const FALLBACK_IMAGE =
        "https://images.pexels.com/photos/111085/pexels-photo-111085.jpeg?auto=compress&cs=tinysrgb&w=800";

    function setText(el, value) {
        if (!el) return;
        el.textContent = value ?? "";
    }

    function formatMinutes(min) {
        const n = Number(min);
        return Number.isFinite(n) ? `${n} min` : "-";
    }

    function formatPrice(price) {
        const n = Number(price);
        return Number.isFinite(n) ? `${Math.round(n)} kr.` : "-";
    }

    function formatStartTime(iso) {
        if (!iso) return "";
        // API: "2025-12-15T08:00"
        const [date, time] = String(iso).split("T");
        if (!date) return "";
        if (!time) return date;
        return `${date} kl. ${time.substring(0, 5)}`;
    }

    function showError(message) {
        if (els.errorBox) els.errorBox.classList.remove("hidden");
        setText(els.errorMessage, message || "Der skete en fejl.");
    }

    function hideError() {
        if (els.errorBox) els.errorBox.classList.add("hidden");
        setText(els.errorMessage, "");
    }

    function updatePage(e) {
        hideError();

        setText(els.title, e.title || "Saunagus event");
        setText(els.status, e.status || "EVENT");
        setText(els.startTime, formatStartTime(e.startTime));
        setText(els.durationHero, formatMinutes(e.durationMinutes));

        setText(els.description, e.description || "");
        setText(els.gusName, e.gusmesterName || "Ukendt gusmester");

        if (els.gusImage) {
            els.gusImage.src = e.gusmesterImageUrl || FALLBACK_IMAGE;
            els.gusImage.alt = e.gusmesterName ? `Gusmester: ${e.gusmesterName}` : "Gusmester";
        }

        setText(els.infoDuration, formatMinutes(e.durationMinutes));
        setText(
            els.infoCapacity,
            e.capacity != null ? `${e.capacity} pladser` : "-"
        );
        setText(
            els.infoBookings,
            e.currentBookings != null ? `${e.currentBookings} tilmeldte` : "0 tilmeldte"
        );
        setText(
            els.infoAvailable,
            e.availableSpots != null ? `${e.availableSpots} ledige` : "0 ledige"
        );
        setText(els.infoPrice, formatPrice(e.price));
    }

    async function load() {
        try {
            const res = await fetch(apiUrl, { headers: { Accept: "application/json" } });

            if (!res.ok) {
                // prøv at læse backend error-body, ellers generisk
                let msg = `Kunne ikke hente event (${res.status})`;
                try {
                    const body = await res.json();
                    msg = body?.error || body?.message || msg;
                } catch (_) {}
                throw new Error(msg);
            }

            const data = await res.json();
            updatePage(data);
        } catch (err) {
            console.error("Event load error:", err);
            showError(err?.message || "Kunne ikke hente event-data.");
        }
    }

    // Kør med det samme (filen ligger nederst i html eller med defer)
    load();
})();
