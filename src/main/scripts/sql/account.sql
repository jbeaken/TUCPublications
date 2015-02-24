select 
c.id as customerId, \
concat(c.lastName, ", ", c.firstName) as customer,
case when sum(s.quantity * (1- s.discount/100) * s.sellPrice) + (i.secondHandPrice) + (i.serviceCharge) is null then 
(i.secondHandPrice) + (i.serviceCharge) 
else sum(s.quantity * (1- s.discount/100) * s.sellPrice) + (i.secondHandPrice) + (i.serviceCharge) end
as Total,
sum((case when s.sellPrice is null then 0 else s.sellPrice end) * (case when s.vat is null then 0 else s.vat end)/ 100 ) as vat
from customer c \
right join invoice i on i.customer_id = c.id \
left join invoice_sale invs on invs.invoice_id = i.id 
left join sale s on s.id = invs.sales_id 
-- where c.id = 36314 
where 
(i.paid = false and i.isProforma = false)
and i.creationDate > '2014-03-01 00:00:00'
and i.creationDate < '2014-03-07 23:59:59'
group by c.id, c.firstName, c.lastName
order by c.lastName
into outfile 'accounts.txt'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';
