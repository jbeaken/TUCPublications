-- Politics category
-- update bookmarks.stockitem set category_id = 20 where category_id = 108 and title like '%music%';

-- update bookmarks.stockitem set category_id = 45 where category_id = 108 and title like '%philosophy%';

-- update bookmarks.stockitem set category_id = 4 where category_id = 108 and title like '%africa%';
-- update bookmarks.stockitem set category_id = 4 where category_id = 108 and title like '%nigeria%';
-- update bookmarks.stockitem set category_id = 4 where category_id = 108 and title like '%ghana%';

-- update bookmarks.stockitem set category_id = 28 where category_id = 108 and title like '%feminis%';

-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%europe%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%france%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%germany%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%portugal%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%spain%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%poland%';
-- update bookmarks.stockitem set category_id = 30 where category_id = 108 and title like '%belgium%';

-- update bookmarks.stockitem set category_id = 5 where category_id = 108 and title like '%china%';
-- update bookmarks.stockitem set category_id = 5 where category_id = 108 and title like '%india%';
-- update bookmarks.stockitem set category_id = 5 where category_id = 108 and title like '%japan%';
-- update bookmarks.stockitem set category_id = 5 where category_id = 108 and title like '%thailand%';
-- update bookmarks.stockitem set category_id = 5 where category_id = 108 and title like '%asia%';
-- update bookmarks.stockitem set azPublisher_id = 725 where publisher_id = 305;
-- update bookmarks.stockitem set azPublisher_id = 725 where azPublisher_id in (96,730);
update bookmarks.stockitem set img_url = null where img_url = 'http://g-ecx.images-amazon.com/images/G/02/misc/no-img-lg-uk.gif';

-- Remove empty ebook urls
update bookmarks.stockitem set ebook_turnaround_url = null where ebook_turnaround_url = "";

-- Mugs all have postage of 2.25 (2.15 + 1.75base = 4), and 8 pounds on website
update bookmarks.stockitem set postage = 2.15 where stockitemtype = 'MUG';

-- Zed Books Ltd
-- update bookmarks.stockitem set azPublisher_id = 145 where azPublisher_id in (select id from bookmarks.az_publisher where name like 'zed %');

-- categories
insert into bmw.category (id, name, is_on_website, is_in_sidebar, parent_id) select id, name, is_on_website, is_in_sidebar, parent_id from bookmarks.category c where c.parent_id is null;
insert into bmw.category (id, name, is_on_website, is_in_sidebar, parent_id) select id, name, is_on_website, is_in_sidebar, parent_id from bookmarks.category c where c.parent_id is not null;

-- authors
insert into bmw.author (id, name) select id, name from bookmarks.author;

-- Update postage to default if null or 0
update bookmarks.stockitem set postage = 1.05 where postage is null or postage = 0;

-- transfer stock items;
insert into bmw.stock_item (
 id,
 availability,
 binding,
 isbn,
 isbn_as_number,
 published_date,
 publisher_price,
 quantity_in_stock,
 sell_price,
 type,
 title,
 postage,
 dimensions,
 no_of_pages,
 review_as_text,
 review_as_html,
 img_url,
 img_filename,
 publisher_id,
 price_at_az,
 price_third_party_collectable,
 price_third_party_new,
 price_third_party_second_hand,
 sticky_category_idx,
 sticky_type_idx,
 bouncy_idx,
 category_id,
 gardners_stock_level,
 always_in_stock,
 available_at_suppliers,
 ebook_alternate_url,
 ebook_turnaround_url
 )
select
id,
availability,
binding,
isbn,
isbnAsNumber,
publishedDate,
publisherPrice,
quantityInStock,
sellPrice,
stockitemtype,
title,
postage,
dimensions,
noOfPages,
review_as_text,
review_as_html,
img_url,
img_filename,
publisher_id,
price_at_az,
price_third_party_collectable,
price_third_party_new,
price_third_party_second_hand,
sticky_category_idx,
sticky_type_idx,
bouncy_idx,
category_id,
gardners_stock_level,
always_in_stock,
available_at_suppliers,
ebook_alternate_url,
ebook_turnaround_url
from bookmarks.stockitem si where si.put_on_website = true;

-- Update Parent category
update bmw.stock_item si, bmw.category c set si.parent_category_id = c.parent_id where c.id = si.category_id;

-- fill in parent category
update bmw.stock_item si set si.parent_category_id = si.category_id where si.parent_category_id is null;

-- Get short review
update bmw.stock_item set review_short = substring(review_as_text, 1, 190);
update bmw.stock_item set review_short = null where review_short = '';

