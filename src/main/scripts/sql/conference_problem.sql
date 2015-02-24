select id, creationDate, quantity from sale where stockitem_id = 18556 order by creationDAte desc;
select creationdate, amount from supplierdeliveryline where stockItem_id = 18556 order by creationDAte desc;



select sum(quantity), stockItem_id from sale where creationdate between '2012-01-05 12:00:00' and '2012-01-08 23:00:00' order by stockItem_id;

CREATE TEMPORARY TABLE saleTemp AS 
(select sum(quantity) as total, stockItem_id, title from sale join stockitem on sale.stockItem_id = stockitem.id where sale
.creationdate between '2013-01-04 12:00:00' and '2013-01-06 23:00:00' and event_id = 124 group by stockItem_id order by
sum(quantity));


update bookmarks.stockitem si, saleTemp st set quantityinstock = quantityinstock - total 
	where st.stockItem_id = si.id; 


