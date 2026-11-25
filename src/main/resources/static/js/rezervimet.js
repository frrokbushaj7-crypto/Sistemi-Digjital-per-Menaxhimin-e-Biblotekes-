const butonat = document.querySelectorAll(".kerko-rezervim");
const mesazhi = document.getElementById("mesazhi-rezervimit");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

butonat.forEach((butoni) => {
    butoni.addEventListener("click", async () => {
        const libriId = Number(butoni.dataset.libriId);
        let uKrijua = false;

        butoni.disabled = true;
        butoni.textContent = "Duke dërguar…";

        mesazhi.setAttribute("role", "status");
        mesazhi.textContent = "Po dërgohet kërkesa për rezervim.";

        try {
            const pergjigjja = await fetch("/api/rezervimet", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({ libriId })
            });

            if (pergjigjja.redirected || pergjigjja.status === 401) {
                throw new Error("Hyr përsëri në llogari për të vazhduar.");
            }

            if (pergjigjja.status === 403) {
                throw new Error(
                    "Kërkesa nuk u lejua. Nevojitet abonim aktiv. " +
                    "Nëse e ke abonimin aktiv, rifresko faqen dhe provo përsëri."
                );
            }

            if (pergjigjja.status === 404) {
                throw new Error("Libri nuk u gjet. Rifresko katalogun.");
            }

            if (pergjigjja.status === 409) {
                throw new Error(
                    "Ke tashmë një kërkesë në pritje ose një " +
                    "rezervim të miratuar për këtë libër."
                );
            }

            if (pergjigjja.status === 400) {
                throw new Error("Të dhënat e kërkesës janë të pavlefshme.");
            }

            if (!pergjigjja.ok) {
                throw new Error("Rezervimi nuk u krijua. Provo më vonë.");
            }

            await pergjigjja.json();
            uKrijua = true;

            mesazhi.textContent =
                "Kërkesa u dërgua me sukses. Prit miratimin e bibliotekarit.";
        } catch (gabimi) {
            mesazhi.setAttribute("role", "alert");
            mesazhi.textContent = gabimi instanceof TypeError
                ? "Nuk u konfirmua kërkesa. Kontrollo lidhjen dhe provo përsëri."
                : gabimi.message;
        } finally {
            butoni.disabled = uKrijua;
            butoni.textContent = uKrijua
                ? "Kërkesa u dërgua"
                : "Kërko rezervim";
        }
    });

    butoni.disabled = false;
});