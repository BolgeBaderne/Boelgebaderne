document.addEventListener("DOMContentLoaded", () => {
    const questions = document.querySelectorAll(".faq-question");

    questions.forEach(btn => {
        btn.addEventListener("click", () => {
            const answer = btn.nextElementSibling;
            const isOpen = answer.classList.contains("open");

            document.querySelectorAll(".faq-answer.open").forEach(openAnswer => {
                openAnswer.classList.remove("open");
                openAnswer.previousElementSibling.setAttribute("aria-expanded", "false");
            });

            if (!isOpen) {
                answer.classList.add("open");
                btn.setAttribute("aria-expanded", "true");
            }
        });
    });
});
