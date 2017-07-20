use bookmarks;
create index isbnAsNumber_index on stockItem(isbnAsNumber) using hash;