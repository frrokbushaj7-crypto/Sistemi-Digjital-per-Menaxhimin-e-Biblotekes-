const formulari = document.getElementById("formulari-librit");
const butoni = document.getElementById("ruaj-librin");
const mesazhi = document.getElementById("mesazhi");

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

formulari.addEventListener("submit", async (event) => {
    event.preventDefault();

    const libri = {
        titulli: document.getElementById("titulli").value.trim(),
        permbledhja: document.getElementById("permbledhja").value.trim(),
        gjuha: document.getElementById("gjuha").value.trim()
    };

    if (!libri.titulli || !libri.gjuha) {
        mesazhi.setAttribute("role", "alert");
        mesazhi.textContent = "Plotëso titullin dhe gjuhën.";
        return;
    }

    butoni.disabled = true;
    butoni.textContent = "Duke ruajtur…";
    mesazhi.setAttribute("role", "status");
    mesazhi.textContent = "Po ruhet libri.";

    try {
        const pergjigjja = await fetch(formulari.action, {
            method: "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(libri)
        });

        if (pergjigjja.redirected || pergjigjja.status === 401) {
            throw new Error("Hyr përsëri në llogari për të vazhduar.");
        }

        if (pergjigjja.status === 400) {
            throw new Error("Kontrollo plotësimin dhe gjatësinë e fushave.");
        }

        if (pergjigjja.status === 403) {
            throw new Error(
                "Ruajtja nuk u lejua. Rifresko faqen dhe sigurohu " +
                "që je kyçur si bibliotekar."
            );
        }

        if (!pergjigjja.ok) {
            throw new Error("Libri nuk u ruajt. Provo përsëri më vonë.");
        }

        const libriIRuajtur = await pergjigjja.json();

        mesazhi.textContent =
            `Libri "${libriIRuajtur.titulli}" u ruajt me sukses.`;

        formulari.reset();
    } catch (gabimi) {
        mesazhi.setAttribute("role", "alert");
        mesazhi.textContent = gabimi instanceof TypeError
            ? "Nuk u konfirmua ruajtja. Kontrollo lidhjen dhe katalogun."
            : gabimi.message;
    } finally {
        butoni.disabled = false;
        butoni.textContent = "Ruaj librin";
    }
});

butoni.disabled = false;