-- change as appropriate, this is used to skip the id's in the main server
-- so there are no clashes, set gap to a value which will jump over
-- see moveIdsForTransfer if the ids are not correct in mini beans sql
-- select max(id) from bookmarks.sale;
-- select max(id) from bookmarks.invoice;
set foreign_key_checks = 0;

update mb.sale set event_id = 423;

SET @maxId = (select max(id) from bookmarks.sale);
update mb.sale set id = id + @maxId;
update mb.invoice_sale set sales_id = sales_id + @maxId;

SET @maxId = (select max(id) from bookmarks.invoice);
update mb.invoice set id = id + @maxId;
update mb.invoice_sale set invoice_id = invoice_id + @maxId;

set foreign_key_checks = 1;
 
-- Transfer sales from mini beans that has been copied to temp database
insert into bookmarks.sale (id, creationDate, discount, quantity, sellPrice, vat, stockItem_id, event_id) select id, creationDate, discount, quantity, sellprice, vat, stockItem_id, event_id from mb.sale;

-- Transfer invoices from mini beans
insert into bookmarks.invoice (id, creationDate, note, isProforma, secondHandPrice, serviceCharge, totalPrice, vatPayable, customer_id, deliveryType, paid) 
	select id, creationDate, note, isProforma, secondHandPrice, serviceCharge, totalPrice, vatPayable, customer_id, deliveryType, paid from mb.invoice;
	
insert into bookmarks.invoice_sale (Invoice_id, sales_id) 
	select Invoice_id, sales_id from mb.invoice_sale;



-- Create temp table which adds up amount of stuff sold
CREATE TEMPORARY TABLE mb.saleTemp AS 
    (SELECT stockItem_id, sum(quantity) as total FROM mb.sale sa 
    	group by sa.stockItem_id);

-- Now update stock record
update bookmarks.stockitem si, mb.saleTemp st 
	set si.quantityinstock = si.quantityinstock - st.total 
	where st.stockItem_id = si.id;

drop table mb.saleTemp;


