-- MySQL dump 10.13  Distrib 5.5.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: bmw
-- ------------------------------------------------------
-- Server version	5.5.41-0ubuntu0.14.04.1

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
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `author` (
  `id` bigint(20) NOT NULL,
  `date_created` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_or6k6jmywerxbme223c988bmg` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `author`
--

LOCK TABLES `author` WRITE;
/*!40000 ALTER TABLE `author` DISABLE KEYS */;
/*!40000 ALTER TABLE `author` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` datetime DEFAULT NULL,
  `is_in_sidebar` bit(1) DEFAULT NULL,
  `is_on_website` bit(1) DEFAULT NULL,
  `name` varchar(30) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_81thrbnb8c08gua7tvqj7xdqk` (`parent_id`),
  CONSTRAINT `FK_81thrbnb8c08gua7tvqj7xdqk` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_categories`
--

DROP TABLE IF EXISTS `category_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_categories` (
  `category` bigint(20) NOT NULL,
  `categories` bigint(20) NOT NULL,
  PRIMARY KEY (`category`,`categories`),
  UNIQUE KEY `UK_7um9d6vh8tpm6caj3ics9jjax` (`categories`),
  CONSTRAINT `FK_s52fgdy2v2gjx1b795j9l9ua6` FOREIGN KEY (`category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_7um9d6vh8tpm6caj3ics9jjax` FOREIGN KEY (`categories`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_categories`
--

LOCK TABLES `category_categories` WRITE;
/*!40000 ALTER TABLE `category_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gplwv1k6khglovl9nsjmc0ai1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `end_date` datetime NOT NULL,
  `end_time` varchar(255) DEFAULT NULL,
  `entrance_price` float DEFAULT NULL,
  `show_author` bit(1) NOT NULL,
  `show_bookmarks_address` bit(1) NOT NULL,
  `start_date` datetime NOT NULL,
  `start_time` varchar(255) DEFAULT NULL,
  `stockitem_id` bigint(20) DEFAULT NULL,
  `show_name_not_stock_title` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mt8ulcc4k7fnc56rxaeu1sa33` (`name`),
  KEY `FK_9vomkx3y0w408eqkaptii98e9` (`stockitem_id`),
  CONSTRAINT `FK_9vomkx3y0w408eqkaptii98e9` FOREIGN KEY (`stockitem_id`) REFERENCES `stock_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reading_list`
--

DROP TABLE IF EXISTS `reading_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `is_on_sidebar` bit(1) DEFAULT NULL,
  `is_on_website` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fwsvxydyl5u3x3r2qv1owp1k3` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reading_list`
--

LOCK TABLES `reading_list` WRITE;
/*!40000 ALTER TABLE `reading_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `reading_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reading_list_stock_items`
--

DROP TABLE IF EXISTS `reading_list_stock_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list_stock_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` datetime DEFAULT NULL,
  `position` bigint(20) DEFAULT NULL,
  `readinglist_id` bigint(20) DEFAULT NULL,
  `stockitem_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6v5o4xqyfnmvvig74m5ajud30` (`readinglist_id`),
  KEY `FK_8wunn4eyqjd4ijbn70fab3a24` (`stockitem_id`),
  CONSTRAINT `FK_8wunn4eyqjd4ijbn70fab3a24` FOREIGN KEY (`stockitem_id`) REFERENCES `stock_item` (`id`),
  CONSTRAINT `FK_6v5o4xqyfnmvvig74m5ajud30` FOREIGN KEY (`readinglist_id`) REFERENCES `reading_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reading_list_stock_items`
--

LOCK TABLES `reading_list_stock_items` WRITE;
/*!40000 ALTER TABLE `reading_list_stock_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `reading_list_stock_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_item`
--

DROP TABLE IF EXISTS `stock_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_item` (
  `id` bigint(20) NOT NULL,
  `always_in_stock` bit(1) DEFAULT NULL,
  `availability` varchar(255) NOT NULL,
  `binding` varchar(255) NOT NULL,
  `bouncy_idx` bigint(20) DEFAULT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  `depth` bigint(20) DEFAULT NULL,
  `dimensions` varchar(255) DEFAULT NULL,
  `gardners_stock_level` bigint(20) DEFAULT NULL,
  `height` bigint(20) DEFAULT NULL,
  `img_filename` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `available_at_suppliers` bit(1) DEFAULT NULL,
  `isbn` varchar(255) NOT NULL,
  `isbn_as_number` bigint(20) NOT NULL,
  `merchandise_idx` bigint(20) DEFAULT NULL,
  `no_of_pages` int(11) DEFAULT NULL,
  `parent_category_id` bigint(20) DEFAULT NULL,
  `postage` decimal(19,2) NOT NULL,
  `price_at_az` decimal(19,2) DEFAULT NULL,
  `price_third_party_collectable` decimal(19,2) DEFAULT NULL,
  `price_third_party_new` decimal(19,2) DEFAULT NULL,
  `price_third_party_second_hand` decimal(19,2) DEFAULT NULL,
  `published_date` datetime DEFAULT NULL,
  `publisher_id` bigint(20) DEFAULT NULL,
  `publisher_name` varchar(255) DEFAULT NULL,
  `publisher_price` decimal(19,2) NOT NULL,
  `quantity_in_stock` bigint(20) NOT NULL,
  `review_as_html` text,
  `review_as_text` text,
  `review_short` varchar(255) DEFAULT NULL,
  `sales_last_year` int(11) NOT NULL,
  `sales_total` int(11) NOT NULL,
  `sell_price` decimal(19,2) NOT NULL,
  `sticky_category_idx` bigint(20) DEFAULT NULL,
  `sticky_type_idx` bigint(20) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `width` bigint(20) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `ebook_alternate_url` varchar(255) NULL,
  `ebook_turnaround_url` varchar(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9cw8tbx3leu3s5qtt183n1fh3` (`isbn`),
  UNIQUE KEY `UK_fnayeqhxmjoq9wlgaapwob5ii` (`isbn_as_number`),
  KEY `FK_d5yvxlbyikal47vicsume7esj` (`category_id`),
  CONSTRAINT `FK_d5yvxlbyikal47vicsume7esj` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_item`
--

LOCK TABLES `stock_item` WRITE;
/*!40000 ALTER TABLE `stock_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_item_authors`
--

DROP TABLE IF EXISTS `stock_item_authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_item_authors` (
  `stockitem_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`stockitem_id`,`author_id`),
  KEY `FK_emom8hxq3txd9muvsj1ncqlod` (`author_id`),
  CONSTRAINT `FK_my4ug7uuxjnmrjmb3rl4mvfk2` FOREIGN KEY (`stockitem_id`) REFERENCES `stock_item` (`id`),
  CONSTRAINT `FK_emom8hxq3txd9muvsj1ncqlod` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_item_authors`
--

LOCK TABLES `stock_item_authors` WRITE;
/*!40000 ALTER TABLE `stock_item_authors` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_item_authors` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-07 11:57:52
