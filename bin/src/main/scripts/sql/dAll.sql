SET foreign_key_checks = 0;
delete from customer_customerorderline;
delete from invoice_customerorderline;
delete from invoiceorderline;
delete from customerorderline_supplierdeliveryline;
delete from supplierdelivery_supplierdeliveryline;
delete from supplierorder_supplierorderline;
delete from supplierOrderline;
delete from customerorderline;
delete from supplierorder;
delete from invoice;
delete from invoice_sale;
delete from sale;
delete from saleorreturn;
delete from saleorreturn_saleorreturnorderline;
delete from saleorreturnorderline;
delete from saleOrReturn;
SET foreign_key_checks = 1;


