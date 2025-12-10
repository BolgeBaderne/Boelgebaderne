// booking.js
// Bruger userId fra booking.html (Thymeleaf)

let currentWeekStart = startOfWeek(new Date()); // mandag i denne uge
let allSlots = []; // fyldes fra backend

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("prevWeekBtn").addEventListener("click", () => {
        currentWeekStart.setDate(currentWeekStart.getDate() - 7);
        renderCalendar();
    });

    document.getElementById("nextWeekBtn").addEventListener("click", () => {
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);
        renderCalendar();
    });

    document.getElementById("cancelBtn").addEventListener("click", closeModal);

    loadSlots();
});

function startOfWeek(date) {
    const d = new Date(date);
    const day = d.getDay(); // 0 = søn, 1 = man...
    const diff = (day === 0 ? -6 : 1 - day); // juster til mandag
    d.setDate(d.getDate() + diff);
    d.setHours(0,0,0,0);
    return d;
}

async function loadSlots() {
    const res = await fetch(`/api/bookings/available?userId=${userId}`);
    allSlots = await res.json();
    renderCalendar();
}

function renderCalendar() {
    const container = document.getElementById("calendarContainer");
    container.innerHTML = "";

    const weekEnd = new Date(currentWeekStart);
    weekEnd.setDate(weekEnd.getDate() + 6);

    const formatter = new Intl.DateTimeFormat("da-DK", { day: "numeric", month: "short", year: "numeric" });
    document.getElementById("weekLabel").innerText =
        formatter.format(currentWeekStart) + " - " + formatter.format(weekEnd);

    // tidsrækker (fx 8-20)
    const startHour = 8;
    const endHour = 21;

    const grid = document.createElement("div");
    grid.className = "calendar-grid";

    // venstre tids-kolonne
    const timeCol = document.createElement("div");
    timeCol.className = "calendar-time-col";

    for (let h = startHour; h <= endHour; h++) {
        const row = document.createElement("div");
        row.className = "time-slot-label";
        row.innerText = h + ":00";
        timeCol.appendChild(row);
    }
    grid.appendChild(timeCol);

    // 7 dag-kolonner
    for (let i = 0; i < 7; i++) {
        const dayDate = new Date(currentWeekStart);
        dayDate.setDate(dayDate.getDate() + i);

        const dayCol = document.createElement("div");
        dayCol.className = "calendar-day-col";

        const header = document.createElement("div");
        header.className = "day-header";

        const weekdayNames = ["Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn"];
        const dayName = weekdayNames[i];

        const strong = document.createElement("strong");
        strong.innerText = dayName;
        header.appendChild(strong);

        const small = document.createElement("span");
        small.innerText = dayDate.getDate() + "/" + (dayDate.getMonth() + 1);
        header.appendChild(small);

        dayCol.appendChild(header);

        const body = document.createElement("div");
        body.className = "day-body";

        // slots til denne dag
        const daySlots = allSlots.filter(slot => {
            const d = parseSlotDate(slot.startTime);
            return isSameDay(d, dayDate);
        });

        daySlots.forEach(slot => {
            const d = parseSlotDate(slot.startTime);
            const top = (d.getHours() - startHour) * 60 + (d.getMinutes() / 60) * 60;
            const height = 60; // 1 time = 60px (simpelt)

            const card = document.createElement("div");
            card.className = "session-card";

            // type-farve
            const title = slot.title.toUpperCase();
            if (title.includes("MEDLEM")) {
                card.classList.add("session-member");
            } else if (title.includes("VAGT")) {
                card.classList.add("session-vagt");
            } else {
                card.classList.add("session-guest");
            }

            if (slot.full) {
                card.classList.add("session-full");
            }

            card.style.top = top + "px";
            card.style.height = height + "px";

            const titleDiv = document.createElement("div");
            titleDiv.className = "session-card-title";
            titleDiv.innerText = slot.title;
            card.appendChild(titleDiv);

            const metaDiv = document.createElement("div");
            metaDiv.className = "session-card-meta";

            const timeSpan = document.createElement("span");
            timeSpan.innerText = d.toLocaleTimeString("da-DK", { hour: "2-digit", minute: "2-digit" });
            metaDiv.appendChild(timeSpan);

            const capacitySpan = document.createElement("span");
            capacitySpan.className = "session-capacity";
            capacitySpan.innerText = `${slot.booked}/${slot.capacity}`;
            metaDiv.appendChild(capacitySpan);

            // lille "lås" ikon hvis kun medlemmer
            const lockSpan = document.createElement("span");
            lockSpan.className = "session-lock";
            if (title.includes("MEDLEM") || title.includes("VAGT")) {
                lockSpan.innerText = "🔒";
            } else {
                lockSpan.innerText = "🔓";
            }
            metaDiv.appendChild(lockSpan);

            card.appendChild(metaDiv);

            if (!slot.full && slot.userAllowed) {
                card.addEventListener("click", () => openModal(slot));
            } else if (slot.full) {
                card.addEventListener("click", () => showMessage("Denne tid er fuldt booket.", true));
            } else {
                card.addEventListener("click", () => showMessage("Du har ikke adgang til denne tid.", true));
            }

            body.appendChild(card);
        });

        dayCol.appendChild(body);
        grid.appendChild(dayCol);
    }

    container.appendChild(grid);
}

function parseSlotDate(str) {
    // format: "yyyy-MM-dd HH:mm"
    const [datePart, timePart] = str.split(" ");
    const [year, month, day] = datePart.split("-").map(Number);
    const [hour, minute] = timePart.split(":").map(Number);
    return new Date(year, month - 1, day, hour, minute);
}

function isSameDay(a, b) {
    return a.getFullYear() === b.getFullYear() &&
        a.getMonth() === b.getMonth() &&
        a.getDate() === b.getDate();
}

// --- Modal og booking ---

let selectedSlot = null;

function openModal(slot) {
    selectedSlot = slot;
    document.getElementById("modalText").innerText =
        `${slot.title}\n${slot.startTime}\nPladser: ${slot.booked}/${slot.capacity}`;
    document.getElementById("modalMessage").innerText = "";
    document.getElementById("modalMessage").className = "alert";

    document.getElementById("bookingModal").style.display = "flex";

    document.getElementById("confirmBtn").onclick = confirmBooking;
}

function closeModal() {
    document.getElementById("bookingModal").style.display = "none";
    selectedSlot = null;
}

async function confirmBooking() {
    if (!selectedSlot) return;

    const res = await fetch("/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId, eventId: selectedSlot.eventId })
    });

    const msgEl = document.getElementById("modalMessage");

    let data;
    try {
        data = await res.json();
    } catch (e) {
        msgEl.innerText = "Ukendt fejl.";
        msgEl.className = "alert alert-error";
        return;
    }

    if (!res.ok || data.error) {
        msgEl.innerText = data.error || "Noget gik galt.";
        msgEl.className = "alert alert-error";
        return;
    }

    msgEl.innerText = "Du har nu booket en tid ✅";
    msgEl.className = "alert alert-success";

    // opdater kalender efter kort delay
    setTimeout(() => {
        closeModal();
        loadSlots();
        showMessage("Booking gennemført!", false);
    }, 800);
}

function showMessage(text, isError) {
    const container = document.getElementById("messageContainer");
    container.innerHTML = "";
    const div = document.createElement("div");
    div.className = "alert " + (isError ? "alert-error" : "alert-success");
    div.innerText = text;
    container.appendChild(div);

    setTimeout(() => {
        container.innerHTML = "";
    }, 3000);
}