-- Copy over publisher name
update bookmarks.stockitem si, bmw.stock_item bsi set bsi.publisher_id = si.publisher_id where bsi.id = si.id;
update bmw.stock_item si, bookmarks.publisher p set si.publisher_name = p.name where p.id = si.publisher_id;

-- Copy over category name
update bmw.stock_item si, bookmarks.category c set si.category_name = c.name where c.id = si.category_id;

-- Is the review to be shown
update bmw.stock_item si, bookmarks.stockitem bsi set si.review_as_text = null, si.review_short = null where bsi.put_review_on_website = false and bsi.id = si.id;

-- Sales last year
update bmw.stock_item si set sales_last_year = (select sum(s.quantity) from bookmarks.sale s where stockItem_id = si.id and s.creationDate >= DATE_SUB(NOW(),INTERVAL 1 YEAR));
update bmw.stock_item si set sales_total = (select sum(s.quantity) from bookmarks.sale s where stockItem_id = si.id);

-- Images, has stock item got an image?
update  bmw.stock_item si, bookmarks.stockitem bsi set si.img_url = null where bsi.put_image_on_website = false and bsi.id = si.id;
update bmw.stock_item set img_url = null where img_url = 'http://g-ecx.images-amazon.com/images/G/02/misc/no-img-lg-uk.gif';
update bmw.stock_item set img_url = null where img_url = '';

-- Copy over stockitem authors
insert into bmw.stock_item_authors (stockitem_id, author_id) (select stockitem_id, author_id from bookmarks.stockitem_author sia join bmw.stock_item si on si.id = sia.stockitem_id);

-- Copy over stockitem categories
-- insert into bmw.stock_item_categories (stockitem_id, category_id) (select si.id, category_id from bookmarks.stockitem si join bmw.stock_item bmwsi on si.id = bmwsi.id);

-- Sort out price for 2nd hand
update bmw.stock_item si set sell_price = price_third_party_second_hand + 8 + RAND() where si.availability = 'OUT_OF_PRINT';

-- Sort out price for 3rd Party new
update bmw.stock_item si set sell_price = price_third_party_new + 8 + RAND() where si.availability = 'AVAILABLE_NEW_FROM_THIRD_PARTY';

-- Deazify
update bmw.stock_item set review_short = null where review_short like '%Read a customer review or write one%';
update bmw.stock_item set review_short = null where review_short like '%amazon%';
update bmw.stock_item set review_as_text = null, review_as_html = null where review_as_text like '%amazon%';
update bmw.stock_item set review_as_text = null, review_as_html = null where review_as_text like '%Read a customer review or write one%';

-- Price changes on website
update bmw.stock_item set sell_price = 8 where type = 'MUG';

-- READING LIST
-- Remove stockitems on reading list with put_on_website = false
delete rl.* from bookmarks.reading_list_stockitem rl left join bookmarks.stockitem si on si.id = rl.stockItems_id where put_on_website = false;

-- Only copy across reading lists on website
INSERT INTO bmw.reading_list (id, name, is_on_website, is_on_sidebar) select id, name, is_on_website, is_on_sidebar from bookmarks.reading_list brl;

INSERT INTO bmw.reading_list_stock_item (stockitem_id, readinglist_id, position)
	select rlsi.stockItems_id, rlsi.reading_list_id, rlsi.stockItem_idx from bookmarks.reading_list_stockitem rlsi
	join bookmarks.reading_list brl  on brl.id = rlsi.reading_list_id;

-- Events
update bookmarks.event set description = replace(description, '<br/>', '');
update bookmarks.event set description = replace(description, '<br />', '');
update bookmarks.event set description = replace(description, '<p>&nbsp;</p>', '');

insert into bmw.event (id, name, start_date, end_date, description, start_time, end_time, stockitem_id, show_author, entrance_price)
	select id, name, startDate, endDate, description, startTime, endTime, stockItem_id, show_author, entrance_price from bookmarks.event where onWebsite = true and startDate > now();

-- Escape apostophes
update bmw.stock_item set review_as_html = replace(review_as_html, '’', '&#39;');

-- Indexes
create index type_idx on bmw.stock_item (type);
create index parent_category_idx on bmw.stock_item (parent_category_id);
create index sales_last_year_idx on bmw.stock_item (sales_last_year);
create index quantity_in_stock_idx on bmw.stock_item (quantity_in_stock);
create index published_date_idx on bmw.stock_item (published_date);
create index publisher_id_idx on bmw.stock_item (publisher_id);
create index sticky_category_idx on bmw.stock_item (sticky_category_idx);
create index bouncy_idx on bmw.stock_item (bouncy_idx);
create index sticky_type_idx on bmw.stock_item (sticky_type_idx);
