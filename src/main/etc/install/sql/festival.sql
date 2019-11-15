-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: marxism
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.16.04.1

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
-- Table structure for table `about`
--

DROP TABLE IF EXISTS `about`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `about` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `icon` varchar(30) NOT NULL,
  `modal_image` varchar(255) DEFAULT NULL,
  `modal_text` varchar(2500) DEFAULT NULL,
  `name` varchar(30) NOT NULL,
  `text` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `booking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `accommodation_contact` varchar(255) DEFAULT NULL,
  `accommodation_needs` varchar(255) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `town` varchar(255) DEFAULT NULL,
  `children18months_to5years` int(11) DEFAULT NULL,
  `children5years_to11years` int(11) DEFAULT NULL,
  `children_under18months` int(11) DEFAULT NULL,
  `college` varchar(255) DEFAULT NULL,
  `date` datetime(6) NOT NULL,
  `discount_code` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `hear_about` varchar(255) DEFAULT NULL,
  `is_actioned` bit(1) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `other_membership` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `after_party` bit(1) DEFAULT NULL,
  `friday` int(11) DEFAULT NULL,
  `ticket_pricing` varchar(255) NOT NULL,
  `saturday` int(11) DEFAULT NULL,
  `sunday` int(11) DEFAULT NULL,
  `thursday` int(11) DEFAULT NULL,
  `ticket_type` varchar(255) NOT NULL,
  `ticket_web_price` varchar(255) DEFAULT NULL,
  `trade_union` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=923 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carousel_item`
--

DROP TABLE IF EXISTS `carousel_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carousel_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_a8gssp8bi915rjf9x7gedbwn3` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `culture_item`
--

DROP TABLE IF EXISTS `culture_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `culture_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `long_description` text NOT NULL,
  `name` varchar(30) NOT NULL,
  `short_description` varchar(255) NOT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_73qoy8cidy2l7714hnvnfr17y` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website`
--

