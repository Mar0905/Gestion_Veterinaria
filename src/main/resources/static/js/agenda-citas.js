document.addEventListener("DOMContentLoaded", () => {

    const rows = document.querySelectorAll("tbody tr");
    const forms = document.querySelectorAll("form");

    rows.forEach((row, index) => {
        row.style.opacity = 0;
        row.style.transform = "translateX(-15px)";

        setTimeout(() => {
            row.style.transition = "all 0.4s ease";
            row.style.opacity = 1;
            row.style.transform = "translateX(0)";
        }, 150 + index * 80);
    });

    forms.forEach(form => {
        form.addEventListener("submit", (e) => {
            const confirmDelete = confirm("¿Deseas eliminar esta cita?");
            if (!confirmDelete) {
                e.preventDefault();
            }
        });
    });

});
