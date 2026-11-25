# Sistemi Digjital për Menaxhimin e Bibliotekës

Projekt individual për lëndën **Projekt – Praktikë Profesionale**.

**Studenti:** Frrok Bushaj  
**Profesori:** Prof. Ass. Dr. Endrit Fetahi

## Gjendja aktuale — Raporti Teknik 3

Është zhvilluar ndërfaqja web me HTML, CSS, JavaScript dhe Thymeleaf,
e lidhur me backend-in Spring Boot dhe databazën MariaDB.

Funksionet e realizuara përmes ndërfaqes janë:

- Shfletimi publik i katalogut të librave.
- Hyrja dhe dalja nga llogaria.
- Shtimi i librave nga bibliotekari.
- Kërkesa për rezervim nga lexuesi me abonim aktiv.
- Shfaqja dhe miratimi i kërkesave nga bibliotekari.
- Shfaqja e rezervimeve personale, statusit dhe afatit të marrjes.

Janë përmirësuar formularët, butonat dhe navigimi.
Statuset e rezervimeve dallohen me ngjyra.

Pas miratimit, sistemi cakton një kopje të disponueshme dhe afatin
48-orësh për marrjen e librit. Rezervimet që skadojnë kontrollohen
automatikisht dhe kopjet përkatëse lirohen. Miratimi përdor
transaksione dhe bllokim të rreshtave në databazë.

API-ja mbështet gjithashtu kërkimin e librit sipas ID-së.
Validimi dhe kontrolli i të drejtave zbatohen në backend.

Për fazat e ardhshme mbeten regjistrimi publik dhe verifikimi real
me email, menaxhimi i marrjes dhe kthimit të librave, zgjatjet,
gjobat, njoftimet, pagesat, materialet digjitale, shitjet,
donacionet dhe backup-i automatik. Përditësimi/fshirja e librave
dhe kërkimi tekstual ende nuk janë implementuar.

## Teknologjitë

- Java 25 dhe Spring Boot 4.1.1.
- MariaDB 10.4.32 nga XAMPP; databaza quhet `bibloteka`.
- Spring JDBC dhe `JdbcTemplate` për kërkesat SQL.
- Spring Security për hyrjen përmes formularit, sesionet, HTTP Basic, rolet, BCrypt dhe mbrojtjen CSRF.
- Jakarta Validation për kontrollin e input-it.
- Maven për varësitë dhe përpilimin; Git/GitHub për versionimin.
- Thymeleaf për shfaqjen e të dhënave dinamike në faqet HTML.
- HTML, CSS dhe JavaScript për ndërtimin dhe ndërveprimin e ndërfaqes.

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

Aplikacioni punon në http://localhost:8080.

Faqet kryesore janë:

- `/` ose `/katalogu` — katalogu publik.
- `/hyrja` — hyrja në llogari.
- `/librat/shto` — shtimi i librit nga bibliotekari.
- `/bibliotekari/rezervimet` — menaxhimi i rezervimeve.
- `/lexuesi/rezervimet` — rezervimet personale të lexuesit.

Endpoint-i `/api/librat` kthen listën e librave në format JSON.

Për ndalje përdorni Ctrl + C dhe, nëse pyet terminali,
përgjigjuni `Y`.

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

Ndërfaqja përdor sesionin e krijuar pas hyrjes përmes formularit.
Për testimin e API-së mbështetet edhe autentikimi HTTP Basic.

Kërkesat POST kërkojnë tokenin CSRF dhe sesionin përkatës.
Gjatë testimit me PowerShell, tokeni merret nga `/api/csrf`.
Fusha `headerName` përcakton emrin e header-it që duhet dërguar.
Roli i gabuar ose tokeni CSRF i pavlefshëm mund të japin 403.

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
- [Raporti Teknik 3 në Word](Raporti_Teknik_3.docx)
- [Skema SQL dhe librat fillestarë](database/bibloteka.sql)
- [Të dhënat ndihmëse demo](database/demo.sql)
