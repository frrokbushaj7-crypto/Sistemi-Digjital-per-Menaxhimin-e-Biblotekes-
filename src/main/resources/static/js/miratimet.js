const butonat = document.querySelectorAll(".mirato-rezervimin");
const mesazhi = document.getElementById("mesazhi-miratimit");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

butonat.forEach((butoni) => {
    butoni.addEventListener("click", async () => {
        const rezervimiId = Number(butoni.dataset.rezervimiId);
        let uMiratua = false;

        butoni.disabled = true;
        butoni.textContent = "Duke miratuar…";

        mesazhi.setAttribute("role", "status");
        mesazhi.textContent = "Po përpunohet miratimi.";

        try {
            const pergjigjja = await fetch(
                `/api/rezervimet/${rezervimiId}/mirato`,
                {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Accept": "application/json",
                        [csrfHeader]: csrfToken
                    }
                }
            );

            if (pergjigjja.redirected || pergjigjja.status === 401) {
                throw new Error("Hyr përsëri në llogari për të vazhduar.");
            }

            if (pergjigjja.status === 403) {
                throw new Error(
                    "Miratimi nuk u lejua. Rifresko faqen dhe " +
                    "sigurohu që je kyçur si bibliotekar."
                );
            }

            if (pergjigjja.status === 404) {
                throw new Error("Rezervimi nuk u gjet. Rifresko listën.");
            }

            if (pergjigjja.status === 409) {
                throw new Error(
                    "Rezervimi nuk mund të miratohet. Kontrollo statusin, " +
                    "abonimin e lexuesit dhe kopjet e disponueshme."
                );
            }

            if (!pergjigjja.ok) {
                throw new Error("Miratimi nuk u krye. Provo më vonë.");
            }

            const rezervimi = await pergjigjja.json();
            uMiratua = true;

            const rreshti = butoni.closest("tr");

            rreshti.querySelector(".statusi").textContent =
                rezervimi.statusi;

            rreshti.querySelector(".afati-marrjes").textContent =
                new Date(rezervimi.afatiMarrjes).toLocaleString("sq-AL");

            mesazhi.textContent =
                "Rezervimi u miratua. Lexuesi ka 48 orë për ta marrë librin.";
        } catch (gabimi) {
            mesazhi.setAttribute("role", "alert");
            mesazhi.textContent = gabimi instanceof TypeError
                ? "Nuk u konfirmua rezultati. Rifresko listën për ta kontrolluar."
                : gabimi.message;
        } finally {
            butoni.disabled = uMiratua;
            butoni.textContent = uMiratua ? "Miratuar" : "Mirato";
        }
    });

    butoni.disabled = false;
});