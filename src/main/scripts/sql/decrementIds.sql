select c.firstname, c.lastname, s.quantity from invoice i join invoice_sale ins on i.id = ins.invoice_id join sale s on ins.sales_id = s.id join customer c on i.customer_id = c.id where i.id > 1855;

-- Invoices
set foreign_key_checks = 0;
update invoice set id = id - ? where id > ?;
update invoice_sale set invoice_id = invoice_id - ? where invoice_id > ?;
update customerorderline set invoice_id = invoice_id - ? where invoice_id > ?;
set foreign_key_checks = 1;

-- Sales
set foreign_key_checks = 0;
update sale set id = id - ? where id > ?;
update invoice_sale set sales_id = sales_id - ? where sales_id > ?;
set foreign_key_checks = 1;

alter table sale auto_increment = ??