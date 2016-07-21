-- Delete event sales
SET @eventId = ???;
set foreign_key_checks = 0;
delete from invoice where id in ( select Invoice_id from invoice_sale where sales_id in (select id from sale where event_id = @eventId) );
delete from invoice_sale where sales_id in (select id from sale where event_id = @eventId);
delete from sale where event_id = @eventId;
set foreign_key_checks = 1;

-- copy over stockrecord
-- Bookmarks publishers, swp, socialist review, merchandise category
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
