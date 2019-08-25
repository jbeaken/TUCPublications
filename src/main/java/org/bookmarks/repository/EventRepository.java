package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;
import org.bookmarks.domain.Event;

public interface EventRepository extends Repository<Event>{

	Collection<Event> getChipsEvents();

}
