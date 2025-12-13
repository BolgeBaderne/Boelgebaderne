const API_BASE = "/api/admin/events";

const form = document.getElementById("eventForm");
const formMessage = document.getElementById("formMessage");
const cancelEditBtn = document.getElementById("cancelEditBtn");
const refreshBtn = document.getElementById("refreshBtn");

const tbody = document.getElementById("eventsBody");
const tableError = document.getElementById("tableError");

function showMessage(text, type = "success") {
    formMessage.className = `message ${type}`;
    formMessage.textContent = text;
}

function showTableError(text) {
    tableError.classList.remove("hidden");
    tableError.textContent = text;
}

function clearTableError() {
    tableError.classList.add("hidden");
    tableError.textContent = "";
}

function resetEditMode() {
    delete form.dataset.editId;
    cancelEditBtn.style.display = "none";
    showMessage("Opret nyt event.", "success");
    form.reset();
    // sæt default status hvis du vil:
    document.getElementById("status").value = "UPCOMING";
}

cancelEditBtn.addEventListener("click", () => resetEditMode());
refreshBtn.addEventListener("click", () => loadEvents());

function createRow(event) {
    const tr = document.createElement("tr");

    tr.innerHTML = `
    <td>${event.id ?? "-"}</td>
    <td>${event.title ?? "-"}</td>
    <td>${event.saunagusMasterName ?? event.gusmesterName ?? "-"}</td>
    <td>${event.capacity ?? "-"}</td>
    <td>${event.price ?? 0} kr.</td>
    <td>${event.status ?? "-"}</td>
    <td>
      <div class="actions">
        <button class="btn btn-secondary edit-btn" data-id="${event.id}">Rediger</button>
        <button class="btn btn-danger delete-btn" data-id="${event.id}">Slet</button>
      </div>
    </td>
  `;

    return tr;
}

async function loadEvents() {
    clearTableError();
    tbody.innerHTML = `<tr><td colspan="7" class="muted">Indlæser...</td></tr>`;

    try {
        const res = await fetch(API_BASE, { credentials: "same-origin" });

        if (!res.ok) {
            throw new Error(`Kunne ikke hente events. Status: ${res.status}`);
        }

        const events = await res.json();

        if (!Array.isArray(events) || events.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="muted">Ingen events endnu.</td></tr>`;
            return;
        }

        tbody.innerHTML = "";
        events.forEach(e => tbody.appendChild(createRow(e)));

    } catch (err) {
        console.error(err);
        tbody.innerHTML = `<tr><td colspan="7" class="muted">Fejl ved hentning.</td></tr>`;
        showTableError(err.message);
    }
}

tbody.addEventListener("click", async (e) => {
    const target = e.target;

    // SLET
    if (target.classList.contains("delete-btn")) {
        const id = target.dataset.id;
        if (!confirm(`Vil du slette event #${id}?`)) return;

        try {
            const res = await fetch(`${API_BASE}/${id}`, {
                method: "DELETE",
                credentials: "same-origin"
            });

            if (!res.ok) {
                throw new Error(`Slet fejlede. Status: ${res.status}`);
            }

            showMessage(`Event #${id} slettet.`, "success");
            await loadEvents();
        } catch (err) {
            console.error(err);
            showMessage(err.message, "error");
        }
    }

    // REDIGER
    if (target.classList.contains("edit-btn")) {
        const id = target.dataset.id;

        try {
            const res = await fetch(`${API_BASE}/${id}`, {
                credentials: "same-origin"
            });

            if (!res.ok) {
                throw new Error(`Kunne ikke hente event #${id}. Status: ${res.status}`);
            }

            const event = await res.json();

            document.getElementById("title").value = event.title ?? "";
            document.getElementById("saunagusMasterName").value =
                event.saunagusMasterName ?? event.gusmesterName ?? "";
            document.getElementById("saunagusMasterImageUrl").value =
                event.saunagusMasterImageUrl ?? event.gusmesterImageUrl ?? "";
            document.getElementById("description").value = event.description ?? "";
            document.getElementById("durationMinutes").value = event.durationMinutes ?? 45;
            document.getElementById("capacity").value = event.capacity ?? 12;
            document.getElementById("price").value = event.price ?? 99;
            document.getElementById("status").value = event.status ?? "UPCOMING";

            form.dataset.editId = id;
            cancelEditBtn.style.display = "inline-block";
            showMessage(`Redigerer event #${id}`, "success");

            window.scrollTo({ top: 0, behavior: "smooth" });
        } catch (err) {
            console.error(err);
            showMessage(err.message, "error");
        }
    }
});

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const dto = {
        title: document.getElementById("title").value.trim(),
        saunagusMasterName: document.getElementById("saunagusMasterName").value.trim(),
        saunagusMasterImageUrl: document.getElementById("saunagusMasterImageUrl").value.trim(),
        description: document.getElementById("description").value.trim(),
        durationMinutes: Number(document.getElementById("durationMinutes").value),
        capacity: Number(document.getElementById("capacity").value),
        price: Number(document.getElementById("price").value),
        status: document.getElementById("status").value
    };

    const editId = form.dataset.editId;
    const url = editId ? `${API_BASE}/${editId}` : API_BASE;
    const method = editId ? "PUT" : "POST";

    try {
        const res = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify(dto)
        });

        if (!res.ok) {
            // prøv at læse JSON-fejl hvis din GlobalExceptionHandler returnerer det
            let msg = `Gem fejlede. Status: ${res.status}`;
            try {
                const body = await res.json();
                msg = body.message || body.error || body.details || msg;
            } catch {}
            throw new Error(msg);
        }

        const saved = await res.json();
        showMessage(editId ? `Event #${saved.id} opdateret.` : `Event #${saved.id} oprettet.`, "success");

        resetEditMode();
        await loadEvents();
    } catch (err) {
        console.error(err);
        showMessage(err.message, "error");
    }
});

// init
document.addEventListener("DOMContentLoaded", async () => {
    cancelEditBtn.style.display = "none";
    showMessage("Opret nyt event.", "success");
    await loadEvents();
});
