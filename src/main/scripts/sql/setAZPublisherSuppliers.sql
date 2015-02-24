-- Wiley
update az_publisher set supplier_id = 3 where name like '%princeton%'
 or name like '%blackwell%'
or name like '%chicago%'
or name like '%columbia%'
or name like '%harvard%'
or name like '%polity%'
or name like '%yale%'
or name like '%norton%'
or name like '%wiley%'
or name like '%tauris%'
 ;

 -- Turnaround
 update az_publisher set supplier_id = 2 where name like '%haymarket%'
 or name like '%turnaround%'
or name like '%pm press%'
or name like '%seven stories%'
or name like '%tradewind%'
or name like '%new internationalist%'
or name like '%autonomedia%'
or name like '%arcadia%'
or name like '%ak %'
or name like '%atlas%'
 ;

 -- Central
 update az_publisher set supplier_id = 8 where name like '%merlin%'
 or name like '%monthly review%'
or name like '%pm press%'
or name like '%five leaves%'
or name like '%l & w%'
or name like '%wishart%'
or name like '%central%'
 ;