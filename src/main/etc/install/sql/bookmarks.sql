-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: bookmarks
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
-- Table structure for table `CreditNote`
--

DROP TABLE IF EXISTS `CreditNote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CreditNote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` decimal(19,2) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `date` datetime DEFAULT NULL,
  `transactionDescription` varchar(255) DEFAULT NULL,
  `transactionReference` varchar(255) DEFAULT NULL,
  `transactionType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_o4weht63frl2btsdii8ycauj7` (`customer_id`),
  CONSTRAINT `FK_o4weht63frl2btsdii8ycauj7` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38076 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReadingListStockItem`
--

DROP TABLE IF EXISTS `ReadingListStockItem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReadingListStockItem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `position` bigint(20) DEFAULT NULL,
  `readingList` tinyblob,
  `stockItem` tinyblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SaleOrReturn`
--

DROP TABLE IF EXISTS `SaleOrReturn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SaleOrReturn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `customerReference` varchar(255) DEFAULT NULL,
  `returnDate` datetime NOT NULL,
  `saleOrReturnStatus` varchar(255) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8ynvd21hm1rsqvgso7q95u451` (`customer_id`),
  CONSTRAINT `FK_8ynvd21hm1rsqvgso7q95u451` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=821 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SaleOrReturnOrderLine`
--

DROP TABLE IF EXISTS `SaleOrReturnOrderLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SaleOrReturnOrderLine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `amountRemainingWithCustomer` bigint(20) DEFAULT NULL,
  `amountSold` bigint(20) DEFAULT NULL,
  `sellPrice` decimal(19,2) NOT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `sor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_t8ycueoekpuh1th6vh7aswwc2` (`stockItem_id`),
  KEY `FK_55om3vhdnwqe3eii4u03nbwi3` (`sor_id`),
  CONSTRAINT `FK_t8ycueoekpuh1th6vh7aswwc2` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `SaleOrReturnOrderLine_ibfk_1` FOREIGN KEY (`sor_id`) REFERENCES `SaleOrReturn` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8074 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SaleOrReturn_SaleOrReturnOrderLine`
--

DROP TABLE IF EXISTS `SaleOrReturn_SaleOrReturnOrderLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SaleOrReturn_SaleOrReturnOrderLine` (
  `SaleOrReturn_id` bigint(20) NOT NULL,
  `saleOrReturnOrderLines_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_tiue7k15d2sny3f0mrmretqc1` (`saleOrReturnOrderLines_id`),
  KEY `FK_tiue7k15d2sny3f0mrmretqc1` (`saleOrReturnOrderLines_id`),
  KEY `FK_5y79a7ufr4s95bq79uieakdou` (`SaleOrReturn_id`),
  CONSTRAINT `FK_5y79a7ufr4s95bq79uieakdou` FOREIGN KEY (`SaleOrReturn_id`) REFERENCES `SaleOrReturn` (`id`),
  CONSTRAINT `FK_tiue7k15d2sny3f0mrmretqc1` FOREIGN KEY (`saleOrReturnOrderLines_id`) REFERENCES `SaleOrReturnOrderLine` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StockTakeLine`
--

DROP TABLE IF EXISTS `StockTakeLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StockTakeLine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `quantity` bigint(20) NOT NULL,
  `stockItem_id` bigint(20) DEFAULT NULL,
  `dateOfUpdate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mj29el57r01b1asgmeibxx0u7` (`stockItem_id`),
  CONSTRAINT `FK_mj29el57r01b1asgmeibxx0u7` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25373 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VTTransaction`
--

DROP TABLE IF EXISTS `VTTransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VTTransaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `primaryAccount` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `total` float DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=823 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `author` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `az_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_author_name` (`name`),
  UNIQUE KEY `UK_author_az_name` (`az_name`)
) ENGINE=InnoDB AUTO_INCREMENT=35273 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `is_on_wesite` tinyint(1) NOT NULL DEFAULT '1',
  `is_in_sidebar` tinyint(1) NOT NULL DEFAULT '0',
  `is_on_website` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name_idx` (`name`),
  UNIQUE KEY `name_idx` (`name`),
  KEY `FK6DD211E25AA5B5D` (`parent_id`),
  CONSTRAINT `FK6DD211E25AA5B5D` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_category`
--

