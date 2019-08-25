-- Run to clean up mini for new sales
-- Use with caution
set foreign_key_checks = 0;
delete from customerorderline_supplierdeliveryline;
delete from supplierorder_supplierorderline;
delete from supplierorderline;
delete from customerorderline;
delete from invoice_sale;
delete from invoice_customerorderline;
delete from invoice;
delete from sale;
delete from event;
alter table invoice auto_increment = 1;
alter table sale auto_increment = 1;
set foreign_key_checks = 1;


