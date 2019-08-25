<<<<<<< HEAD
=======
alter table reading_list_stockitem change reading_list_id ReadingList_id bigint(20);


update temp.customerorderline set nameOnCard = null, creditCard1 = null, creditCard2 = null, creditCard3 = null, creditCard4 = null, securityCode = null, expiryYear = null, expiryMonth = null where customerOrderStatus in ('CANCELLED', 'COMPLETE');

update bookmarks.customerorderline bookmarks, temp.customerorderline temp 
set 
bookmarks.creditCard1 = temp.creditCard1, 
bookmarks.creditCard2 = temp.creditCard2, 
bookmarks.creditCard3 = temp.creditCard3, 
bookmarks.creditCard4 = temp.creditCard4, 
bookmarks.securityCode = temp.securityCode, 
bookmarks.expiryMonth = temp.expiryMonth, 
bookmarks.expiryYear = temp.expiryYear, 
bookmarks.nameOnCard = temp.nameOnCard
where bookmarks.id = temp.id;


select creditCard1, creditCard2, creditCard3, creditCard4, expiryMonth, expiryYear, securityCode from customerorderline where id = 9191;


select customerOrderStatus from customerorderline where creditCard1 is not null;


>>>>>>> 3e957da... Booting up but getting Could not obtain transaction-synchronized Session for current thread
/*
 
 
--
alter table SaleOrReturnOrderLine add column sor_id bigint(20) NOT NULL;

set foreign_key_checks = 0;
SET @maxId = (select max(id) from saleorreturn);
update saleorreturn set id = id + @maxId;
update saleorreturn_saleorreturnorderline set SaleOrReturn_id = SaleOrReturn_id + @maxId;

SET @maxId = (select max(id) from saleorreturnorderline);
update saleorreturnorderline set id = id + @maxId;
update saleorreturn_saleorreturnorderline set saleOrReturnOrderLines_id = saleOrReturnOrderLines_id + @maxId;





insert into SaleOrReturn (id,creationDate,note,returnDate,saleOrReturnStatus,customer_id,customerReference) select id,creationDate,note,returnDate,saleOrReturnStatus,customer_id,customerReference from saleorreturn;
insert into SaleOrReturnOrderLine 
	(id,creationDate,note,amount,amountRemainingWithCustomer,amountSold,sellPrice,stockItem_id,sor_id)
	select id,creationDate,note,amount,amountRemainingWithCustomer,amountSold,sellPrice,stockItem_id,0 from saleorreturnorderline;
insert into SaleOrReturn_SaleOrReturnOrderLine 
	(SaleOrReturn_id, saleOrReturnOrderLines_id)
	select SaleOrReturn_id, saleOrReturnOrderLines_id from saleorreturn_saleorreturnorderline;
	
set foreign_key_checks = 1;

update SaleOrReturnOrderLine sorl, SaleOrReturn_SaleOrReturnOrderLine jo set sorl.sor_id = jo.SaleOrReturn_id  where
sorl.id = jo.saleOrReturnOrderLines_id;

drop table SaleOrReturn_SaleOrReturnOrderLine;
drop table saleorreturn_saleorreturnorderline;
drop table saleorreturn;
drop table saleorreturnorderline;
--
 
 update stockitem set review_as_text = null, review_as_html = null where review_as_text like '%amazon%';
alter table bookmarks.stockitem drop column concatenatedAuthors;
alter table bookmarks.stockitem add column supplier_id bigint(20);
update stockitem si, publisher p set si.supplier_id = p.supplier_id where si.publisher_id = p.id;

alter table stockitem drop foreign key `FKC3028B292861248B`;
alter table stockitem drop foreign key `FK_s8hx52b3ivxnm8af7jt93mdfc`;
update stockitem set publisher_id = azPublisher_id;
drop table bookmarks.publisher;
RENAME TABLE az_publisher TO publisher;

alter table publisher add column address1 varchar(255);

alter table bookmarks.stockitem drop column azPublisher_id;
update stockitem set publisher_id = 1 where publisher_id = 0;
alter table stockitem add foreign key (publisher_id) references publisher(id);

update stockitem si, publisher p set p.supplier_id = si.supplier_id where p.id = si.publisher_id;

update publisher p set p.supplier_id = 8 where p.name like '%merlin%';



alter table bmw.customer add column payment_type varchar(20) not null;
alter table bmw.customer add column delivery_type varchar(20) not null;

update bookmarks.stockitem set azPublisher_id = 725 where publisher_id = 305;
update bookmarks.stockitem set azPublisher_id = 725 where azPublisher_id in (96,730);
update bookmarks.stockitem set postage = 0.75;
update bookmarks.stockitem set postage = 0.25 where stockItemType = 'PAMPHLET'; //asdfasdfasdf
update bookmarks.stockitem set update_title = true;

update bookmarks.stockitem set put_on_website = true, put_image_on_website = true, put_review_on_website = true where quantityInStock > 0;
update bookmarks.customer set email = null where email = '';
update bookmarks.stockitem si, bmw.stock_item bsi set bsi.publisher_id = si.azPublisher_id where bsi.id = si.id;
update bookmarks.category set is_on_website = true;

select count(*) from bmw.stock_item where review_as_text like '%amazon%';

create index type_idx on bmw.stock_item (type);

select id, firstName, lastname from customer group by email having(count(email) > 1);

select id, firstName, lastname, email from bookmarks.customer where email in (select email from customer group by email having(count(email) > 1)) order by email;


-- Regular runs

 
alter table bookmarks.stockitem add column sticky_category_idx bigint(20) null;
alter table bookmarks.stockitem add column sticky_type_idx bigint(20) null;
alter table bookmarks.stockitem add column bouncy_idx bigint(20) null;

alter table bookmarks.reading_list change column is_on_wesite is_on_website tinyint(1) null default 1;
update bookmarks.category set is_in_sidebar = true where id in (3,4,5,6,7,10);

drop table bookmarks.reading_list_stockitem;
drop table bookmarks.reading_list;
CREATE TABLE bookmarks.`reading_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `is_on_wesite` tinyint(1) DEFAULT NULL,
   is_on_sidebar tinyint(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fwsvxydyl5u3x3r2qv1owp1k3` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE bookmarks.`reading_list_stockitem` (
  `reading_list_id` bigint(20) NOT NULL,
  `stockItems_id` bigint(20) NOT NULL,
  `stockItem_idx` int(11) NOT NULL,
  PRIMARY KEY (`reading_list_id`,`stockItem_idx`),
  KEY `FK_3m6lvbyqwp1qx0ey61e8vvx2f` (`stockItems_id`),
  KEY `FK_s3myj6edxvfhrm671q1qq2asy` (`reading_list_id`),
  CONSTRAINT `FK_s3myj6edxvfhrm671q1qq2asy` FOREIGN KEY (`reading_list_id`) REFERENCES `reading_list` (`id`),
  CONSTRAINT `FK_3m6lvbyqwp1qx0ey61e8vvx2f` FOREIGN KEY (`stockItems_id`) REFERENCES `stockitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Get ready for sync
drop table stockitem_author;
drop table author;

CREATE TABLE `author` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `az_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_author_name` (`name`),
  UNIQUE KEY `UK_author_az_name` (`az_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `stockitem_author` (
  `stockitem_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`stockitem_id`,`author_id`),
  KEY `FK_p05u5o2jialverh1y53309nla` (`author_id`),
  KEY `FK_lxrk7f9fckvrwv7jrp1fcyout` (`stockitem_id`),
  CONSTRAINT `FK_lxrk7f9fckvrwv7jrp1fcyout` FOREIGN KEY (`stockitem_id`) REFERENCES `stockitem` (`id`),
  CONSTRAINT `FK_p05u5o2jialverh1y53309nla` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

update stockitem set is_synced_with_az = false;



update stockitem set category_id = 69 where concatenatedAuthors = 'Ewa Baker';
update stockitem set category_id = 69 where concatenatedAuthors = 'Redstuff';
 alter table bookmarks.stockitem drop column imageURLUnavailable;
-- update stockitem set title = replace(title, "<Jan Sale 2010> ", "") check <;
-- alter table bookmarks.stockitem add column review_as_html text null;


alter table bookmarks.stockitem add column update_availablity tinyint(1) null default true;
alter table bookmarks.stockitem add column update_sellPrice tinyint(1) null default true;
alter table bookmarks.stockitem add column update_review tinyint(1) null default true;
alter table bookmarks.stockitem add column update_image tinyint(1) null default true;
alter table bookmarks.stockitem add column update_publisher tinyint(1) null default true;
alter table bookmarks.stockitem add column update_authors tinyint(1) null default true;

-- alter table bookmarks.stockitem add column img_filename varchar(255) null;

-- update bookmarks.stockitem set put_review_on_website = false where length(review1) < 30;
update bookmarks.stockitem set img_url = null where img_url = '';

alter table bookmarks.stockitem change review1 review_as_text text null;
update stockitem set put_on_website = false where category_id in (55, 27);
 * 
 * 
 * alter table stockitem change img_url img_url_az varchar (255);
alter table stockitem add column img_url varchar (255) null;
update customerorderline col, stockitem si set col.sell_price = si.sellPrice where si.id = col.stockitem_id;
alter table stockitem change putOnWebsite put_on_website tinyint (1) not null default 0;
alter table stockitem change putImageOnWebsite put_image_on_website tinyint (1) not null default 0;
alter table stockitem change putReviewOnWebsite put_review_on_website tinyint (1) not null default 0;

alter table stockitem drop column reviewIsOnWebsite;
alter table stockitem drop column isOnWebsite;
alter table stockitem drop column imageIsOnWebsite;


/*
alter table customerorderline add column source varchar(20);

update customerorderline set customerorderstatus = 'OUT_OF_STOCK' where customerorderstatus = 'PENDING_OUT_OF_STOCK';
update customerorderline set customerorderstatus = 'IN_STOCK' where customerorderstatus = 'PENDING_INSTOCK';
update customerorderline set source = 'UNKNOWN'; 

alter table category add column is_on_wesite boolean not null default true;
alter table category add column is_in_sidebar boolean not null default false;

update category set is_on_wesite = true, is_in_sidebar = false;



update stockitem set putReviewOnWebsite = true;

alter table customerorderline add column dt varchar(20);
alter table customerorderline add column pt varchar(20);
alter table customerorderline add column sell_price decimal(19,2) not null default 0;
alter table customerorderline add column postage decimal(19,2);
update customerorderline col, stockitem si set col.sell_price = si.sellPrice where col.stockItem_id = si.id;


update customerorderline set dt = 'MAIL' where deliveryType = 0;
update customerorderline set dt = 'COLLECTION' where deliveryType = 1;
beansOl
update customerorderline set pt = 'CASH' where paymentType = 0;
update customerorderline set pt = 'PAID' where paymentType = 1;
update customerorderline set pt = 'CREDIT_CARD' where paymentType = 2;
update customerorderline set pt = 'ACCOUNT' where paymentType = 3;
update customerorderline set pt = 'INSTITUTION' where paymentType = 4;
update customerorderline set pt = 'CHEQUE' where paymentType = 5;

alter table customerorderline drop column deliveryType;
alter table customerorderline drop column paymentType;

alter table customerorderline change dt deliveryType varchar (20) ;
alter table customerorderline change pt paymentType varchar (20) ;

alter table stockitem drop column availableAtAZ;
alter table stockitem drop column isInBrochure;
alter table stockitem drop column syncedWithAZ;
alter table stockitem change imageURL img_url varchar (255);


alter table stockitem add column is_on_az boolean not null default 0;
alter table stockitem add column is_image_on_az boolean not null default 0;
alter table stockitem add column is_image_downloaded boolean not null default 0;
alter table stockitem add column is_synced_with_az boolean not null default 0;
alter table stockitem add column has_newer_edition boolean not null default 0;

update stockitem set is_image_on_az = true where img_url is not null;
update stockitem set is_image_on_az = false where img_url like '%no-img%';


update stockitem set title = replace(title, "<rec> ", "");
update stockitem set title = replace(title, "<rec>", "");
update stockitem set title = replace(title, "<Jan10sale> ", "");
update stockitem set title = replace(title, "<Jan10sale>", "");

update stockitem set img_url = replace(img_url, "_BO2,204,203,200_PIsitb-,", "");
update stockitem set img_url = replace(img_url, "_BO2,204,203,200_PIsitb--small,", "");
update stockitem set img_url = replace(img_url, "_BO2,204,203,200_PIsitb-sticker-arrow-click,", "");
update stockitem set img_url = replace(img_url, "TopRight,35,-76_AA300_SH20_OU02_.", "");
update stockitem set img_url = replace(img_url, "TopRight,12,-30_AA300_SH20_OU02_.", "");
update stockitem set img_url = replace(img_url, "._.", "");


-- update stockitem set img_url = null where img_url = 'http://g-ecx.images-amazon.com/images/G/02/misc/no-img-lg-uk.gif';

*//

 
 

