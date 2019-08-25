-- Display vat paid by each customer during period set below in section equivalent to:
-- and i.creationDate > '2012-04-01 00:00:00'
-- and i.creationDate < '2012-06-30 23:59:59'
-- Remember to use the dates in this format, ie above is 1 April 2012 at midnight till 30 June 2012 at 23:59

select c.id as customerId, concat(c.lastName, ", ", c.firstName) as customer,
case when sum(s.quantity * (1- s.discount/100) * s.sellPrice) + (i.secondHandPrice) + (i.serviceCharge) is null then 
(i.secondHandPrice) + (i.serviceCharge) 
else sum(s.quantity * (1- s.discount/100) * s.sellPrice) + (i.secondHandPrice) + (i.serviceCharge) end
as GrossTotal,
case when (s.quantity * (1- s.discount/100) * s.sellPrice * s.vat / 100) is null then
0.00 else round(sum(s.quantity * (1- s.discount/100) * s.sellPrice * s.vat / 100), 2) end
as VATPaid
from Customer c right join Invoice i on i.customer_id = c.id 
left join Invoice_Sale invs on invs.invoice_id = i.id 
left join Sale s on s.id = invs.sales_id 
-- where c.id = 36314 
where 
(i.paid = false and i.isProforma = false)
and i.creationDate > '2012-04-01 00:00:00'
and i.creationDate < '2012-06-30 23:59:59'
group by c.id, c.firstName, c.lastName
order by c.lastName;