-- Te dhena vetem per demonstrimin lokal te Raportit Teknik 2.
-- Importo fillimisht bibloteka.sql dhe nise aplikacionin me profilin demo.
-- Llogarite demo krijohen nga DemoDataConfig me fjalekalimet e mjedisit.
USE bibloteka;

INSERT INTO paketat (
    emri, afati_huazimit_dite, lejon_shkarkime,
    kufi_shkarkimesh, cmimi_mujor, cmimi_vjetor
)
SELECT 'Baze', 14, FALSE, 0, 2.00, 10.00
WHERE NOT EXISTS (SELECT 1 FROM paketat WHERE emri = 'Baze');

INSERT INTO kopjet (libri_id, kodi_kopjes, gjendja, destinimi)
SELECT libri_id, 'LIB-0001', 'E_DISPONUESHME', 'HUAZIM'
FROM librat
WHERE libri_id = 1
  AND NOT EXISTS (SELECT 1 FROM kopjet WHERE kodi_kopjes = 'LIB-0001');

INSERT INTO abonimet (
    lexuesi_id, paketa_id, lloji_pageses,
    data_fillimit, data_perfundimit, cmimi_aplikuar, statusi
)
SELECT u.perdoruesi_id, p.paketa_id, 'MUJOR',
       NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), p.cmimi_mujor, 'AKTIV'
FROM perdoruesit u
JOIN paketat p ON p.emri = 'Baze'
WHERE u.email = 'lexues@demo.test'
  AND NOT EXISTS (
      SELECT 1 FROM abonimet a
      WHERE a.lexuesi_id = u.perdoruesi_id
        AND a.statusi = 'AKTIV'
        AND a.data_fillimit <= NOW()
        AND a.data_perfundimit > NOW()
  );
