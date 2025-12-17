if (userId == null) {
    // guest mode: vis kalender, men disable booking-knapper / kald der kræver login
}

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
// Hent slots fra backend for den aktuelle uge
// ------------------------------

async function loadSlots() {
    // currentWeekStart er allerede mandag i den uge vi viser
// currentWeekStart er allerede mandag i den uge vi viser (lokal tid)
    const y = currentWeekStart.getFullYear();
    const m = String(currentWeekStart.getMonth() + 1).padStart(2, "0");
    const d = String(currentWeekStart.getDate()).padStart(2, "0");
    const weekStartStr = `${y}-${m}-${d}`; // "yyyy-MM-dd" i lokal tid

    try {
        const res = await fetch(
            `/api/bookings/week?userId=${userId}&weekStart=${weekStartStr}`
        );

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

    const startHour = 7;
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

// Hvor højt oppe i kolonnen?
            const top = (d.getHours() - startHour) * 60 + d.getMinutes();

// Forsøg at læse varighed ud fra teksten "HH:MM-HH:MM"
            let height = 60; // fallback = 1 time
            const match = slot.title.match(/(\d{2}:\d{2})-(\d{2}:\d{2})/);
            if (match) {
                const [_, startStr, endStr] = match;
                const [sh, sm] = startStr.split(":").map(Number);
                const [eh, em] = endStr.split(":").map(Number);
                const startMinutes = sh * 60 + sm;
                const endMinutes = eh * 60 + em;
                const durationMinutes = endMinutes - startMinutes;
                if (durationMinutes > 0) {
                    height = durationMinutes; // 1 minut = 1px
                }
            }

            const card = document.createElement("div");
            card.className = "session-card";

            const titleUpper = slot.title.toUpperCase();

            const isOpening = titleUpper.includes("ÅBENT");
            const isMemberOpen = isOpening && titleUpper.includes("MEDLEM");
            const isGuestOpen = isOpening && titleUpper.includes("OFFENTLIG");

            if (titleUpper.includes("GUS")) {
                // Gus (som nu, bare én blok)
                card.classList.add("session-gus");
            } else if (isMemberOpen) {
                // Medlems-åbent = grøn baggrund
                card.classList.add("session-open-member");
            } else if (isGuestOpen) {
                // Offentligt åbent = gul baggrund
                card.classList.add("session-open-guest");
            } else if (titleUpper.includes("MEDLEM")) {
                card.classList.add("session-member");
            } else if (titleUpper.includes("OFFENTLIG")) {
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

            if (!isMemberOpen) {
                const capacitySpan = document.createElement("span");
                capacitySpan.className = "session-capacity";
                capacitySpan.innerText = `${slot.booked}/${slot.capacity}`;
                metaDiv.appendChild(capacitySpan);
            }


            card.appendChild(metaDiv);

            // Grøn medlems-åbent: INGEN klik
            if (!isMemberOpen) {
                // Kun klikbar hvis:
                // - ikke fuld
                // - bruger må booke
                // - dagen ikke er grå (fortid)
                if (!slot.full && slot.userAllowed && !body.classList.contains("past-day")) {
                    card.addEventListener("click", () => openModal(slot));
                } else if (slot.full && !body.classList.contains("past-day")) {
                    // Fuld tid: åbn modal så venteliste-knap kan vises
                    card.addEventListener("click", () => openModal(slot));
                } else if (!body.classList.contains("past-day")) {
                    // Ikke adgang: præcis besked som kravet
                    card.addEventListener("click", () => showMessage("Din medlemsstatus giver ikke adgang til denne tid.", true));
                }
            }

            card.style.top = top + "px";
            card.style.height = height + "px";
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
    const startHour = 7;
    const endHour = 21;

    // Vis kun linjen i den uge, hvor vi er nu
    const thisWeekMonday = startOfWeek(now);
    if (thisWeekMonday.getTime() !== currentWeekStart.getTime()) {
        line.style.display = "none";
        return;
    }

    // Find dagens kolonne (0 = mandag, 6 = søndag)
    const weekdayIndex = (now.getDay() + 6) % 7;
    const dayCols = grid.querySelectorAll(".calendar-day-col");
    const todayCol = dayCols[weekdayIndex];
    if (!todayCol) {
        line.style.display = "none";
        return;
    }

    const minutesFromStart = (now.getHours() - startHour) * 60 + now.getMinutes();
    if (minutesFromStart < 0 || minutesFromStart > (endHour - startHour) * 60) {
        line.style.display = "none";
        return;
    }

    line.style.display = "block";
    line.style.top = minutesFromStart + "px";
    line.style.left = todayCol.offsetLeft + "px";
    line.style.width = todayCol.offsetWidth + "px";
}

function getGusInfo(slot) {
    const d = parseSlotDate(slot.startTime);
    const weekday = d.getDay(); // 0 = søndag, 1 = mandag, ... 6 = lørdag
    const hour = d.getHours();
    const minute = d.getMinutes();

    const upper = slot.title.toUpperCase();
    if (!upper.includes("GUS")) {
        return null;
    }

    // ---------------------------
    // GÆSTE-GUS (onsdag 20-21) → LARS
    // ---------------------------
    if (weekday === 3 && hour === 20) { // 3 = onsdag
        return {
            name: "Lars Svendsen",
            label: "Klassisk finsk gus",
            description:
                "Lars' gæste-gus er en varm og rolig oplevelse med klassiske finske elementer, duft af birk og behagelige sving – perfekt til både begyndere og erfarne."
        };
    }

    // ---------------------------
    // MEDLEMSGUS
    // ---------------------------

    // Tirsdag 20-21 → LARS
    if (weekday === 2 && hour === 20) { // tirsdag
        return {
            name: "Lars Svendsen",
            label: "Klassisk finsk gus",
            description:
                "Lars laver klassiske finske gus med fokus på varme, ro og rene dufte. En dyb og traditionel gusoplevelse."
        };
    }

    // Torsdag 17:30–18:30 → METTE
    if (weekday === 4 && hour === 17 && minute === 30) {
        return {
            name: "Mette Nielsen",
            label: "Russisk banya-stil",
            description:
                "Mette kombinerer varme, rytme og banya-teknikker for en intens og meditativ oplevelse."
        };
    }

    // Torsdag 20–21 → THOMAS
    if (weekday === 4 && hour === 20) {
        return {
            name: "Thomas Andersen",
            label: "Aromaterapi-gus",
            description:
                "Thomas bruger æteriske olier og rolige bevægelser til at skabe et afstressende og sanseligt gus."
        };
    }

    // Fredag 07–08 → SOFIE
    if (weekday === 5 && hour === 7) {
        return {
            name: "Sofie Kristensen",
            label: "Moderne fusion-gus",
            description:
                "Sofie blander moderne teknikker, kreative dufte og et hint af energi til en let og opløftende morgengus."
        };
    }

    // Fallback
    return null;
}



// ------------------------------
// Modal + booking
// ------------------------------
function openModal(slot) {
    selectedSlot = slot;

    const upper = slot.title.toUpperCase();
    let ekstraLinje = "";

    // Pris-tekst for offentlig åbent (gul)
    if (upper.includes("OFFENTLIG") && !upper.includes("GUS")) {
        ekstraLinje += "\nPris: 80 kr. for 1 times adgang";
    }

    // Pris-tekst for gæste-gus
    if (upper.includes("GÆSTE-GUS")) {
        ekstraLinje += "\nPris: 120 kr. for 1 times gus";
    }

    // Hent info om gusmester + beskrivelse hvis det er en gus-session
    const gusInfo = getGusInfo(slot);
    if (gusInfo) {
        ekstraLinje += `\n\nGusmester: ${gusInfo.name}\n${gusInfo.label}\n${gusInfo.description}`;
    }

    document.getElementById("modalText").innerText =
        `${slot.title}\n${slot.startTime}\nPladser: ${slot.booked}/${slot.capacity}${ekstraLinje}`;
    document.getElementById("modalMessage").innerText = "";
    document.getElementById("modalMessage").className = "alert";
    document.getElementById("bookingModal").style.display = "flex";

    // Toggle knapper afhængigt af om tiden er fuld
    const confirmBtn = document.getElementById("confirmBtn");
    const waitlistBtn = document.getElementById("waitlistBtn");

    if (slot.full) {
        // Kun venteliste-knap når fuld
        confirmBtn.style.display = "none";
        waitlistBtn.style.display = "inline-block";

        waitlistBtn.onclick = () => {
            const msgEl = document.getElementById("modalMessage");

            if (typeof window.signupWaitlist === "function") {
                window.signupWaitlist(selectedSlot);
                return;
            }

            msgEl.innerText = "Venteliste-funktion er ikke tilkoblet endnu.";
            msgEl.className = "alert alert-error";
        };

    } else {
        // Normal booking når ikke fuld
        confirmBtn.style.display = "inline-block";
        waitlistBtn.style.display = "none";
    }


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
        body: JSON.stringify({
            userId: userId,
            eventId: selectedSlot.eventId,
            title: selectedSlot.title,
            startTime: selectedSlot.startTime,
            capacity: selectedSlot.capacity
        })
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

    msgEl.innerText = "Du har nu booket en tid";
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
