document.addEventListener("DOMContentLoaded", () => {

    const inputs = document.querySelectorAll("input");
    const form = document.querySelector("form");
    const button = document.querySelector("button");

    inputs.forEach((input, index) => {
        input.style.opacity = 0;
        input.style.transform = "translateX(-20px)";

        setTimeout(() => {
            input.style.transition = "all 0.5s ease";
            input.style.opacity = 1;
            input.style.transform = "translateX(0)";
        }, 200 + index * 100);
    });

    button.addEventListener("click", function (e) {

        const ripple = document.createElement("span");
        ripple.classList.add("ripple");

        const rect = button.getBoundingClientRect();
        ripple.style.left = `${e.clientX - rect.left}px`;
        ripple.style.top = `${e.clientY - rect.top}px`;

        button.appendChild(ripple);

        setTimeout(() => {
            ripple.remove();
        }, 600);
    });

    form.addEventListener("submit", function (e) {

        let valid = true;

        inputs.forEach(input => {
            if (!input.value.trim()) {
                valid = false;
                input.style.borderColor = "#d9534f";
                input.style.boxShadow = "0 0 0 3px rgba(217, 83, 79, 0.2)";
            } else {
                input.style.borderColor = "#28a745";
                input.style.boxShadow = "0 0 0 3px rgba(40, 167, 69, 0.2)";
            }
        });

        if (!valid) {
            e.preventDefault();
        }
    });

});
