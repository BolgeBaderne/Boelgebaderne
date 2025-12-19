document.addEventListener("DOMContentLoaded", () => {

    const messageBox = document.getElementById("loginMessage");
    if (!messageBox) return;

    const params = new URLSearchParams(window.location.search);

    if (params.has("error")) {
        messageBox.textContent = "Forkert email eller password. Prøv igen.";
        messageBox.className = "login-message error";
    }

    if (params.has("logout")) {
        messageBox.textContent = "Du er nu logget ud.";
        messageBox.className = "login-message success";
    }

    if (params.has("auth")) {
        messageBox.textContent = "Du skal være logget ind for at se den side.";
        messageBox.className = "login-message info";
    }
});
