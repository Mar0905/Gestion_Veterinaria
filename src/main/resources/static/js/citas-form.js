document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const fields = document.querySelectorAll(".form-control, .form-select");

    fields.forEach((field, index) => {
        field.style.opacity = 0;
        field.style.transform = "translateX(-15px)";

        setTimeout(() => {
            field.style.transition = "all 0.4s ease";
            field.style.opacity = 1;
            field.style.transform = "translateX(0)";
        }, 150 + index * 80);
    });

    form.addEventListener("submit", (e) => {
        let valid = true;

        fields.forEach(field => {
            if (!field.value.trim()) {
                valid = false;
                field.style.borderColor = "#f87171";
                field.style.boxShadow = "0 0 0 3px rgba(248,113,113,0.3)";
            } else {
                field.style.borderColor = "#22c55e";
                field.style.boxShadow = "0 0 0 3px rgba(34,197,94,0.3)";
            }
        });

        if (!valid) {
            e.preventDefault();
        }
    });

});
