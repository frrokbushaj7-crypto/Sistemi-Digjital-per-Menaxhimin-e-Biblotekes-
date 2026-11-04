-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 04, 2026 at 03:21 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bibloteka`
--

-- --------------------------------------------------------

--
-- Table structure for table `abonimet`
--

CREATE TABLE `abonimet` (
  `abonimi_id` int(11) NOT NULL,
  `lexuesi_id` int(11) NOT NULL,
  `paketa_id` int(11) NOT NULL,
  `lloji_pageses` enum('MUJOR','VJETOR') NOT NULL,
  `data_fillimit` datetime NOT NULL,
  `data_perfundimit` datetime NOT NULL,
  `cmimi_aplikuar` decimal(8,2) NOT NULL,
  `statusi` enum('NE_PRITJE','AKTIV','ANULUAR') NOT NULL DEFAULT 'NE_PRITJE'
) ;

-- --------------------------------------------------------

--
-- Table structure for table `huazimet`
--

CREATE TABLE `huazimet` (
  `huazimi_id` int(11) NOT NULL,
  `rezervimi_id` int(11) DEFAULT NULL,
  `lexuesi_id` int(11) NOT NULL,
  `kopje_id` int(11) NOT NULL,
  `data_marrjes` datetime NOT NULL DEFAULT current_timestamp(),
  `afati_kthimit` datetime NOT NULL,
  `data_kthimit` datetime DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Table structure for table `kopjet`
--

CREATE TABLE `kopjet` (
  `kopje_id` int(11) NOT NULL,
  `libri_id` int(11) NOT NULL,
  `kodi_kopjes` varchar(50) NOT NULL,
  `gjendja` varchar(30) NOT NULL DEFAULT 'E_DISPONUESHME',
  `destinimi` enum('HUAZIM','SHITJE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `librat`
--

CREATE TABLE `librat` (
  `libri_id` int(11) NOT NULL,
  `titulli` varchar(255) NOT NULL,
  `permbledhja` text DEFAULT NULL,
  `gjuha` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `librat`
--

INSERT INTO `librat` (`libri_id`, `titulli`, `permbledhja`, `gjuha`) VALUES
(1, 'Libri testues 1', 'Përmbledhje për testimin e katalogut.', 'Shqip'),
(2, 'Libri testues 2', 'Një libër tjetër për testimin e sistemit.', 'Anglisht');

-- --------------------------------------------------------

--
-- Table structure for table `paketat`
--

CREATE TABLE `paketat` (
  `paketa_id` int(11) NOT NULL,
  `emri` varchar(50) NOT NULL,
  `afati_huazimit_dite` int(11) NOT NULL,
  `lejon_shkarkime` tinyint(1) NOT NULL DEFAULT 0,
  `kufi_shkarkimesh` int(11) DEFAULT NULL,
  `cmimi_mujor` decimal(8,2) DEFAULT NULL,
  `cmimi_vjetor` decimal(8,2) DEFAULT NULL
) ;

-- --------------------------------------------------------

--
-- Table structure for table `perdoruesit`
--

CREATE TABLE `perdoruesit` (
  `perdoruesi_id` int(11) NOT NULL,
  `emri` varchar(100) NOT NULL,
  `mbiemri` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `fjalekalimi_hash` varchar(255) NOT NULL,
  `roli` enum('LEXUES','BIBLIOTEKAR') NOT NULL DEFAULT 'LEXUES',
  `email_verifikuar` tinyint(1) NOT NULL DEFAULT 0,
  `data_regjistrimit` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rezervimet`
--

