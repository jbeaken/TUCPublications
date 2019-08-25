package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.domain.Event;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl extends AbstractService<Event> implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Override
	public Repository<Event> getRepository() {
		return eventRepository;
	}
	
	@Override
	public void save(Event event) {
		super.save(event);
	}
	
	@Override
	public void update(Event event) {
		super.update(event);
	}	
	
	@Override
	public Collection<Event> getChipsEvents() {
		return eventRepository.getChipsEvents();
	}
}
