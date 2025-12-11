// /js/calendar.js
document.addEventListener("DOMContentLoaded", () => {
    const DAY_START = 8;   // 08:00
    const DAY_END = 22;    // 22:00
    const HOURS_TOTAL = DAY_END - DAY_START; // 14

    const bookingSection = document.querySelector("#booking");
    const weekRangeEl = bookingSection.querySelector(".week-range");
    const prevBtn = bookingSection.querySelector(".prev-week");
    const nextBtn = bookingSection.querySelector(".next-week");
    const dayCols = bookingSection.querySelectorAll(".day-col");
    const daysRow = bookingSection.querySelector(".days-row");
    const timeIndicator = bookingSection.querySelector(".time-indicator");

    const pop = document.getElementById("booking-pop");
    const popContent = pop.querySelector(".pop-content");
    const bookingForm = document.getElementById("booking-form");
    const bookingConfirm = pop.querySelector(".booking-confirm");
    const popClose = pop.querySelector(".pop-close");
    const confirmOk = pop.querySelector(".confirm-ok");

    let weekOffset = 0; // 0 = current week

    // helper: format date
    function fmtDate(d) {
        return d.toLocaleDateString("da-DK", { day: "2-digit", month: "short" });
    }

    // update week UI
    function renderWeek(offset = 0) {
        weekOffset = offset;
        const start = new Date();
        const day = start.getDay(); // 0 Sun, 1 Mon etc.
        // We want Monday-first. compute monday:
        const monday = new Date(start);
        const diffToMon = ((day + 6) % 7); // days since Monday
        monday.setDate(start.getDate() - diffToMon + (offset * 7));

        const end = new Date(monday);
        end.setDate(monday.getDate() + 6);

        weekRangeEl.textContent = `${fmtDate(monday)} — ${fmtDate(end)}`;

        // render day headers
        daysRow.innerHTML = "";
        for (let i=0;i<7;i++) {
            const d = new Date(monday);
            d.setDate(monday.getDate() + i);
            const label = d.toLocaleDateString("da-DK", { weekday: "short" });
            const el = document.createElement("div");
            el.className = "day";
            el.innerHTML = `<div class="day-name">${label}</div><div class="day-date">${d.getDate()}</div>`;
            daysRow.appendChild(el);

            // tag day column with date (ISO)
            const col = dayCols[i];
            col.dataset.date = d.toISOString();
            col.innerHTML = ""; // clear existing blocks
        }

        // demo: add some demo blocks (replace with real data)
        addDemoBlocks(monday);
        positionTimeIndicator();
    }

    // utility to convert time string to top/height %
    function timeToPosition(startHourStr, endHourStr) {
        // example startHourStr = "14:00"
        const [sh, sm] = startHourStr.split(":").map(Number);
        const [eh, em] = endHourStr.split(":").map(Number);

        const startDecimal = (sh + sm/60) - DAY_START;
        const endDecimal = (eh + em/60) - DAY_START;
        const topPercent = (startDecimal / HOURS_TOTAL) * 100;
        const heightPercent = ((endDecimal - startDecimal) / HOURS_TOTAL) * 100;
        return { topPercent, heightPercent };
    }

    // add a block DOM in col index
    function createBlock(colIndex, opts) {
        const col = dayCols[colIndex];
        const wrapper = document.createElement("div");
        wrapper.className = `gus-block ${opts.type === "member" ? "gus-member" : "gus-open"}`;
        wrapper.tabIndex = 0;
        wrapper.setAttribute("role","button");
        wrapper.setAttribute("aria-pressed","false");
        wrapper.dataset.title = opts.title;
        wrapper.dataset.start = opts.start;
        wrapper.dataset.end = opts.end;
        wrapper.dataset.capacity = opts.capacity;
        wrapper.dataset.taken = opts.taken;
        wrapper.dataset.type = opts.type;

        const pos = timeToPosition(opts.start, opts.end);
        wrapper.style.top = `${pos.topPercent}%`;
        wrapper.style.height = `${pos.heightPercent}%`;
        wrapper.style.left = "6%";
        wrapper.style.right = "6%";

        wrapper.innerHTML = `
      <div class="title">${opts.title}</div>
      <div class="meta">${opts.gusmeister ? "Gusmester: "+opts.gusmeister : ""}</div>
      <div class="meta">Tid: <strong>${opts.start}–${opts.end}</strong></div>
      <div class="meta">Kapacitet: <span class="gus-cap">${opts.taken}/${opts.capacity}</span></div>
    `;

        // click / keyboard
        wrapper.addEventListener("click", () => openPop(wrapper));
        wrapper.addEventListener("keydown", (e) => {
            if (e.key === "Enter" || e.key === " ") { e.preventDefault(); openPop(wrapper); }
        });

        col.appendChild(wrapper);
    }

    // demo blocks (replace with real data later)
    function addDemoBlocks(mondayBase) {
        // place some demo blocks across days 2..6
        const demo = [
            { day: 2, start: "14:00", end:"18:00", title:"Medlemsgus", gusmeister:"Peter A.", capacity:12, taken:3, type:"member"},
            { day: 5, start: "15:00", end:"19:00", title:"Åben gus", gusmeister:"Maria S.", capacity:15, taken:0, type:"open"},
            { day: 6, start: "19:00", end:"21:00", title:"Åben gus - Aften", gusmeister:"Lars N.", capacity:12, taken:8, type:"open"}
        ];
        demo.forEach(d => createBlock(d.day, d));
    }

    // open popup for block
    function openPop(blockEl) {
        const title = blockEl.dataset.title;
        const start = blockEl.dataset.start;
        const end = blockEl.dataset.end;
        const cap = blockEl.dataset.capacity;
        const taken = blockEl.dataset.taken;
        const type = blockEl.dataset.type;

        popContent.innerHTML = `
      <p class="detail"><strong>${title}</strong></p>
      <p class="detail">Tid: ${start} - ${end}</p>
      <p class="detail">Kapacitet: <strong>${taken}/${cap}</strong></p>
      <p class="detail">${type === "member" ? "Kun for medlemmer. Log ind for at booke." : "Åben for alle - du kan booke nedenfor."}</p>
    `;

        // show form vs login button
        if (type === "member") {
            bookingForm.setAttribute("aria-hidden","true");
            bookingForm.style.display = "none";
            bookingConfirm.style.display = "none";
            pop.querySelector(".pop-login").style.display = "inline-block";
            pop.querySelector(".pop-login").textContent = "Log ind";
        } else {
            bookingForm.setAttribute("aria-hidden","false");
            bookingForm.style.display = "block";
            bookingConfirm.style.display = "none";
            pop.querySelector(".pop-login").style.display = "inline-block";
            pop.querySelector(".pop-login").textContent = "Log ind (valgfrit)";
        }

        // store context
        pop.dataset.context = JSON.stringify({
            title, start, end, cap, taken, type
        });

        openDialog(pop);
    }

    // open dialog (focus management)
    function openDialog(el) {
        el.setAttribute("aria-hidden","false");
        bookingForm.querySelector("input, button").focus();
        // trap focus: for simplicity we set document-level key handler to close on Esc
        document.addEventListener("keydown", escClose);
    }

    function closeDialog() {
        pop.setAttribute("aria-hidden","true");
        bookingForm.reset();
        bookingForm.style.display = "none";
        bookingConfirm.style.display = "none";
        document.removeEventListener("keydown", escClose);
    }

    function escClose(e) {
        if (e.key === "Escape") closeDialog();
    }

    // form submit simulation
    bookingForm.addEventListener("submit", (e) => {
        e.preventDefault();
        // show confirmation (simulate server)
        bookingForm.style.display = "none";
        bookingConfirm.style.display = "block";
        bookingConfirm.setAttribute("aria-hidden","false");
        bookingConfirm.querySelector(".confirm-ok").focus();
    });

    // close handlers
    popClose.addEventListener("click", closeDialog);
    confirmOk.addEventListener("click", closeDialog);
    pop.querySelector(".pop-login").addEventListener("click", () => {
        // placeholder: redirect to login page or open your login modal
        window.location.href = "/login";
    });

    // current time indicator
    function positionTimeIndicator() {
        const now = new Date();
        const hour = now.getHours() + now.getMinutes()/60;
        if (hour < DAY_START || hour > DAY_END) {
            timeIndicator.style.display = "none";
            return;
        }
        const percent = ((hour - DAY_START) / HOURS_TOTAL) * 100;
        timeIndicator.style.top = `${percent}%`;
        timeIndicator.style.display = "block";
    }

    // nav buttons
    prevBtn.addEventListener("click", () => renderWeek(weekOffset - 1));
    nextBtn.addEventListener("click", () => renderWeek(weekOffset + 1));

    // initial render
    renderWeek(0);

    // update time indicator every minute
    setInterval(positionTimeIndicator, 60 * 1000);
});
