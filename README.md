# Sistemi Digjital për Menaxhimin e Bibliotekës

Projekt individual për lëndën **Projekt – Praktikë Profesionale**.

**Studenti:** Frrok Bushaj  
**Profesori:** Prof. Ass. Dr. Endrit Fetahi

## Gjendja aktuale — Raporti Teknik 2

Është krijuar databaza reale dhe lidhja e saj me Spring Boot.
API-ja mbështet listimin dhe shtimin e librave, kërkimin sipas ID-së,
kërkesën për rezervim, miratimin nga bibliotekari dhe skadimin automatik.
Janë provuar validimi i input-it, autentikimi dhe kufizimet sipas rolit.

Procesi kryesor është: lexuesi me abonim aktiv paraqet kërkesën;
bibliotekari e miraton; sistemi cakton një kopje të disponueshme dhe
afatin 48-orësh. Detyra periodike kontrollon çdo minutë rezervimet e
skaduara dhe liron kopjet përkatëse. Gjatë miratimit përdoren transaksione
dhe bllokim i rreshtave në databazë.

Ndërfaqja web ende nuk është ndërtuar. Tabela `huazimet` ekziston,
por dorëzimi dhe kthimi ende nuk kanë endpoint-e. Regjistrimi publik,
emaili real i verifikimit, gjobat dhe kontrolli i tyre, pagesat, materialet
digjitale, shitjet, donacionet dhe backup-i automatik mbeten për fazat e tjera.
Përditësimi/fshirja e librave dhe kërkimi tekstual ende nuk janë implementuar.

## Teknologjitë

- Java 25 dhe Spring Boot 4.1.1.
- MariaDB 10.4.32 nga XAMPP; databaza quhet `bibloteka`.
- Spring JDBC dhe `JdbcTemplate` për kërkesat SQL.
- Spring Security për HTTP Basic, rolet, BCrypt dhe CSRF.
- Jakarta Validation për kontrollin e input-it.
- Maven për varësitë dhe përpilimin; Git/GitHub për versionimin.
- Thymeleaf është përfshirë si varësi; HTML, CSS dhe JavaScript do të përdoren për ndërfaqen.

Në Raportin 1 ishte planifikuar MySQL. Implementimi lokal përdor MariaDB,
serverin e përfshirë në XAMPP.

## Përgatitja e databazës

1. Aktivizoni serverin e databazës. Konfigurimi lokal përdor portën **3306**.
2. Krijoni një databazë bosh me emrin `bibloteka`.
3. Në phpMyAdmin, zgjidhni databazën dhe importoni `database/bibloteka.sql`.

Eksporti përmban shtatë tabela dhe dy libra testues. Importimi bëhet në
databazë bosh; për një databazë ekzistuese nuk nevojitet riimportimi.

Konfigurimi i parazgjedhur është:

```properties
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/bibloteka
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

Nëse përdoruesi i databazës ka kredenciale të tjera, caktoni `DB_USERNAME`
dhe `DB_PASSWORD` në mjedisin e terminalit përpara nisjes.

## Nisja për demonstrim

Nevojitet **JDK 25** dhe internet për shkarkimin fillestar të varësive.
Hapni PowerShell brenda folderit ku gjendet `pom.xml`.

Ekzekutoni secilin nga dy rreshtat më poshtë veçmas. Vendosni fjalëkalime
testuese me të paktën tetë karaktere kur të kërkohen:

```powershell
$env:DEMO_LIBRARIAN_PASSWORD = [System.Net.NetworkCredential]::new("", (Read-Host "Fjalekalimi i bibliotekarit" -AsSecureString)).Password
```

```powershell
$env:DEMO_READER_PASSWORD = [System.Net.NetworkCredential]::new("", (Read-Host "Fjalekalimi i lexuesit" -AsSecureString)).Password
```

Pastaj, në të njëjtin terminal:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Profili `demo` krijon ose përditëson vetëm llogaritë:

- `bibliotekar@demo.test`, roli `BIBLIOTEKAR`.
- `lexues@demo.test`, roli `LEXUES`.

Fjalëkalimet ruhen si hash BCrypt. Këto llogari shënohen si të verifikuara
për demonstrim; dërgimi i emailit real ende nuk është implementuar.

Pasi aplikacioni të niset, ekzekutoni `database/demo.sql` në phpMyAdmin.
Ky skedar përgatit paketën bazë, kopjen `LIB-0001` të librit 1 dhe një
abonim aktiv testues për lexuesin. Abonimi aktivizohet manualisht,
pa kryer pagesë reale. Komandat shmangin dyfishimin gjatë përsëritjes.

Aplikacioni punon në **http://localhost:8080**. Për katalogun hapni
**http://localhost:8080/api/librat**. Faqja kryesore `/` ende nuk ka ndërfaqe.
Për ndalje përdorni **Ctrl + C** dhe, nëse pyet terminali, përgjigjuni `Y`.

## Endpoint-et

| Metoda | Endpoint-i | Qasja | Rezultati kryesor |
|---|---|---|---|
| GET | `/api/librat` | Publike | 200, lista e librave |
| GET | `/api/librat/{id}` | Publike | 200 me librin ose 404 |
| POST | `/api/librat` | Bibliotekar | 201; validim 400 |
| GET | `/api/csrf` | Publike | Tokeni CSRF dhe emri i header-it |
| POST | `/api/rezervimet` | Lexues me abonim aktiv | 201; kërkesë e përsëritur 409 |
| POST | `/api/rezervimet/{id}/mirato` | Bibliotekar | 200; mungesë 404 ose konflikt 409 |

Shembull për `POST /api/librat`:

```json
{
  "titulli": "Libri demonstrues",
  "permbledhja": "Përmbledhje e shkurtër.",
  "gjuha": "Shqip"
}
```

Shembull për `POST /api/rezervimet`:

```json
{ "libriId": 1 }
```

ID-ja e lexuesit merret nga përdoruesi i autentikuar, jo nga trupi JSON.
Për miratim përdorni ID-në e kthyer nga krijimi i rezervimit.

Kërkesat POST dërgojnë HTTP Basic, tokenin CSRF dhe të njëjtin sesion
ku u mor tokeni nga `/api/csrf`. `headerName` i përgjigjes përcakton
header-in që duhet dërguar. Pas rinisjes së aplikacionit merret token i ri.
Roli i gabuar ose CSRF i pavlefshëm mund të japin 403.

## Testimi i kryer

Janë kryer prova manuale me shfletues dhe PowerShell:

- Listim real nga databaza dhe shtim libri me 201.
- Refuzim i titullit me vetëm hapësira me 400.
- Refuzim i shtimit të librit nga lexuesi me 403.
- Krijim rezervimi me 201 dhe ndalim i përsëritjes me 409.
- Refuzim i miratimit nga lexuesi me 403.
- Miratim nga bibliotekari me 200 dhe afat 48-orësh.
- Ndalim i miratimit të përsëritur me 409.
- Skadim automatik dhe kthim i kopjes në `E_DISPONUESHME`,
  pasi afati i rezervimit testues u shkurtua vetëm për provë.

Ende nuk janë kryer teste ngarkese me shumë klientë njëkohësisht.

## Dokumentacioni

- [Raporti Teknik 1 në Word](Dokumenti%20Word.docx)
- [Raporti Teknik 2 në Word](Raporti_Teknik_2.docx)
- [Skema SQL dhe librat fillestarë](database/bibloteka.sql)
- [Të dhënat ndihmëse demo](database/demo.sql)
