package org.bookmarks.repository;


import org.bookmarks.domain.ReadingList;

public interface ReadingListRepository extends Repository<ReadingList>{

	ReadingList findByName(String name);

}
