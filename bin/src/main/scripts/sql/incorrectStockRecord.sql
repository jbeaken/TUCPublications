alter table stockitem add column amountsold bigint(20);
alter table stockitem add column amountdelivered bigint(20);

update stockitem si set amountdelivered  = (select sum(sol.amount) from SupplierDeliveryLine sol where sol.stockItem_id = si.id) where si.creationDate > '2012-02-22';

update stockitem si set amountsold  = (select sum(sa.quantity) from sale sa where sa.stockItem_id = si.id) where si.creationDate > '2012-02-22';

update stockitem set amountsold = 0 where amountsold is null and amountdelivered is not null;

update stockitem set amountdelivered = 0 where amountdelivered is null and amountsold is not null;


update stockitem set quantityinstock = amountdelivered  - amountsold where amountdelivered is not null;

alter table stockitem drop column amountsold;
alter table stockitem drop column amountdelivered;

--
select sum(sol.amount) from SupplierDeliveryLine sol where sol.stockItem_id =  31526;

select sum(sa.quantity) from sale sa where sa.stockItem_id  = 31526;

select quantityinstock from stockitem where id = 31526;