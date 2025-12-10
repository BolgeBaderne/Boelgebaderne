// src/main/resources/static/js/admin-events.js

document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.getElementById("events-table-body");
    const msgBox = document.getElementById("message-box");
    const msgText = document.getElementById("message-text");
    const createBtn = document.getElementById("create-event-btn");

    function showMessage(text, isError = false) {
        if (!msgBox || !msgText) return;
        msgText.textContent = text;
        msgBox.classList.remove("hidden", "error");
        if (isError) {
            msgBox.classList.add("error");
        }
    }

    function clearMessage() {
        if (!msgBox || !msgText) return;
        msgBox.classList.add("hidden");
        msgText.textContent = "";
    }

    function renderEvents(events) {
        tbody.innerHTML = "";

        if (!events || events.length === 0) {
            showMessage(
                'Der er endnu ingen events. Opret det første med knappen "Opret nyt event".'
            );
            return;
        }

        clearMessage();

        events.forEach(e => {
            const tr = document.createElement("tr");

            // ID
            const idCell = document.createElement("td");
            idCell.textContent = e.id ?? "";
            tr.appendChild(idCell);

            // Titel (hvis du ikke har title i DTO, bruger vi fallback)
            const titleCell = document.createElement("td");
            titleCell.textContent = e.title ?? "Saunagus-event";
            tr.appendChild(titleCell);

            // Gusmester-navn
            const gusmesterCell = document.createElement("td");
            gusmesterCell.textContent = e.gusmesterName ?? e.saunagusMasterName ?? "";
            tr.appendChild(gusmesterCell);

            // Start (hvis din DTO ikke har startTime, viser vi bare "-")
            const startCell = document.createElement("td");
            startCell.textContent = e.startTime ?? "–";
            tr.appendChild(startCell);

            // Kapacitet + bookinger
            const capacityCell = document.createElement("td");
            const cap = e.capacity ?? 0;
            const bookings = e.currentBookings ?? 0;
            capacityCell.textContent = `${bookings}/${cap}`;
            tr.appendChild(capacityCell);

            // Pris
            const priceCell = document.createElement("td");
            if (typeof e.price === "number") {
                priceCell.textContent = e.price.toFixed(2) + " kr.";
            } else {
                priceCell.textContent = "–";
            }
            tr.appendChild(priceCell);

            // Status badge (hvis status ikke findes, gætter vi ud fra ledige pladser)
            const statusCell = document.createElement("td");
            const available = e.availableSpots ?? 0;
            const rawStatus = e.status ?? (available <= 0 ? "FULLY_BOOKED" : "UPCOMING");
            const badge = document.createElement("span");
            badge.className = "badge " + rawStatus;
            badge.textContent = rawStatus.replace("_", " ");
            statusCell.appendChild(badge);
            tr.appendChild(statusCell);

            // Handlinger (edit / delete – lige nu kun “fake” til demo)
            const actionsCell = document.createElement("td");

            const editBtn = document.createElement("button");
            editBtn.type = "button";
            editBtn.className = "btn icon-btn edit";
            editBtn.textContent = "✏️";
            editBtn.title = "Redigér (kommer senere)";
            editBtn.addEventListener("click", () => {
                alert("Redigering af events kommer i næste user story 🙂");
            });

            const deleteBtn = document.createElement("button");
            deleteBtn.type = "button";
            deleteBtn.className = "btn icon-btn delete";
            deleteBtn.textContent = "🗑";
            deleteBtn.title = "Slet (kommer senere)";
            deleteBtn.addEventListener("click", () => {
                alert("Sletning af events kommer i næste user story 🙂");
            });

            actionsCell.appendChild(editBtn);
            actionsCell.appendChild(deleteBtn);
            tr.appendChild(actionsCell);

            tbody.appendChild(tr);
        });
    }

    async function loadEvents() {
        try {
            clearMessage();

            const res = await fetch("/api/admin/events", {
                headers: {
                    "Accept": "application/json"
                }
            });

            if (res.status === 401) {
                showMessage("Du skal være logget ind som administrator for at se denne side.", true);
                return;
            }

            if (res.status === 403) {
                showMessage("Kun administratorer må se denne oversigt.", true);
                return;
            }

            if (!res.ok) {
                showMessage("Kunne ikke hente events (fejlkode " + res.status + ").", true);
                return;
            }

            const data = await res.json();
            renderEvents(data);
        } catch (err) {
            console.error(err);
            showMessage("Der skete en uventet fejl under hentning af events.", true);
        }
    }

    // Klik på "Opret nyt event" (kun demo lige nu)
    if (createBtn) {
        createBtn.addEventListener("click", () => {
            alert("Opret-UI kommer i næste step – lige nu er knappen kun til demo 🙂");
        });
    }

    // Hent events, når siden åbner
    loadEvents();
});
