(() => {
    const qs = new URLSearchParams(window.location.search);
    const timeslotId = qs.get("timeslotId") || "1";
    const apiUrl = `/api/timeslots/${encodeURIComponent(timeslotId)}/waitlist`;

    const els = {
        timeslotId: document.getElementById("timeslot-id"),
        status: document.getElementById("waitlist-status"),
        hint: document.getElementById("waitlist-hint"),
        position: document.getElementById("position"),
        count: document.getElementById("count"),
        extra: document.getElementById("extra"),

        refreshBtn: document.getElementById("refresh-btn"),
        joinBtn: document.getElementById("join-btn"),

        errorBox: document.getElementById("error-box"),
        errorMsg: document.getElementById("error-message"),
    };

    els.timeslotId.textContent = timeslotId;

    function showError(msg) {
        els.errorBox.classList.remove("hidden");
        els.errorMsg.textContent = msg || "Ukendt fejl";
    }

    function hideError() {
        els.errorBox.classList.add("hidden");
        els.errorMsg.textContent = "";
    }

    function normalize(data) {
        return {
            open: data.open ?? data.isOpen ?? data.waitlistOpen ?? true,
            position: data.position ?? data.myPosition ?? "-",
            count: data.count ?? data.size ?? "-"
        };
    }

    function applyStatus(open) {
        els.status.textContent = open ? "ÅBEN" : "LUKKET";
        els.hint.textContent = open
            ? "Du kan tilmelde dig ventelisten."
            : "Ventelisten er lukket for dette timeslot.";
        els.joinBtn.disabled = !open;
    }

    async function fetchStatus() {
        hideError();
        try {
            const res = await fetch(apiUrl);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = normalize(await res.json());

            applyStatus(data.open);
            els.position.textContent = data.position;
            els.count.textContent = data.count;
            els.extra.textContent = "";
        } catch (e) {
            console.error(e);
            showError(e.message);
            els.joinBtn.disabled = true;
        }
    }

    async function joinWaitlist() {
        hideError();
        els.joinBtn.disabled = true;
        els.joinBtn.textContent = "Tilmelder...";

        try {
            const res = await fetch(apiUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({})
            });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            await fetchStatus();
        } catch (e) {
            showError(e.message);
        } finally {
            els.joinBtn.textContent = "Join venteliste";
        }
    }

    els.refreshBtn.addEventListener("click", fetchStatus);
    els.joinBtn.addEventListener("click", joinWaitlist);

    fetchStatus();
})();
