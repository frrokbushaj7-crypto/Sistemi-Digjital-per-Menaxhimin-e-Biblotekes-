# Sistemi Digjital për Menaxhimin e Bibliotekës

Projekt individual për lëndën **Projekt – Praktikë Profesionale**.

**Studenti:** Frrok Bushaj  
**Profesori:** Prof. Ass. Dr. Endrit Fetahi

## Përshkrimi i projektit

Ky projekt synon zhvillimin e një sistemi digjital që lehtëson
punën e bibliotekarit dhe qasjen e lexuesve në shërbimet e bibliotekës.

Bibliotekari do të menaxhojë librat, kopjet fizike, rezervimet,
huazimet dhe kthimet. Lexuesit do të mund të kërkojnë libra,
të paraqesin kërkesa për rezervim dhe të shohin huazimet,
afatet e kthimit dhe detyrimet personale.

Në kërkesat e projektit parashikohen gjithashtu abonime,
materiale digjitale, pagesa, shitje dhe donacione librash.

## Teknologjitë

- **Java 25** – logjika e aplikacionit.
- **Spring Boot 4.1.1** – ndërtimi i aplikacionit web.
- **Thymeleaf** – përgatitja e faqeve HTML me të dhëna dinamike.
- **HTML, CSS dhe JavaScript** – ndërfaqja e përdoruesit.
- **MySQL** – databaza e planifikuar për ruajtjen e të dhënave.
- **Maven** – menaxhimi i varësive dhe përpilimi i projektit.
- **Git dhe GitHub** – ndjekja dhe ruajtja e ndryshimeve në kod.

## Gjendja aktuale

Projekti është në fazën e Raportit Teknik 1, që përfshin analizën
e problemit, kërkesat dhe dizajnin fillestar teknik.

Është krijuar struktura fillestare Spring Boot dhe është
verifikuar nisja e aplikacionit lokalisht. Funksionalitetet
e bibliotekës dhe lidhja me databazën ende nuk janë implementuar.

## Nisja e aplikacionit

Për nisjen e aplikacionit nevojitet **JDK 25**. Gjatë nisjes
së parë kërkohet lidhje me internetin për shkarkimin e varësive.

Hapni PowerShell në folderin ku gjendet `pom.xml` dhe ekzekutoni:

```powershell
.\mvnw.cmd spring-boot:run
```

Pas nisjes së suksesshme, aplikacioni është i arritshëm në:

http://localhost:8080

Aktualisht faqja kryesore nuk është krijuar, prandaj hapja
e kësaj adrese mund të shfaqë përgjigjen **404**.

Për ta ndalur aplikacionin, shtypni **Ctrl + C** në terminal.

## Dokumentacioni

[Shkarko Raportin Teknik 1 në Word](Dokumenti%20Word.docx)