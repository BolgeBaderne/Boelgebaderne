// booking.js

// Mandag i den aktuelle uge
let currentWeekStart = startOfWeek(new Date());
// Fyldes fra backend (/api/bookings/available?userId=...)
let allSlots = [];
// Gemmer valgt slot til modal
let selectedSlot = null;

// ------------------------------
// INITIALISERING
// ------------------------------
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("prevWeekBtn").addEventListener("click", () => {
        currentWeekStart.setDate(currentWeekStart.getDate() - 7);
        loadSlots();
    });

    document.getElementById("nextWeekBtn").addEventListener("click", () => {
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);
        loadSlots();
    });

    document.getElementById("cancelBtn").addEventListener("click", closeModal);

    // Første load
    loadSlots();

    // Opdater rød tidslinje hvert minut
    setInterval(updateCurrentTimeLine, 60_000);
});

// ------------------------------
// Hjælp: find mandag i uge
// ------------------------------
function startOfWeek(date) {
    const d = new Date(date);
    const day = d.getDay();           // 0 = søn, 1 = man ...
    const diff = (day === 0 ? -6 : 1 - day); // flyt til mandag
    d.setDate(d.getDate() + diff);
    d.setHours(0, 0, 0, 0);
    return d;
}

// ------------------------------
// Hent slots fra backend
// ------------------------------
async function loadSlots() {
    try {
        const res = await fetch(`/api/bookings/available?userId=${userId}`);
        if (!res.ok) {
            console.error("Fejl fra backend:", res.status);
            allSlots = [];
        } else {
            allSlots = await res.json();
        }
    } catch (e) {
        console.error("Netværksfejl:", e);
        allSlots = [];
    }

    renderCalendar();
    updateCurrentTimeLine();
}

// ------------------------------
// Tegn kalender-ugen
// ------------------------------
function renderCalendar() {
    const container = document.getElementById("calendarContainer");
    container.innerHTML = "";

    const weekEnd = new Date(currentWeekStart);
    weekEnd.setDate(weekEnd.getDate() + 6);

    const formatter = new Intl.DateTimeFormat("da-DK", {
        day: "numeric",
        month: "short",
        year: "numeric"
    });

    document.getElementById("weekLabel").innerText =
        formatter.format(currentWeekStart) + " - " + formatter.format(weekEnd);

    const startHour = 8;
    const endHour = 21;

    // Hele uge-kalenderen
    const grid = document.createElement("div");
    grid.className = "calendar-grid";

    // VENSTRE tids-kolonne
    const timeCol = document.createElement("div");
    timeCol.className = "calendar-time-col";

    for (let h = startHour; h <= endHour; h++) {
        const row = document.createElement("div");
        row.className = "time-slot-label";
        row.innerText = h + ":00";
        timeCol.appendChild(row);
    }
    grid.appendChild(timeCol);

    // Dato-logik til grå dage
    const today = new Date();
    const todayAtMidnight = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const isCurrentWeek = isSameDay(startOfWeek(today), currentWeekStart);

    const weekdayNames = ["Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn"];

    // 7 DAG-KOLONNER
    for (let i = 0; i < 7; i++) {
        const dayDate = new Date(currentWeekStart);
        dayDate.setDate(dayDate.getDate() + i);

        const dayCol = document.createElement("div");
        dayCol.className = "calendar-day-col";

        const header = document.createElement("div");
        header.className = "day-header";

        const strong = document.createElement("strong");
        strong.innerText = weekdayNames[i];
        header.appendChild(strong);

        const small = document.createElement("span");
        small.innerText = dayDate.getDate() + "/" + (dayDate.getMonth() + 1);
        header.appendChild(small);

        dayCol.appendChild(header);

        const body = document.createElement("div");
        body.className = "day-body";

        // GRÅ og lås dage, der ligger tidligere i ugen end i dag
        if (isCurrentWeek && dayDate < todayAtMidnight) {
            body.classList.add("past-day");
            dayCol.classList.add("past-day");
        }

        // Slots for denne dag
        const daySlots = Array.isArray(allSlots)
            ? allSlots.filter(slot => {
                const d = parseSlotDate(slot.startTime);
                return isSameDay(d, dayDate);
            })
            : [];

        daySlots.forEach(slot => {
            const d = parseSlotDate(slot.startTime);

            const top = (d.getHours() - startHour) * 60 + d.getMinutes();
            const height = 60; // 1 time = 60px

            const card = document.createElement("div");
            card.className = "session-card";

            const titleUpper = slot.title.toUpperCase();

            // Du vil ikke vise vagt-tider i skemaet
            if (titleUpper.includes("VAGT")) {
                return;
            } else if (titleUpper.includes("GUS")) {
                card.classList.add("session-gus");
            } else if (titleUpper.includes("MEDLEM")) {
                card.classList.add("session-member");
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
            timeSpan.innerText = d.toLocaleTimeString("da-DK", {
                hour: "2-digit",
                minute: "2-digit"
            });
            metaDiv.appendChild(timeSpan);

            const capacitySpan = document.createElement("span");
            capacitySpan.className = "session-capacity";
            capacitySpan.innerText = `${slot.booked}/${slot.capacity}`;
            metaDiv.appendChild(capacitySpan);

            const lockSpan = document.createElement("span");
            lockSpan.className = "session-lock";
            if (titleUpper.includes("MEDLEM")) {
                lockSpan.innerText = "🔒";
            } else {
                lockSpan.innerText = "🔓";
            }
            metaDiv.appendChild(lockSpan);

            card.appendChild(metaDiv);

            // Kun klikbar hvis:
            // - ikke fuld
            // - bruger må booke
            // - dagen ikke er grå (fortid)
            if (!slot.full && slot.userAllowed && !body.classList.contains("past-day")) {
                card.addEventListener("click", () => openModal(slot));
            } else if (slot.full) {
                card.addEventListener("click", () => showMessage("Denne tid er fuldt booket.", true));
            } else if (!body.classList.contains("past-day")) {
                card.addEventListener("click", () => showMessage("Du har ikke adgang til denne tid.", true));
            }

            body.appendChild(card);
        });

        dayCol.appendChild(body);
        grid.appendChild(dayCol);
    }

    // LAV RØD TIDSLINJE SOM BARN AF GRID
    const timeLine = document.createElement("div");
    timeLine.id = "current-time-line";
    grid.appendChild(timeLine);

    container.appendChild(grid);

    // Opdater position på den røde linje når kalenderen er tegnet
    updateCurrentTimeLine();
}

// ------------------------------
// Dato-hjælp
// ------------------------------
function parseSlotDate(str) {
    // Håndtér både "yyyy-MM-dd HH:mm" og ISO med "T"
    if (str.includes("T")) {
        return new Date(str);
    }
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

// ------------------------------
// Rød tids-linje (nu inde i grid)
// ------------------------------
function updateCurrentTimeLine() {
    const grid = document.querySelector(".calendar-grid");
    const line = document.getElementById("current-time-line");
    if (!grid || !line) return;

    const now = new Date();
    const hour = now.getHours();
    const minutes = now.getMinutes();

    const startHour = 8;
    const minutesFromStart = (hour - startHour) * 60 + minutes;

    // 1 minut = 1px (fordi 60px = 1 time)
    const y = minutesFromStart;

    line.style.top = y + "px";
}

// ------------------------------
// Modal + booking
// ------------------------------
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
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({userId, eventId: selectedSlot.eventId})
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

    setTimeout(() => {
        closeModal();
        loadSlots();
        showMessage("Booking gennemført!", false);
    }, 800);
}

// ------------------------------
// Besked øverst på siden
// ------------------------------
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
