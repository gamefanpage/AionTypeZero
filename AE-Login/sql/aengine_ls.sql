CREATE DATABASE  IF NOT EXISTS `ae_loginserver` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `ae_loginserver`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ae_loginserver
-- ------------------------------------------------------
-- Server version	5.5.41-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_data`
--

DROP TABLE IF EXISTS `account_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(65) COLLATE utf8_unicode_ci NOT NULL,
  `activated` tinyint(1) NOT NULL DEFAULT '1',
  `access_level` tinyint(3) NOT NULL DEFAULT '0',
  `last_server` tinyint(3) NOT NULL DEFAULT '-1',
  `last_ip` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_mac` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'xx-xx-xx-xx-xx-xx',
  `ip_force` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `toll` bigint(20) unsigned NOT NULL DEFAULT '0',
  `bonus_toll` bigint(20) unsigned NOT NULL DEFAULT '0',
  `last_active` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `expiration_time` timestamp NULL DEFAULT NULL,
  `session_duration` int(10) unsigned DEFAULT NULL,
  `accumulated_online` bigint(20) unsigned DEFAULT NULL,
  `accumulated_rest` bigint(20) unsigned DEFAULT NULL,
  `penalty_end` timestamp NULL DEFAULT NULL,
  `membership` tinyint(3) NOT NULL DEFAULT '0',
  `old_membership` tinyint(3) NOT NULL DEFAULT '0',
  `membership_expire` date DEFAULT NULL,
  `craftship` tinyint(3) NOT NULL DEFAULT '0',
  `old_craftship` tinyint(3) NOT NULL DEFAULT '0',
  `craftship_expire` date DEFAULT NULL,
  `apship` tinyint(3) NOT NULL DEFAULT '0',
  `old_apship` tinyint(3) NOT NULL DEFAULT '0',
  `apship_expire` date DEFAULT NULL,
  `collectionship` tinyint(3) NOT NULL DEFAULT '0',
  `old_collectionship` tinyint(3) NOT NULL DEFAULT '0',
  `collectionship_expire` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_data`
--

LOCK TABLES `account_data` WRITE;
/*!40000 ALTER TABLE `account_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_playtime`
--

DROP TABLE IF EXISTS `account_playtime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_playtime` (
  `account_id` int(10) unsigned NOT NULL,
  `accumulated_online` bigint(20) unsigned NOT NULL DEFAULT '0',
  `lastupdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_playtime`
--

LOCK TABLES `account_playtime` WRITE;
/*!40000 ALTER TABLE `account_playtime` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_playtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_rewards`
--

DROP TABLE IF EXISTS `account_rewards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_rewards` (
  `uniqId` int(11) NOT NULL AUTO_INCREMENT,
  `accountId` int(11) NOT NULL,
  `added` varchar(70) NOT NULL DEFAULT '',
  `points` decimal(20,0) NOT NULL DEFAULT '0',
  `bonus_points` decimal(20,0) NOT NULL DEFAULT '0',
  `received` varchar(70) NOT NULL DEFAULT '0',
  `rewarded` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uniqId`),
  KEY `FK_account_rewards` (`accountId`),
  CONSTRAINT `FK_account_rewards` FOREIGN KEY (`accountId`) REFERENCES `account_data` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_rewards`
--

LOCK TABLES `account_rewards` WRITE;
/*!40000 ALTER TABLE `account_rewards` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_rewards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_time`
--

DROP TABLE IF EXISTS `account_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_time` (
  `account_id` int(11) NOT NULL,
  `last_active` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL DEFAULT NULL,
  `session_duration` int(10) DEFAULT '0',
  `accumulated_online` int(10) DEFAULT '0',
  `accumulated_rest` int(10) DEFAULT '0',
  `penalty_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  CONSTRAINT `FK_account_time` FOREIGN KEY (`account_id`) REFERENCES `account_data` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_time`
--

LOCK TABLES `account_time` WRITE;
/*!40000 ALTER TABLE `account_time` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_time` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banned_ip`
--

DROP TABLE IF EXISTS `banned_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banned_ip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banned_ip`
--

LOCK TABLES `banned_ip` WRITE;
/*!40000 ALTER TABLE `banned_ip` DISABLE KEYS */;
/*!40000 ALTER TABLE `banned_ip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banned_mac`
--

DROP TABLE IF EXISTS `banned_mac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banned_mac` (
  `uniId` int(10) NOT NULL AUTO_INCREMENT,
  `address` varchar(20) NOT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `details` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`uniId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banned_mac`
--

LOCK TABLES `banned_mac` WRITE;
/*!40000 ALTER TABLE `banned_mac` DISABLE KEYS */;
/*!40000 ALTER TABLE `banned_mac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gameservers`
--

DROP TABLE IF EXISTS `gameservers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gameservers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `gm_only` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gameservers`
--

LOCK TABLES `gameservers` WRITE;
/*!40000 ALTER TABLE `gameservers` DISABLE KEYS */;
INSERT INTO `gameservers` VALUES (1,'127.0.0.1','aion',0);
/*!40000 ALTER TABLE `gameservers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_transfers`
--

DROP TABLE IF EXISTS `player_transfers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_transfers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source_server` tinyint(3) NOT NULL,
  `target_server` tinyint(3) NOT NULL,
  `source_account_id` int(11) NOT NULL,
  `target_account_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `time_added` varchar(100) DEFAULT NULL,
  `time_performed` varchar(100) DEFAULT NULL,
  `time_done` varchar(100) DEFAULT NULL,
  `comment` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_transfers`
--

LOCK TABLES `player_transfers` WRITE;
/*!40000 ALTER TABLE `player_transfers` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_transfers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tasks` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `task_type` enum('SHUTDOWN','RESTART','CLEAN_ACCOUNTS') NOT NULL,
  `trigger_type` enum('FIXED_IN_TIME','AFTER_RESTART') NOT NULL,
  `exec_param` text,
  `trigger_param` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ae_loginserver'
--

--
-- Dumping routines for database 'ae_loginserver'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-22 19:42:17