CREATE TABLE `rezervimet` (
  `rezervimi_id` int(11) NOT NULL,
  `lexuesi_id` int(11) NOT NULL,
  `libri_id` int(11) NOT NULL,
  `kopje_id` int(11) DEFAULT NULL,
  `statusi` enum('NE_PRITJE','MIRATUAR','REFUZUAR','ANULUAR','SKADUAR','PERFUNDUAR') NOT NULL DEFAULT 'NE_PRITJE',
  `data_kerkeses` datetime NOT NULL DEFAULT current_timestamp(),
  `data_miratimit` datetime DEFAULT NULL,
  `afati_marrjes` datetime DEFAULT NULL,
  `miratuar_nga` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `abonimet`
--
ALTER TABLE `abonimet`
  ADD PRIMARY KEY (`abonimi_id`),
  ADD KEY `idx_abonimet_lexuesi` (`lexuesi_id`,`statusi`,`data_perfundimit`),
  ADD KEY `fk_abonimet_paketa` (`paketa_id`);

--
-- Indexes for table `huazimet`
--
ALTER TABLE `huazimet`
  ADD PRIMARY KEY (`huazimi_id`),
  ADD UNIQUE KEY `uq_huazimet_rezervimi` (`rezervimi_id`),
  ADD KEY `idx_huazimet_kopja_kthimi` (`kopje_id`,`data_kthimit`),
  ADD KEY `fk_huazimet_lexuesi` (`lexuesi_id`);

--
-- Indexes for table `kopjet`
--
ALTER TABLE `kopjet`
  ADD PRIMARY KEY (`kopje_id`),
  ADD UNIQUE KEY `uq_kopjet_kodi` (`kodi_kopjes`),
  ADD KEY `fk_kopjet_libri` (`libri_id`);

--
-- Indexes for table `librat`
--
ALTER TABLE `librat`
  ADD PRIMARY KEY (`libri_id`);

--
-- Indexes for table `paketat`
--
ALTER TABLE `paketat`
  ADD PRIMARY KEY (`paketa_id`),
  ADD UNIQUE KEY `uq_paketat_emri` (`emri`);

--
-- Indexes for table `perdoruesit`
--
ALTER TABLE `perdoruesit`
  ADD PRIMARY KEY (`perdoruesi_id`),
  ADD UNIQUE KEY `uq_perdoruesit_email` (`email`);

--
-- Indexes for table `rezervimet`
--
ALTER TABLE `rezervimet`
  ADD PRIMARY KEY (`rezervimi_id`),
  ADD KEY `idx_rezervimet_radha` (`libri_id`,`statusi`,`data_kerkeses`),
  ADD KEY `fk_rezervimet_lexuesi` (`lexuesi_id`),
  ADD KEY `fk_rezervimet_kopja` (`kopje_id`),
  ADD KEY `fk_rezervimet_miratuesi` (`miratuar_nga`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `abonimet`
--
ALTER TABLE `abonimet`
  MODIFY `abonimi_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `huazimet`
--
ALTER TABLE `huazimet`
  MODIFY `huazimi_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `kopjet`
--
ALTER TABLE `kopjet`
  MODIFY `kopje_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `librat`
--
ALTER TABLE `librat`
  MODIFY `libri_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `paketat`
--
ALTER TABLE `paketat`
  MODIFY `paketa_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `perdoruesit`
--
ALTER TABLE `perdoruesit`
  MODIFY `perdoruesi_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rezervimet`
--
ALTER TABLE `rezervimet`
  MODIFY `rezervimi_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `abonimet`
--
ALTER TABLE `abonimet`
  ADD CONSTRAINT `fk_abonimet_lexuesi` FOREIGN KEY (`lexuesi_id`) REFERENCES `perdoruesit` (`perdoruesi_id`),
  ADD CONSTRAINT `fk_abonimet_paketa` FOREIGN KEY (`paketa_id`) REFERENCES `paketat` (`paketa_id`);

--
-- Constraints for table `huazimet`
--
ALTER TABLE `huazimet`
  ADD CONSTRAINT `fk_huazimet_kopja` FOREIGN KEY (`kopje_id`) REFERENCES `kopjet` (`kopje_id`),
  ADD CONSTRAINT `fk_huazimet_lexuesi` FOREIGN KEY (`lexuesi_id`) REFERENCES `perdoruesit` (`perdoruesi_id`),
  ADD CONSTRAINT `fk_huazimet_rezervimi` FOREIGN KEY (`rezervimi_id`) REFERENCES `rezervimet` (`rezervimi_id`);

--
-- Constraints for table `kopjet`
--
ALTER TABLE `kopjet`
  ADD CONSTRAINT `fk_kopjet_libri` FOREIGN KEY (`libri_id`) REFERENCES `librat` (`libri_id`);

--
-- Constraints for table `rezervimet`
--
ALTER TABLE `rezervimet`
  ADD CONSTRAINT `fk_rezervimet_kopja` FOREIGN KEY (`kopje_id`) REFERENCES `kopjet` (`kopje_id`),
  ADD CONSTRAINT `fk_rezervimet_lexuesi` FOREIGN KEY (`lexuesi_id`) REFERENCES `perdoruesit` (`perdoruesi_id`),
  ADD CONSTRAINT `fk_rezervimet_libri` FOREIGN KEY (`libri_id`) REFERENCES `librat` (`libri_id`),
  ADD CONSTRAINT `fk_rezervimet_miratuesi` FOREIGN KEY (`miratuar_nga`) REFERENCES `perdoruesit` (`perdoruesi_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
