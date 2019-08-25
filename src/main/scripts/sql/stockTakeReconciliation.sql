drop table stockSalesTemp;
drop table stockDelTemp;

create temporary table stockSalesTemp	
select si.id, sum(s.quantity) as quantity
	from stockitem si 
	join stocktakeline stl on si.id = stl.stockItem_id 
	join sale s on si.id = s.stockItem_id 
	where s.creationDate > '2013-05-10 10:28:47'
	group by si.id, stl.id;
	
create temporary table stockDelTemp	
select si.id, sum(sdl.amount) as quantity
	from stockitem si
	left join stocktakeline stl on si.id = stl.stockItem_id 
	join supplierdeliveryline sdl on si.id = sdl.stockItem_id 
	where sdl.creationDate > '2013-05-10 10:28:47'
	group by si.id, stl.id;	
	
select si.id, substring(title, 1, 20), 
	(case when stl.quantity is null then 0 else stl.quantity end) as stocktake,
	(case when sdl.quantity  is null then 0 else sdl.quantity end)  as delivered,
	(case when s.quantity  is null then 0 else s.quantity end) as sales,
	si.quantityInStock as stockRecord,
	si.quantityReadyForCustomer as readyForCust,
	stl.quantity - s.quantity + sdl.quantity - si.quantityInStock as difference
	from stockitem si
	left join stocktakeline stl on si.id = stl.stockItem_id 
	left join stockDelTemp sdl on sdl.id = si.id
	left join stockSalesTemp s on s.id = si.id
	where 
		-- Take in account readyForCustomer
		((case when stl.quantity is null then 0 else stl.quantity end)  +
		(case when sdl.quantity  is null then 0 else sdl.quantity end) -
		si.quantityReadyForCustomer -  
		(case when s.quantity  is null then 0 else s.quantity end) != si.quantityInStock)
		and
		((case when stl.quantity is null then 0 else stl.quantity end)  +
		(case when sdl.quantity  is null then 0 else sdl.quantity end) -
		(case when s.quantity  is null then 0 else s.quantity end) != si.quantityInStock)
		limit 3;


-- select * from stockDelTemp where id = 32882;

-- select max(creationDate) from stocktakeline

select id, creationDAte, sale_id from customerorderline where stockItem_id = 1168;

update customerorderline set sale_id = 