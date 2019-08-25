-- Invoice order lines
SELECT 'Lastname', 'Firstname', 'Customer Id', 'Title', 'Quantity', 'Sell Price', 'Discount', 'VAT', 'Date'
UNION ALL
select c.lastname, c.firstname, c.id as customerId, si.title, s.quantity, s.sellPrice, s.discount, s.vat, i.creationDate
from customer c
join invoice i on i.customer_id = c.id
join invoice_sale invs on invs.invoice_id = i.id
join sale s on s.id = invs.sales_id
join stockitem si on si.id = s.stockItem_id
where i.creationDate between '2017-10-01 00:00:00' and '2018-01-31 23:59:59'
and i.paid = false
and i.isProforma = false
and c.accountHolder = true
INTO OUTFILE '/var/lib/mysql-files/invoiceorderline.csv'
FIELDS ENCLOSED BY '"'
TERMINATED BY ';'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

-- Invoice order lines
SELECT 'Lastname', 'Firstname', 'Customer Id', 'Amount', 'Date'
UNION ALL
select c.lastname, c.firstname, c.id as customerId, cn.amount, cn.creationDate
from CreditNote cn
join customer c on c.id = cn.customer_id
where cn.creationDate between '2017-10-01 00:00:00' and '2018-01-31 23:59:59'
and c.accountHolder = true
INTO OUTFILE '/var/lib/mysql-files/creditnotes.csv'
FIELDS ENCLOSED BY '"'
TERMINATED BY ','
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

-- Invoices with second hand
select c.lastname, c.firstname, c.id as customerId, i.creationDate as date, i.secondHandPrice as secHand, i.serviceCharge
from customer c
join invoice i on i.customer_id = c.id
where i.creationDate between '2017-10-01 00:00:00' and '2018-01-31 23:59:59'
and i.paid = false
i.isProforma = false
and c.accountHolder = true
INTO OUTFILE '/var/lib/mysql-files/secHand.csv'
FIELDS ENCLOSED BY '"'
TERMINATED BY ';'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';




-- Minibeans hasn't been reset, delete sales below given sale id
SET @saleId = 300041;
set foreign_key_checks = 0;
delete from invoice where id in ( select Invoice_id from invoice_sale where sales_id < @saleId );
delete from invoice_sale where sales_id < @saleId;
delete from sale where id < @saleId;
set foreign_key_checks = 1;

-- Get sales from invoices for a customer
 select id, discount from sale
 	where id in (select sales_id from invoice_sale
 		where Invoice_id in (select id from invoice where customer_id = 40635 and deliveryType = 1) );

-- update discount of account holders
update sale set discount = 10 where id in (select sales_id from invoice_sale where Invoice_id in (select id from invoice where customer_id = 40635 and deliveryType = 1) );


-- Reset reorder review for an event
update sale s, stockitem si set lastReorderReviewDate = '2016-01-01 00:00:00'
  where s.stockItem_id = si.id and s.event_id in (636, 635);

-- Delete event sales
SET @eventId = ???;
set foreign_key_checks = 0;
delete from invoice where id in ( select Invoice_id from invoice_sale where sales_id in (select id from sale where event_id = @eventId) );
delete from invoice_sale where sales_id in (select id from sale where event_id = @eventId);
delete from sale where event_id = @eventId;
set foreign_key_checks = 1;

update bookmarks.stockitem b, mb.stockitem mb
  set b.quantityInStock = mb.quantityInStock
  where b.id = mb.id and (b.publisher_id in (3769, 725, 151, 3645) or b.category_id = 69);



-- How much are on account
select sum(s.quantity * s.sellPrice) + i.secondHandPrice + i.serviceCharge from invoice i join invoice_sale invs on invs.Invoice_id = i.id join sale s on invs.sales_id = s.id where i.paid = false and s.event_id = 327;
select sum(s.quantity * s.sellPrice) from sale s  where s.event_id = 327;

-- Get all titles with <rubbish> stuff
select substring(title, 1, 5), substring(title, 6) from stockitem where substring(title, 1, 5) = '<web>';

select substring(title, 1, 5), substring(title, 2) from stockitem where substring(title, 1, 1) = '<';

update stockitem set title = substring(title, 6) where substring(title, 1, 5) = '<web>';

select substring(title, 1, 5), substring(title, 6) from stockitem where substring(title, 1, 5) = '<nyp>'


-- Get total for invoices
select c.id, concat(c.lastName, ", ", c.firstName) as customer,
case when sum(s.quantity * (1- s.discount/100) * s.sellPrice) + sum(i.secondHandPrice) + sum(i.serviceCharge) is null then sum(i.secondHandPrice) + sum(i.serviceCharge)
else sum(s.quantity * (1- s.discount/100) * s.sellPrice) + sum(i.secondHandPrice) + sum(i.serviceCharge) end
as Total
from Customer c right join Invoice i on i.customer_id = c.id left join Invoice_Sale invs on invs.invoice_id = i.id left join Sale s on s.id = invs.sales_id
-- where c.id = 36314
where i.paid = false
group by c.id, c.firstName, c.lastName
order by c.lastName;

-- DELETE SUPPLIER DELIVERY AND UPDATE STOCK RECORD (QUANTITYINSTOCK)
SET @sdlId = 2711;
-- Recon
select si.quantityInStock, si.title from stockitem si join supplierdeliveryline sdl  where si.id = sdl.stockItem_id and sdl.id in
 (select supplierDeliveryLine_id from supplierdelivery_supplierdeliveryline where SupplierDelivery_id = @sdlId);
-- Update stock record
update stockitem si, supplierdeliveryline sdl set si.quantityInStock = si.quantityInStock - sdl.amount where si.id = sdl.stockItem_id and sdl.id in
 (select supplierDeliveryLine_id from supplierdelivery_supplierdeliveryline where SupplierDelivery_id = @sdlId);
-- Delete sdl, sd, sd_sdl
set foreign_key_checks = 0;
delete from supplierdeliveryline where id in
	(select supplierDeliveryLine_id from supplierdelivery_supplierdeliveryline where SupplierDelivery_id = @sdlId);

delete from supplierdelivery_supplierdeliveryline where SupplierDelivery_id = @sdlId;

delete from supplierdelivery where id = @sdlId;
set foreign_key_checks = 1;
