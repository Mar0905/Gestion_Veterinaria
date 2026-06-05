document.addEventListener("DOMContentLoaded", () => {

    const links = document.querySelectorAll("nav .nav-link");

    const currentPath = window.location.pathname;

    links.forEach(link => {
        if (link.getAttribute("href") === currentPath) {
            link.classList.add("active");
        }
    });

    links.forEach((link, index) => {
        link.style.opacity = 0;
        link.style.transform = "translateX(-10px)";

        setTimeout(() => {
            link.style.transition = "all 0.3s ease";
            link.style.opacity = 1;
            link.style.transform = "translateX(0)";
        }, 150 + index * 70);
    });

});
