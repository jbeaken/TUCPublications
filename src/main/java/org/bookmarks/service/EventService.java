package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.domain.Event;


public interface EventService extends Service<Event>{

	Collection<Event> getChipsEvents();
}
