// -------------------------------------------
// ON LOAD
// -------------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    loadUser();
    loadBookings();
    loadShifts();
    loadEvents();
    loadPosts(); // ligger i main.js
});

// -------------------------------------------
// USER
// -------------------------------------------
function loadUser() {
    fetch("/api/users/me")
        .then(res => res.json())
        .then(user => {

            const accountBtn = document.querySelector(".account-trigger");

            // Grundindhold (navn)
            accountBtn.innerHTML = user.name;

            // Admin badge (kun hvis ADMIN)
            if (user.role === "ADMIN") {
                accountBtn.innerHTML +=
                    ` <span class="admin-badge">Admin</span>`;
            }

            // Velkomst
            document.querySelector(".dashboard-intro h1").innerText =
                `Velkommen, ${user.name}`;

            // Admin-sektion
            if (user.role !== "ADMIN") {
                document.querySelector(".admin-section").style.display = "none";
            }
        });
}

// -------------------------------------------
// BOOKINGS
// -------------------------------------------
function loadBookings() {
    fetch("/api/bookings/me")
        .then(res => res.json())
        .then(bookings => {

            const list = document.querySelector(
                ".updates-card ul.dashboard-list"
            );

            list.innerHTML = "";

            bookings.forEach(b => {
                list.innerHTML += `
                    <li>${b.type} · ${formatDate(b.dateTime)}</li>
                `;
            });
        });
}

// -------------------------------------------
// SHIFTS
// -------------------------------------------
function loadShifts() {
    fetch("/api/shifts/me")
        .then(res => res.json())
        .then(shifts => {

            const lists = document.querySelectorAll(".dashboard-list");
            const shiftList = lists[1]; // anden liste = vagter

            shiftList.innerHTML = "";

            shifts.forEach(s => {
                shiftList.innerHTML += `
                    <li>${s.role} · ${formatDate(s.dateTime)}</li>
                `;
            });
        });
}

// -------------------------------------------
// EVENTS (ADMIN)
// -------------------------------------------
function loadEvents() {
    fetch("/api/events")
        .then(res => res.json())
        .then(events => {

            const list = document.querySelector(".admin-list");
            list.innerHTML = "";

            events.forEach(e => {
                list.innerHTML += `
                    <li>
                        ${e.title} · ${formatDate(e.dateTime)}
                        <div class="admin-actions">
                            <button class="btn btn-pill btn-sm"
                                    onclick="editEvent(${e.id})">
                                Rediger
                            </button>
                            <button class="btn btn-pill btn-danger"
                                    onclick="deleteEvent(${e.id})">
                                Slet
                            </button>
                        </div>
                    </li>
                `;
            });
        });
}

// -------------------------------------------
// CREATE POST
// -------------------------------------------
function createPost() {
    const textarea = document.getElementById("postContent");
    const content = textarea.value.trim();

    if (!content) return;

    fetch("/api/posts", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ content })
    })
        .then(() => {
            textarea.value = "";
            loadPosts();
        });
}

// -------------------------------------------
// ADMIN EVENT ACTIONS
// -------------------------------------------
function createEvent() {
    window.location.href = "/admin/create-event.html";
}

function editEvent(id) {
    window.location.href = `/admin/edit-event.html?id=${id}`;
}

function deleteEvent(id) {
    if (!confirm("Er du sikker på, at du vil slette eventet?")) return;

    fetch(`/api/events/${id}`, {
        method: "DELETE"
    }).then(() => loadEvents());
}

// -------------------------------------------
// NAVIGATION
// -------------------------------------------
function goToBookings() {
    window.location.href = "/bookings.html";
}

function goToShifts() {
    window.location.href = "/shifts.html";
}

function goToBookingCalendar() {
    window.location.href = "/booking-calendar.html";
}

function goToEvents() {
    window.location.href = "/events.html";
}

// -------------------------------------------
// HELPERS
// -------------------------------------------
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString("da-DK", {
        day: "numeric",
        month: "long",
        hour: "2-digit",
        minute: "2-digit"
    });
}
