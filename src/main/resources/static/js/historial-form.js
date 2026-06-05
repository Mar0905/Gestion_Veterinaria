document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const textareas = document.querySelectorAll("textarea");

    textareas.forEach((area, index) => {
        area.style.opacity = 0;
        area.style.transform = "translateX(-15px)";

        setTimeout(() => {
            area.style.transition = "all 0.4s ease";
            area.style.opacity = 1;
            area.style.transform = "translateX(0)";
        }, 150 + index * 120);
    });

    form.addEventListener("submit", (e) => {

        let valid = true;

        textareas.forEach(area => {
            if (area.hasAttribute("required") && !area.value.trim()) {
                valid = false;
                area.style.borderColor = "#f87171";
                area.style.boxShadow = "0 0 0 3px rgba(248,113,113,0.3)";
            } else {
                area.style.borderColor = "#22c55e";
                area.style.boxShadow = "0 0 0 3px rgba(34,197,94,0.3)";
            }
        });

        if (!valid) {
            e.preventDefault();
        }

    });

});