DROP TABLE IF EXISTS `category_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_category` (
  `Category_id` bigint(20) NOT NULL,
  `children_id` bigint(20) NOT NULL,
  UNIQUE KEY `children_id` (`children_id`),
  UNIQUE KEY `UK_b3l81vr8wy5r00o9dqygy9ex3` (`children_id`),
  KEY `FK8635931F91D589E8` (`children_id`),
  KEY `FK8635931F5EC5689` (`Category_id`),
  CONSTRAINT `FK8635931F5EC5689` FOREIGN KEY (`Category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FK8635931F91D589E8` FOREIGN KEY (`children_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_children`
--

DROP TABLE IF EXISTS `category_children`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_children` (
  `category_id` bigint(20) NOT NULL,
  `children_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_98qo8cf5kyu4c5d0bcrd04cqg` (`children_id`),
  KEY `FK_5bnqb1psrttbqw5o0g9bmtid0` (`category_id`),
  CONSTRAINT `FK_5bnqb1psrttbqw5o0g9bmtid0` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_98qo8cf5kyu4c5d0bcrd04cqg` FOREIGN KEY (`children_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditnote`
--

DROP TABLE IF EXISTS `creditnote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditnote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` decimal(19,2) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK552C86AB61C2EA89` (`customer_id`),
  CONSTRAINT `FK552C86AB61C2EA89` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(2000) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `accountHolder` bit(1) DEFAULT NULL,
  `amountPaidInMonthly` decimal(19,2) DEFAULT NULL,
  `paysInMonthly` bit(1) DEFAULT NULL,
  `sponsor` bit(1) DEFAULT NULL,
  `customerType` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `firstName` varchar(55) NOT NULL,
  `joinedDate` datetime DEFAULT NULL,
  `lastName` varchar(55) NOT NULL,
  `lastRegisterdDate` datetime DEFAULT NULL,
  `leftDate` datetime DEFAULT NULL,
  `homeNumber` varchar(255) DEFAULT NULL,
  `mobileNumber` varchar(255) DEFAULT NULL,
  `workNumber` varchar(255) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `creditCard1` varchar(255) DEFAULT NULL,
  `creditCard2` varchar(255) DEFAULT NULL,
  `creditCard3` varchar(255) DEFAULT NULL,
  `creditCard4` varchar(255) DEFAULT NULL,
  `expiryMonth` varchar(255) DEFAULT NULL,
  `expiryYear` varchar(255) DEFAULT NULL,
  `securityCode` varchar(255) DEFAULT NULL,
  `currentBalance` decimal(19,3) DEFAULT NULL,
  `openingBalance` decimal(19,2) DEFAULT NULL,
  `bookmarksDiscount` decimal(19,2) DEFAULT NULL,
  `nonBookmarksDiscount` decimal(19,2) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `nameOnCard` varchar(255) DEFAULT NULL,
  `webaddress1` varchar(255) DEFAULT NULL,
  `webaddress2` varchar(255) DEFAULT NULL,
  `webaddress3` varchar(255) DEFAULT NULL,
  `webcity` varchar(255) DEFAULT NULL,
  `webcountry` varchar(255) DEFAULT NULL,
  `webpostcode` varchar(255) DEFAULT NULL,
  `comment` text,
  `tsbMatch` varchar(255) DEFAULT NULL,
  `tsbMatchSecondary` varchar(255) DEFAULT NULL,
  `sponsorshipStartDate` datetime DEFAULT NULL,
  `sponsorshipType` varchar(255) DEFAULT NULL,
  `sponsorshipComment` text,
  `sponsorshipEndDate` datetime DEFAULT NULL,
  `sponsorshipAmount` int(11) DEFAULT NULL,
  `firstPaymentDate` datetime DEFAULT NULL,
  `lastPaymentDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tsbMatch_unique` (`tsbMatch`),
  UNIQUE KEY `tsbMatchSecondary_unique` (`tsbMatchSecondary`)
) ENGINE=InnoDB AUTO_INCREMENT=51130 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer_customer_order_lines`
--

DROP TABLE IF EXISTS `customer_customer_order_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_customer_order_lines` (
  `customer_id` bigint(20) NOT NULL,
  `customer_order_lines_id` bigint(20) NOT NULL,
  PRIMARY KEY (`customer_id`,`customer_order_lines_id`),
  UNIQUE KEY `UK_8cyn6h3y945hkomh6rerac288` (`customer_order_lines_id`),
  CONSTRAINT `FK_8cyn6h3y945hkomh6rerac288` FOREIGN KEY (`customer_order_lines_id`) REFERENCES `customerorderline` (`id`),
  CONSTRAINT `FK_bdaghq6vh0ukik0y3vpg2ckto` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer_customerorderline`
--

DROP TABLE IF EXISTS `customer_customerorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_customerorderline` (
  `customer_id` bigint(20) NOT NULL,
  `CustomerOrderLines_id` bigint(20) NOT NULL,
  PRIMARY KEY (`customer_id`,`CustomerOrderLines_id`),
  UNIQUE KEY `UK_eoa9fkadgtjhfkmio1qshvo29` (`CustomerOrderLines_id`),
  KEY `FK_eoa9fkadgtjhfkmio1qshvo29` (`CustomerOrderLines_id`),
  KEY `FK_2p1oqtxt24vvgggxtoalhygxm` (`customer_id`),
  CONSTRAINT `FK_2p1oqtxt24vvgggxtoalhygxm` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_eoa9fkadgtjhfkmio1qshvo29` FOREIGN KEY (`CustomerOrderLines_id`) REFERENCES `customerorderline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customerorderline`
--

DROP TABLE IF EXISTS `customerorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customerorderline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(2000) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `completionDate` datetime DEFAULT NULL,
  `creditCard1` varchar(255) DEFAULT NULL,
  `creditCard2` varchar(255) DEFAULT NULL,
  `creditCard3` varchar(255) DEFAULT NULL,
  `creditCard4` varchar(255) DEFAULT NULL,
  `expiryMonth` varchar(255) DEFAULT NULL,
  `expiryYear` varchar(255) DEFAULT NULL,
  `securityCode` varchar(255) DEFAULT NULL,
  `customerOrderStatus` varchar(255) NOT NULL,
  `isPaid` bit(1) NOT NULL,
  `onOrderDate` datetime DEFAULT NULL,
  `receivedIntoStockDate` datetime DEFAULT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `invoice_id` bigint(20) DEFAULT NULL,
  `supplierOrderLine_id` bigint(20) DEFAULT NULL,
  `sale_id` bigint(20) DEFAULT NULL,
  `source` varchar(20) DEFAULT NULL,
  `deliveryType` varchar(20) DEFAULT NULL,
  `paymentType` varchar(20) DEFAULT NULL,
  `sell_price` decimal(19,2) NOT NULL,
  `postage` decimal(19,2) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `nameOnCard` varchar(255) DEFAULT NULL,
  `isMultipleOrder` tinyint(1) DEFAULT NULL,
  `webReference` varchar(255) DEFAULT NULL,
  `isSecondHand` tinyint(1) DEFAULT NULL,
  `havePrintedLabel` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK82A92E04A120DD4B` (`supplierOrderLine_id`),
  KEY `FK82A92E0425FEAB2B` (`invoice_id`),
  KEY `FK82A92E0461C2EA89` (`customer_id`),
  KEY `FK82A92E04A46AED8B` (`stockItem_id`),
  KEY `FK82A92E045C85C569` (`sale_id`),
  CONSTRAINT `FK82A92E0425FEAB2B` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK82A92E045C85C569` FOREIGN KEY (`sale_id`) REFERENCES `sale` (`id`),
  CONSTRAINT `FK82A92E0461C2EA89` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK82A92E04A120DD4B` FOREIGN KEY (`supplierOrderLine_id`) REFERENCES `supplierorderline` (`id`),
  CONSTRAINT `FK82A92E04A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26662 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customerorderline_supplier_delivery_lines`
--

DROP TABLE IF EXISTS `customerorderline_supplier_delivery_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customerorderline_supplier_delivery_lines` (
  `customerorderline_id` bigint(20) NOT NULL,
  `supplier_delivery_lines_id` bigint(20) NOT NULL,
  KEY `FK_sesrugrht1kth82tfwb1foc9a` (`supplier_delivery_lines_id`),
  KEY `FK_byj1maki3dw2qk0ehv1oosbbl` (`customerorderline_id`),
  CONSTRAINT `FK_byj1maki3dw2qk0ehv1oosbbl` FOREIGN KEY (`customerorderline_id`) REFERENCES `customerorderline` (`id`),
  CONSTRAINT `FK_sesrugrht1kth82tfwb1foc9a` FOREIGN KEY (`supplier_delivery_lines_id`) REFERENCES `supplierdeliveryline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customerorderline_supplierdeliveryline`
--

DROP TABLE IF EXISTS `customerorderline_supplierdeliveryline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customerorderline_supplierdeliveryline` (
  `CustomerOrderLine_id` bigint(20) NOT NULL,
  `supplierDeliveryLines_id` bigint(20) NOT NULL,
  KEY `FKABA3ED6F383F068B` (`CustomerOrderLine_id`),
  KEY `FKABA3ED6F530D491E` (`supplierDeliveryLines_id`),
  CONSTRAINT `FKABA3ED6F383F068B` FOREIGN KEY (`CustomerOrderLine_id`) REFERENCES `customerorderline` (`id`),
  CONSTRAINT `FKABA3ED6F530D491E` FOREIGN KEY (`supplierDeliveryLines_id`) REFERENCES `supplierdeliveryline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `endDate` datetime NOT NULL,
  `onWebsite` bit(1) DEFAULT NULL,
  `startDate` datetime NOT NULL,
  `stockItem_id` bigint(20) DEFAULT NULL,
  `totalSellPrice` decimal(19,2) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `review1` text,
  `review2` text,
  `review3` text,
  `description` text,
  `endTime` varchar(255) DEFAULT NULL,
  `startTime` varchar(255) DEFAULT NULL,
  `entrance_price` float DEFAULT NULL,
  `show_author` bit(1) NOT NULL,
  `show_name_not_stock_title` bit(1) DEFAULT NULL,
  `show_bookmarks_address` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK403827AA46AED8B` (`stockItem_id`),
  CONSTRAINT `FK403827AA46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=846 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(2000) DEFAULT NULL,
  `isProforma` bit(1) NOT NULL,
  `secondHandPrice` decimal(19,2) DEFAULT NULL,
  `serviceCharge` decimal(19,2) DEFAULT NULL,
  `totalPrice` decimal(19,2) NOT NULL,
  `vatPayable` decimal(19,2) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `deliveryType` int(11) NOT NULL,
  `paid` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKD80EDB0D61C2EA89` (`customer_id`),
  CONSTRAINT `FKD80EDB0D61C2EA89` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21120 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_customer_order_lines`
--

DROP TABLE IF EXISTS `invoice_customer_order_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_customer_order_lines` (
  `invoice_id` bigint(20) NOT NULL,
  `customer_order_lines_id` bigint(20) NOT NULL,
  PRIMARY KEY (`invoice_id`,`customer_order_lines_id`),
  UNIQUE KEY `UK_asm9xrax1lrheegr168lnvub0` (`customer_order_lines_id`),
  CONSTRAINT `FK_2jjoys5wc2pjd1oc1awgxvlml` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FK_asm9xrax1lrheegr168lnvub0` FOREIGN KEY (`customer_order_lines_id`) REFERENCES `customerorderline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_customerorderline`
--

DROP TABLE IF EXISTS `invoice_customerorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_customerorderline` (
  `Invoice_id` bigint(20) NOT NULL,
  `customerOrderLines_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Invoice_id`,`customerOrderLines_id`),
  UNIQUE KEY `customerOrderLines_id` (`customerOrderLines_id`),
  UNIQUE KEY `UK_gu4cr309hiohso9lr0dksi26i` (`customerOrderLines_id`),
  KEY `FKD8A1295225FEAB2B` (`Invoice_id`),
  KEY `FKD8A1295279DA3200` (`customerOrderLines_id`),
  CONSTRAINT `FKD8A1295225FEAB2B` FOREIGN KEY (`Invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FKD8A1295279DA3200` FOREIGN KEY (`customerOrderLines_id`) REFERENCES `customerorderline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_sale`
--

DROP TABLE IF EXISTS `invoice_sale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_sale` (
  `Invoice_id` bigint(20) NOT NULL,
  `sales_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Invoice_id`,`sales_id`),
  UNIQUE KEY `sales_id` (`sales_id`),
  UNIQUE KEY `UK_9wnat6ij69vv5g309glftf3du` (`sales_id`),
  KEY `FKD192F25925FEAB2B` (`Invoice_id`),
  KEY `FKD192F25960E5CA84` (`sales_id`),
  CONSTRAINT `FKD192F25925FEAB2B` FOREIGN KEY (`Invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FKD192F25960E5CA84` FOREIGN KEY (`sales_id`) REFERENCES `sale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoiceorderline`
--

DROP TABLE IF EXISTS `invoiceorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoiceorderline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `discount` decimal(19,2) NOT NULL,
  `discountedPrice` decimal(19,2) NOT NULL,
  `sellPrice` decimal(19,2) NOT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `sale_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKF349B7D55C85C569` (`sale_id`),
  KEY `FKF349B7D5A46AED8B` (`stockItem_id`),
  CONSTRAINT `FKF349B7D55C85C569` FOREIGN KEY (`sale_id`) REFERENCES `sale` (`id`),
  CONSTRAINT `FKF349B7D5A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publisher`
--

DROP TABLE IF EXISTS `publisher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `publisher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `contactName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `telephone1` varchar(20) DEFAULT NULL,
  `telephone2` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dd03xmp8dhh8nwajo3o0vjlv` (`name`),
  KEY `FK_s11e9sgjqwduuuthy07hkybgq` (`supplier_id`),
  CONSTRAINT `FK_s11e9sgjqwduuuthy07hkybgq` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4914 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_list`
--

DROP TABLE IF EXISTS `reading_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `is_on_wesite` tinyint(1) DEFAULT NULL,
  `isbn` varchar(255) DEFAULT NULL,
  `is_on_sidebar` tinyint(1) DEFAULT NULL,
  `is_on_website` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_list_stock_item`
--

DROP TABLE IF EXISTS `reading_list_stock_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list_stock_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `position` bigint(20) DEFAULT NULL,
  `reading_list` tinyblob,
  `stock_item` tinyblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_list_stock_items`
--

DROP TABLE IF EXISTS `reading_list_stock_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list_stock_items` (
  `reading_list_id` bigint(20) NOT NULL,
  `stock_items_id` bigint(20) NOT NULL,
  `stock_item_idx` int(11) NOT NULL,
  PRIMARY KEY (`reading_list_id`,`stock_item_idx`),
  KEY `FK_p6o7797xguv9cp4ti0gorjrj9` (`stock_items_id`),
  CONSTRAINT `FK_5el9dacqfrw8813xbka7v6m1m` FOREIGN KEY (`reading_list_id`) REFERENCES `reading_list` (`id`),
  CONSTRAINT `FK_p6o7797xguv9cp4ti0gorjrj9` FOREIGN KEY (`stock_items_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reading_list_stockitem`
--

DROP TABLE IF EXISTS `reading_list_stockitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reading_list_stockitem` (
  `reading_list_id` bigint(20) NOT NULL,
  `stockItems_id` bigint(20) NOT NULL,
  `stockItem_idx` int(11) NOT NULL,
  PRIMARY KEY (`reading_list_id`,`stockItem_idx`),
  KEY `FK_3m6lvbyqwp1qx0ey61e8vvx2f` (`stockItems_id`),
  KEY `FK_s3myj6edxvfhrm671q1qq2asy` (`reading_list_id`),
  CONSTRAINT `FK_3m6lvbyqwp1qx0ey61e8vvx2f` FOREIGN KEY (`stockItems_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK_s3myj6edxvfhrm671q1qq2asy` FOREIGN KEY (`reading_list_id`) REFERENCES `reading_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale`
--

DROP TABLE IF EXISTS `sale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `discount` decimal(19,2) NOT NULL,
  `quantity` bigint(20) NOT NULL,
  `sellPrice` decimal(19,2) NOT NULL,
  `vat` decimal(19,2) NOT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK273467D09FC84B` (`event_id`),
  KEY `FK273467A46AED8B` (`stockItem_id`),
  CONSTRAINT `FK273467A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK273467D09FC84B` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=334733 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_or_return`
--

DROP TABLE IF EXISTS `sale_or_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_or_return` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `customer_reference` varchar(255) DEFAULT NULL,
  `return_date` datetime NOT NULL,
  `sale_or_return_status` varchar(255) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_1hqx8b84ms91n4hh79dglbu2g` (`customer_id`),
  CONSTRAINT `FK_1hqx8b84ms91n4hh79dglbu2g` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_or_return_order_line`
--

DROP TABLE IF EXISTS `sale_or_return_order_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_or_return_order_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `amount_remaining_with_customer` bigint(20) DEFAULT NULL,
  `amount_sold` bigint(20) DEFAULT NULL,
  `sell_price` decimal(19,2) NOT NULL,
  `stock_item_id` bigint(20) NOT NULL,
  `sor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fnyfl0f2xugdyu9vasq2j7mk` (`stock_item_id`),
  KEY `FK_1sv3ptsl1tjcsw7titw1xysa7` (`sor_id`),
  CONSTRAINT `FK_1sv3ptsl1tjcsw7titw1xysa7` FOREIGN KEY (`sor_id`) REFERENCES `sale_or_return` (`id`),
  CONSTRAINT `FK_fnyfl0f2xugdyu9vasq2j7mk` FOREIGN KEY (`stock_item_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `slackHandle` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stock_take_line`
--

DROP TABLE IF EXISTS `stock_take_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_take_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `date_of_update` datetime DEFAULT NULL,
  `quantity` bigint(20) NOT NULL,
  `stock_item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3ty762eqxh060nuycafmadnj` (`stock_item_id`),
  CONSTRAINT `FK_3ty762eqxh060nuycafmadnj` FOREIGN KEY (`stock_item_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stockitem`
--

DROP TABLE IF EXISTS `stockitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockitem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `availability` varchar(255) NOT NULL,
  `azCurrency` varchar(255) DEFAULT NULL,
  `azPrice` varchar(255) DEFAULT NULL,
  `height` decimal(19,2) DEFAULT NULL,
  `heigthUnit` int(11) DEFAULT NULL,
  `largeImageURL` varchar(255) DEFAULT NULL,
  `length` decimal(19,2) DEFAULT NULL,
  `lengthUnit` int(11) DEFAULT NULL,
  `numberOfItems` int(11) DEFAULT NULL,
  `numberOfPages` int(11) DEFAULT NULL,
  `smallImageURL` varchar(255) DEFAULT NULL,
  `weight` decimal(19,2) DEFAULT NULL,
  `weightUnit` int(11) DEFAULT NULL,
  `width` decimal(19,2) DEFAULT NULL,
  `widthUnit` int(11) DEFAULT NULL,
  `binding` varchar(255) NOT NULL,
  `costPrice` decimal(19,2) NOT NULL,
  `discount` decimal(19,2) NOT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `isStaffPick` bit(1) NOT NULL,
  `isbn` varchar(255) NOT NULL,
  `isbnAsNumber` bigint(20) NOT NULL,
  `publishedDate` datetime DEFAULT NULL,
  `publisherPrice` decimal(19,2) NOT NULL,
  `quantityForCustomerOrder` bigint(20) NOT NULL,
  `quantityInStock` bigint(20) NOT NULL,
  `quantityOnLoan` bigint(20) NOT NULL,
  `quantityOnOrder` bigint(20) NOT NULL,
  `quantityReadyForCustomer` bigint(20) NOT NULL,
  `quantityToKeepInStock` bigint(20) NOT NULL,
  `review` longtext,
  `sellPrice` decimal(19,2) NOT NULL,
  `stockItemType` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `category_id` bigint(20) NOT NULL,
  `preferredSupplier_id` bigint(20) DEFAULT NULL,
  `publisher_id` bigint(20) NOT NULL,
  `websiteInfo_id` bigint(20) DEFAULT NULL,
  `lastReorderReviewDate` datetime NOT NULL,
  `authorOnWebsite` varchar(255) DEFAULT NULL,
  `bookOfTheMonth` bit(1) DEFAULT NULL,
  `frontPageIndex` int(11) DEFAULT NULL,
  `titleOnWebsite` varchar(255) DEFAULT NULL,
  `review_as_text` text,
  `review2` varchar(255) DEFAULT NULL,
  `review3` varchar(255) DEFAULT NULL,
  `put_image_on_website` tinyint(1) NOT NULL DEFAULT '0',
  `put_on_website` tinyint(1) NOT NULL DEFAULT '0',
  `put_review_on_website` tinyint(1) NOT NULL DEFAULT '0',
  `isForMarxism` bit(1) DEFAULT NULL,
  `quantityInStockForMarxism` bigint(20) DEFAULT NULL,
  `miniBeansStockItemId` bigint(20) DEFAULT NULL,
  `twentyThirteenSales` varchar(255) DEFAULT NULL,
  `twentyTwelveSales` varchar(255) DEFAULT NULL,
  `marxismSaleAmount` bigint(20) DEFAULT NULL,
  `marxism2012SaleAmount` bigint(20) DEFAULT NULL,
  `quantityInStockForMarxism2012` bigint(20) DEFAULT NULL,
  `keepInStockLevel` varchar(255) NOT NULL,
  `noOfPages` int(11) DEFAULT NULL,
  `dimensions` varchar(255) DEFAULT NULL,
  `originalTitle` varchar(255) NOT NULL,
  `quantityInStockPreStockTake` bigint(20) DEFAULT NULL,
  `is_on_az` tinyint(1) DEFAULT NULL,
  `is_image_on_az` tinyint(1) DEFAULT NULL,
  `is_synced_with_az` tinyint(1) NOT NULL DEFAULT '0',
  `has_newer_edition` tinyint(1) NOT NULL DEFAULT '0',
  `price_at_az` decimal(19,2) DEFAULT NULL,
  `price_third_party_collectable` decimal(19,2) DEFAULT NULL,
  `price_third_party_new` decimal(19,2) DEFAULT NULL,
  `price_third_party_second_hand` decimal(19,2) DEFAULT NULL,
  `review_as_html` text,
  `postage` decimal(19,2) DEFAULT NULL,
  `img_filename` varchar(255) DEFAULT NULL,
  `is_review_on_az` tinyint(1) DEFAULT NULL,
  `update_availablity` tinyint(1) DEFAULT '1',
  `update_sellPrice` tinyint(1) DEFAULT '1',
  `update_review` tinyint(1) DEFAULT '1',
  `update_image` tinyint(1) DEFAULT '1',
  `update_publisher` tinyint(1) DEFAULT '1',
  `update_authors` tinyint(1) DEFAULT '1',
  `bouncy_idx` bigint(20) DEFAULT NULL,
  `sticky_category_idx` bigint(20) DEFAULT NULL,
  `sticky_type_idx` bigint(20) DEFAULT NULL,
  `update_title` tinyint(1) DEFAULT NULL,
  `always_in_stock` tinyint(1) DEFAULT NULL,
  `twentyFourteenSales` varchar(255) DEFAULT NULL,
  `available_at_suppliers` tinyint(1) DEFAULT NULL,
  `gardners_stock_level` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  `quantityForMarxism` bigint(20) DEFAULT NULL,
  `is_on_extras` tinyint(1) DEFAULT NULL,
  `is_new_release` tinyint(1) DEFAULT NULL,
  `merchandise_idx` bigint(20) DEFAULT NULL,
  `ebook_alternate_url` varchar(255) DEFAULT NULL,
  `ebook_turnaround_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`),
  UNIQUE KEY `isbnAsNumber` (`isbnAsNumber`),
  KEY `FKC3028B292861248B` (`publisher_id`),
  KEY `FKC3028B295EC5689` (`category_id`),
  KEY `FKC3028B2976F644A8` (`preferredSupplier_id`),
  KEY `websiteInfo_id` (`websiteInfo_id`),
  KEY `stock_item_type_idx` (`stockItemType`),
  KEY `stockitem_type_idx` (`stockItemType`),
  KEY `bouncy_idx` (`bouncy_idx`),
  CONSTRAINT `FKC3028B295EC5689` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FKC3028B2976F644A8` FOREIGN KEY (`preferredSupplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `stockitem_ibfk_1` FOREIGN KEY (`websiteInfo_id`) REFERENCES `websiteinfo` (`id`),
  CONSTRAINT `stockitem_ibfk_2` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49171 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stockitem_author`
--

DROP TABLE IF EXISTS `stockitem_author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockitem_author` (
  `stockitem_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`stockitem_id`,`author_id`),
  KEY `FK_p05u5o2jialverh1y53309nla` (`author_id`),
  KEY `FK_lxrk7f9fckvrwv7jrp1fcyout` (`stockitem_id`),
  CONSTRAINT `FK_lxrk7f9fckvrwv7jrp1fcyout` FOREIGN KEY (`stockitem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK_p05u5o2jialverh1y53309nla` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stockitem_authors`
--

DROP TABLE IF EXISTS `stockitem_authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockitem_authors` (
  `stockitem_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`stockitem_id`,`author_id`),
  KEY `FK_o497dc0bi0pcqv9yugj1wgl5b` (`author_id`),
  CONSTRAINT `FK_872643rti8xnksbwyvf40khl2` FOREIGN KEY (`stockitem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK_o497dc0bi0pcqv9yugj1wgl5b` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stockitem_sales`
--

DROP TABLE IF EXISTS `stockitem_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stockitem_sales` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `sales` varchar(255) NOT NULL,
  `year` int(11) NOT NULL,
  `stockitem_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_381445cqfidk038wv64sis6ou` (`stockitem_id`),
  CONSTRAINT `FK_381445cqfidk038wv64sis6ou` FOREIGN KEY (`stockitem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105030 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `address1` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `address3` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `contactName` varchar(255) DEFAULT NULL,
  `defaultDiscount` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `accountNumber` varchar(255) DEFAULT NULL,
  `minimumOrderPrice` decimal(19,2) DEFAULT NULL,
  `minimumOrderQuantity` bigint(20) DEFAULT NULL,
  `vatNumber` varchar(255) DEFAULT NULL,
  `telephone1` varchar(20) DEFAULT NULL,
  `telephone2` varchar(20) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_index` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=474 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplier_supplier_invoices`
--

DROP TABLE IF EXISTS `supplier_supplier_invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier_supplier_invoices` (
  `supplier_id` bigint(20) NOT NULL,
  `supplier_invoices_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_8opwvkotnao8ncpuy8ewgjcfq` (`supplier_invoices_id`),
  KEY `FK_r570x6smdjvihmeyonwsl0ob7` (`supplier_id`),
  CONSTRAINT `FK_8opwvkotnao8ncpuy8ewgjcfq` FOREIGN KEY (`supplier_invoices_id`) REFERENCES `supplierdelivery` (`id`),
  CONSTRAINT `FK_r570x6smdjvihmeyonwsl0ob7` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplier_supplierdelivery`
--

DROP TABLE IF EXISTS `supplier_supplierdelivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier_supplierdelivery` (
  `supplier_id` bigint(20) NOT NULL,
  `supplierInvoices_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_jqkgr8g5mjnchih6ld3i4w2jn` (`supplierInvoices_id`),
  KEY `FK_ml3ovs58ticr066w2wvht5h44` (`supplier_id`),
  CONSTRAINT `FK_jqkgr8g5mjnchih6ld3i4w2jn` FOREIGN KEY (`supplierInvoices_id`) REFERENCES `supplierdelivery` (`id`),
  CONSTRAINT `FK_ml3ovs58ticr066w2wvht5h44` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierdelivery`
--

DROP TABLE IF EXISTS `supplierdelivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierdelivery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `invoiceNumber` varchar(255) NOT NULL,
  `supplier_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4B0F320026F07BC9` (`supplier_id`),
  CONSTRAINT `FK4B0F320026F07BC9` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8914 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierdelivery_supplier_delivery_line`
--

DROP TABLE IF EXISTS `supplierdelivery_supplier_delivery_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierdelivery_supplier_delivery_line` (
  `supplierdelivery_id` bigint(20) NOT NULL,
  `supplier_delivery_line_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_9y83dibvfoqnc4ib1y31rtdox` (`supplier_delivery_line_id`),
  KEY `FK_hlcdc1cntje83bithh0egpbav` (`supplierdelivery_id`),
  CONSTRAINT `FK_9y83dibvfoqnc4ib1y31rtdox` FOREIGN KEY (`supplier_delivery_line_id`) REFERENCES `supplierdeliveryline` (`id`),
  CONSTRAINT `FK_hlcdc1cntje83bithh0egpbav` FOREIGN KEY (`supplierdelivery_id`) REFERENCES `supplierdelivery` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierdelivery_supplierdeliveryline`
--

DROP TABLE IF EXISTS `supplierdelivery_supplierdeliveryline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierdelivery_supplierdeliveryline` (
  `SupplierDelivery_id` bigint(20) NOT NULL,
  `supplierDeliveryLine_id` bigint(20) NOT NULL,
  UNIQUE KEY `supplierDeliveryLine_id` (`supplierDeliveryLine_id`),
  UNIQUE KEY `UK_5gn0snqw8c87k6ogqdgxcurtl` (`supplierDeliveryLine_id`),
  KEY `FK3BD17EF320B86C69` (`SupplierDelivery_id`),
  KEY `FK3BD17EF3ACFB7C89` (`supplierDeliveryLine_id`),
  CONSTRAINT `FK3BD17EF320B86C69` FOREIGN KEY (`SupplierDelivery_id`) REFERENCES `supplierdelivery` (`id`),
  CONSTRAINT `FK3BD17EF3ACFB7C89` FOREIGN KEY (`supplierDeliveryLine_id`) REFERENCES `supplierdeliveryline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierdeliveryline`
--

DROP TABLE IF EXISTS `supplierdeliveryline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierdeliveryline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `costPrice` decimal(19,2) NOT NULL DEFAULT '0.00',
  `discount` decimal(19,2) NOT NULL DEFAULT '0.00',
  `publisherPrice` decimal(19,2) NOT NULL DEFAULT '0.00',
  `sellPrice` decimal(19,2) NOT NULL DEFAULT '0.00',
  `amountForCustomerOrders` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKEC4A5614A46AED8B` (`stockItem_id`),
  CONSTRAINT `FKEC4A5614A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48219 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierorder`
--

DROP TABLE IF EXISTS `supplierorder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierorder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `sendDate` datetime DEFAULT NULL,
  `supplierOrderStatus` varchar(255) NOT NULL,
  `supplier_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_d37h3sn31o0s2b4b0f7dyhfq8` (`supplier_id`),
  CONSTRAINT `FK_d37h3sn31o0s2b4b0f7dyhfq8` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierorder_supplier_order_lines`
--

DROP TABLE IF EXISTS `supplierorder_supplier_order_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierorder_supplier_order_lines` (
  `supplierorder_id` bigint(20) NOT NULL,
  `supplier_order_lines_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_n6iwca6k8xspg0cvxgnex50qk` (`supplier_order_lines_id`),
  KEY `FK_78whgkeao3cribhvijnxviena` (`supplierorder_id`),
  CONSTRAINT `FK_78whgkeao3cribhvijnxviena` FOREIGN KEY (`supplierorder_id`) REFERENCES `supplierorder` (`id`),
  CONSTRAINT `FK_n6iwca6k8xspg0cvxgnex50qk` FOREIGN KEY (`supplier_order_lines_id`) REFERENCES `supplierorderline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierorder_supplierorderline`
--

DROP TABLE IF EXISTS `supplierorder_supplierorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierorder_supplierorderline` (
  `supplierorder_id` bigint(20) NOT NULL,
  `supplierOrderLines_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_ltnfasya14ut0iepo1hy8bf72` (`supplierOrderLines_id`),
  KEY `FK_ltnfasya14ut0iepo1hy8bf72` (`supplierOrderLines_id`),
  KEY `FK_hwp2lorvl7xdu2vgmr274ihh3` (`supplierorder_id`),
  CONSTRAINT `FK_hwp2lorvl7xdu2vgmr274ihh3` FOREIGN KEY (`supplierorder_id`) REFERENCES `supplierorder` (`id`),
  CONSTRAINT `FK_ltnfasya14ut0iepo1hy8bf72` FOREIGN KEY (`supplierOrderLines_id`) REFERENCES `supplierorderline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierorderline`
--

DROP TABLE IF EXISTS `supplierorderline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierorderline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `supplierOrderLineStatus` varchar(255) NOT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `priority` varchar(255) NOT NULL,
  `sendDate` datetime DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `supplier_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKDEDFFD6A46AED8B` (`stockItem_id`),
  CONSTRAINT `FKDEDFFD6A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27623 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierreturn`
--

DROP TABLE IF EXISTS `supplierreturn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierreturn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `sendDate` datetime DEFAULT NULL,
  `supplier_id` bigint(20) NOT NULL,
  `number` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `dateSentToSupplier` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB9AE663C26F07BC9` (`supplier_id`),
  CONSTRAINT `FKB9AE663C26F07BC9` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=463 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierreturn_supplierreturnline`
--

DROP TABLE IF EXISTS `supplierreturn_supplierreturnline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierreturn_supplierreturnline` (
  `SupplierReturn_id` bigint(20) NOT NULL,
  `supplierReturnLines_id` bigint(20) NOT NULL,
  `supplierReturnLine_id` bigint(20) NOT NULL,
  UNIQUE KEY `supplierReturnLines_id` (`supplierReturnLines_id`),
  UNIQUE KEY `UK_6qhifqr9qg4esrhn2d4ceulkj` (`supplierReturnLine_id`),
  KEY `FK46A16B3365801756` (`supplierReturnLines_id`),
  KEY `FK46A16B3369F412A9` (`SupplierReturn_id`),
  CONSTRAINT `FK46A16B3365801756` FOREIGN KEY (`supplierReturnLines_id`) REFERENCES `supplierreturnline` (`id`),
  CONSTRAINT `FK46A16B3369F412A9` FOREIGN KEY (`SupplierReturn_id`) REFERENCES `supplierreturn` (`id`),
  CONSTRAINT `FK_6qhifqr9qg4esrhn2d4ceulkj` FOREIGN KEY (`supplierReturnLine_id`) REFERENCES `supplierreturnline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplierreturnline`
--

DROP TABLE IF EXISTS `supplierreturnline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplierreturnline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `amount` bigint(20) NOT NULL,
  `quantityAuthorised` bigint(20) DEFAULT NULL,
  `quantityRequestedAuthorisation` bigint(20) DEFAULT NULL,
  `stockItem_id` bigint(20) NOT NULL,
  `supplier_return_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKD37D0C50A46AED8B` (`stockItem_id`),
  KEY `FK_n4bafifc4bv0a8ea3grmogd6d` (`supplier_return_id`),
  CONSTRAINT `FKD37D0C50A46AED8B` FOREIGN KEY (`stockItem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK_n4bafifc4bv0a8ea3grmogd6d` FOREIGN KEY (`supplier_return_id`) REFERENCES `supplierreturn` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2453 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `websiteinfo`
--

DROP TABLE IF EXISTS `websiteinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `websiteinfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `bookOfTheMonth` bit(1) DEFAULT NULL,
  `frontPageIndex` int(11) DEFAULT NULL,
  `review1` varchar(5000) DEFAULT NULL,
  `review2` varchar(255) DEFAULT NULL,
  `review3` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-15 14:21:41