DROP TABLE IF EXISTS `marxism_website`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `about_heading` varchar(255) DEFAULT NULL,
  `about_line2` varchar(255) DEFAULT NULL,
  `about_line3` varchar(255) DEFAULT NULL,
  `about_text` text,
  `apply_ticket_discount` bit(1) NOT NULL,
  `audio_url` varchar(255) NOT NULL,
  `contact_address1` varchar(255) DEFAULT NULL,
  `contact_address2` varchar(255) DEFAULT NULL,
  `contact_address3` varchar(255) DEFAULT NULL,
  `contact_address4` varchar(255) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  `contact_heading` varchar(255) DEFAULT NULL,
  `contact_line2` varchar(255) DEFAULT NULL,
  `contact_telephone` varchar(255) DEFAULT NULL,
  `discount_code` varchar(255) DEFAULT NULL,
  `discount_text` varchar(255) DEFAULT NULL,
  `email_subject` varchar(255) DEFAULT NULL,
  `email_text` varchar(2500) DEFAULT NULL,
  `gallery_url` varchar(255) NOT NULL,
  `is_live` bit(1) NOT NULL,
  `meeting_heading` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `pricing_day_ticketfe` int(11) NOT NULL,
  `pricing_day_tickethe` int(11) NOT NULL,
  `pricing_day_ticket_unwaged` int(11) NOT NULL,
  `pricing_day_ticket_waged` int(11) NOT NULL,
  `pricing_flexi_ticketfe` int(11) NOT NULL,
  `pricing_flexi_tickethe` int(11) NOT NULL,
  `pricing_flexi_ticket_unwaged` int(11) NOT NULL,
  `pricing_flexi_ticket_waged` int(11) NOT NULL,
  `pricing_full_eventfe` int(11) NOT NULL,
  `pricing_full_eventhe` int(11) NOT NULL,
  `pricing_full_event_unwaged` int(11) NOT NULL,
  `pricing_full_event_waged` int(11) NOT NULL,
  `pricing_heading` varchar(255) DEFAULT NULL,
  `pricing_text` text,
  `show_about` bit(1) NOT NULL,
  `show_booking` bit(1) NOT NULL,
  `show_contact` bit(1) NOT NULL,
  `show_culture_items` bit(1) NOT NULL,
  `show_discount_code` bit(1) NOT NULL,
  `show_gallery` bit(1) NOT NULL,
  `show_meeting_venues` bit(1) NOT NULL,
  `show_meetings` bit(1) NOT NULL,
  `show_pricing` bit(1) NOT NULL,
  `show_speakers` bit(1) NOT NULL,
  `show_themes` bit(1) NOT NULL,
  `show_video` bit(1) NOT NULL,
  `theme_heading` varchar(255) DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `videos_url` varchar(255) NOT NULL,
  `year` int(11) NOT NULL,
  `booking_after_party_text` varchar(255) DEFAULT NULL,
  `discount_code_amount` int(11) NOT NULL,
  `early_bird_discount_amount` int(11) NOT NULL,
  `early_bird_discount_text` varchar(500) NOT NULL,
  `show_early_bird_discount` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website_about`
--

DROP TABLE IF EXISTS `marxism_website_about`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website_about` (
  `marxism_website_id` bigint(20) NOT NULL,
  `abouts_id` bigint(20) NOT NULL,
  `about_item_index` int(11) NOT NULL,
  PRIMARY KEY (`marxism_website_id`,`about_item_index`),
  KEY `FKnqjtcps7a3if35es3th2jw322` (`abouts_id`),
  CONSTRAINT `FK241cbvt54b2mx07xysshaayf5` FOREIGN KEY (`marxism_website_id`) REFERENCES `marxism_website` (`id`),
  CONSTRAINT `FKnqjtcps7a3if35es3th2jw322` FOREIGN KEY (`abouts_id`) REFERENCES `about` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website_carousel_items`
--

DROP TABLE IF EXISTS `marxism_website_carousel_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website_carousel_items` (
  `marxism_website_id` bigint(20) NOT NULL,
  `carousel_items_id` bigint(20) NOT NULL,
  `carousel_item_index` int(11) NOT NULL,
  PRIMARY KEY (`marxism_website_id`,`carousel_item_index`),
  KEY `FKp0rn8htbkcv4d6b8srsr362me` (`carousel_items_id`),
  CONSTRAINT `FK72ucs3lbki7l7scaa3sxo4uud` FOREIGN KEY (`marxism_website_id`) REFERENCES `marxism_website` (`id`),
  CONSTRAINT `FKp0rn8htbkcv4d6b8srsr362me` FOREIGN KEY (`carousel_items_id`) REFERENCES `carousel_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website_culture_items`
--

DROP TABLE IF EXISTS `marxism_website_culture_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website_culture_items` (
  `marxism_website_id` bigint(20) NOT NULL,
  `culture_items_id` bigint(20) NOT NULL,
  `carousel_item_index` int(11) NOT NULL,
  PRIMARY KEY (`marxism_website_id`,`carousel_item_index`),
  KEY `FKmqfapf2hapa2w90jqhw33509n` (`culture_items_id`),
  CONSTRAINT `FK81i93vvgtifdpdh836itnr8f5` FOREIGN KEY (`marxism_website_id`) REFERENCES `marxism_website` (`id`),
  CONSTRAINT `FKmqfapf2hapa2w90jqhw33509n` FOREIGN KEY (`culture_items_id`) REFERENCES `culture_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website_speakers`
--

DROP TABLE IF EXISTS `marxism_website_speakers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website_speakers` (
  `marxism_website_id` bigint(20) NOT NULL,
  `speakers_id` bigint(20) NOT NULL,
  `speaker_index` int(11) NOT NULL,
  PRIMARY KEY (`marxism_website_id`,`speaker_index`),
  KEY `FKea9t3x9pggoj3taygh8hqouxg` (`speakers_id`),
  CONSTRAINT `FKea9t3x9pggoj3taygh8hqouxg` FOREIGN KEY (`speakers_id`) REFERENCES `speaker` (`id`),
  CONSTRAINT `FKepom9tlu7o0imp3k4bx4ls1oe` FOREIGN KEY (`marxism_website_id`) REFERENCES `marxism_website` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marxism_website_themes`
--

DROP TABLE IF EXISTS `marxism_website_themes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marxism_website_themes` (
  `marxism_website_id` bigint(20) NOT NULL,
  `themes_id` bigint(20) NOT NULL,
  `theme_index` int(11) NOT NULL,
  PRIMARY KEY (`marxism_website_id`,`theme_index`),
  KEY `FKn6m1cujbt2gpn64ny95yfoxhk` (`themes_id`),
  CONSTRAINT `FK62vjylcsbwut7anxoh1np7qb5` FOREIGN KEY (`marxism_website_id`) REFERENCES `marxism_website` (`id`),
  CONSTRAINT `FKn6m1cujbt2gpn64ny95yfoxhk` FOREIGN KEY (`themes_id`) REFERENCES `theme` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meeting`
--

DROP TABLE IF EXISTS `meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meeting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `day` varchar(255) NOT NULL,
  `description` text,
  `room` varchar(255) DEFAULT NULL,
  `speakers` varchar(255) DEFAULT NULL,
  `time` varchar(255) NOT NULL,
  `title` varchar(500) NOT NULL,
  `venue_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhb3uvnt33c163ktlehnhmin9s` (`venue_id`),
  CONSTRAINT `FKhb3uvnt33c163ktlehnhmin9s` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1590 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `speaker`
--

DROP TABLE IF EXISTS `speaker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `speaker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `long_description` varchar(3000) DEFAULT NULL,
  `name` varchar(30) NOT NULL,
  `short_description` varchar(1000) NOT NULL,
  `has_image` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `theme`
--

DROP TABLE IF EXISTS `theme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `theme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `long_description` text NOT NULL,
  `name` varchar(30) NOT NULL,
  `short_description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kos4rdub1av4d5wt6wocsdb7t` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `theme_meetings`
--

DROP TABLE IF EXISTS `theme_meetings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `theme_meetings` (
  `themes_id` bigint(20) NOT NULL,
  `meetings_id` bigint(20) NOT NULL,
  KEY `FK29v8cov836069ei39ubn9fl51` (`meetings_id`),
  KEY `FKmu9dwn8yoxitg3eiha89sldk9` (`themes_id`),
  CONSTRAINT `FK29v8cov836069ei39ubn9fl51` FOREIGN KEY (`meetings_id`) REFERENCES `meeting` (`id`),
  CONSTRAINT `FKmu9dwn8yoxitg3eiha89sldk9` FOREIGN KEY (`themes_id`) REFERENCES `theme` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `venue`
--

DROP TABLE IF EXISTS `venue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `venue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creator` varchar(255) DEFAULT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  `updater` varchar(255) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1070 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-15 14:06:56
