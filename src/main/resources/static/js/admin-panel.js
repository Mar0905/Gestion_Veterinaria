document.addEventListener("DOMContentLoaded", () => {

    const rows = document.querySelectorAll("table tr");

    rows.forEach((row, index) => {
        row.style.opacity = 0;
        row.style.transform = "translateX(-10px)";

        setTimeout(() => {
            row.style.transition = "all 0.4s ease";
            row.style.opacity = 1;
            row.style.transform = "translateX(0)";
        }, 150 + index * 60);
    });

    const deleteLinks = document.querySelectorAll("a[href*='eliminar']");

    deleteLinks.forEach(link => {
        link.addEventListener("click", function (e) {

            const confirmDelete = confirm("¿Estás seguro de que deseas eliminar este registro?");

            if (!confirmDelete) {
                e.preventDefault();
            }
        });
    });

    const modals = document.querySelectorAll(".modal");

    modals.forEach(modal => {
        modal.addEventListener("show.bs.modal", function () {
            this.querySelector(".modal-content").style.transform = "scale(0.9)";
            this.querySelector(".modal-content").style.opacity = 0;

            setTimeout(() => {
                this.querySelector(".modal-content").style.transition = "all 0.3s ease";
                this.querySelector(".modal-content").style.transform = "scale(1)";
                this.querySelector(".modal-content").style.opacity = 1;
            }, 50);
        });
    });

});
