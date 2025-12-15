document.addEventListener("DOMContentLoaded", () => {
    const triggers = document.querySelectorAll(".accordion-question");

    triggers.forEach(trigger => {
        trigger.addEventListener("click", () => {
            const answer = trigger.nextElementSibling;
            const isOpen = answer.classList.contains("open");

            // Close all
            document.querySelectorAll(".accordion-answer.open").forEach(open => {
                open.classList.remove("open");
                open.previousElementSibling.setAttribute("aria-expanded", "false");
            });

            // Open only if not open already
            if (!isOpen) {
                answer.classList.add("open");
                trigger.setAttribute("aria-expanded", "true");
            }
        });
    });
});
