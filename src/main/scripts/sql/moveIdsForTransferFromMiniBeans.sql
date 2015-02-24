set foreign_key_checks = 0;
update sale set event_id = 1;
update sale set id = id - 25152;
update invoice_sale set sales_id = sales_id - 25152;
update invoice set id = id - 2373;
update invoice_sale set invoice_id = invoice_id - 2373;
set foreign_key_checks = 1;