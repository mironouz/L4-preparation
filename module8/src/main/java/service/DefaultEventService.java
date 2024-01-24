package service;

import com.google.common.base.Preconditions;
import dao.*;
import model.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultEventService implements EventService {
    public static EventService initiate(Store store, IdGenerator idGenerator) {
        return new DefaultEventService(new EventDaoImpl(store, idGenerator));
    }

    private final EventDao dao;

    public DefaultEventService(EventDao dao) {
        this.dao = Preconditions.checkNotNull(dao, "DAO object cannot be null");
    }

    @Override
    public Event getEventById(long eventId) {
        Optional<Event> event = dao.findById(PrimaryKey.eventKey(eventId));
        return event.orElse(null);
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        Preconditions.checkNotNull(title, "title cannot be null");
        Preconditions.checkArgument(pageNum > 0, "pageNum must be greater than 0");
        Preconditions.checkArgument(pageSize > 0 && pageSize <= 100, "pageSize must be between 1 and 100");

        int skipCount = (pageNum - 1) * pageSize;

        return StreamSupport.stream(dao.findByTitle(title).spliterator(), false)
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        Preconditions.checkNotNull(day, "date cannot be null");
        Preconditions.checkArgument(pageNum > 0, "pageNum must be greater than 0");
        Preconditions.checkArgument(pageSize > 0 && pageSize <= 100, "pageSize must be between 1 and 100");

        int skipCount = (pageNum - 1) * pageSize;

        return StreamSupport.stream(dao.findByDate(day).spliterator(), false)
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public Event createEvent(Event event) {
        Preconditions.checkNotNull(event, "Event cannot be null");

        return dao.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        Preconditions.checkNotNull(event, "Event cannot be null");
        Preconditions.checkArgument(dao.existsById(PrimaryKey.eventKey(event.getId())), "Event identified by id must exist is the store");

        return dao.save(event);
    }

    @Override
    public boolean deleteEvent(long eventId) {
        Preconditions.checkArgument(eventId > 0, "eventId must be greater than 0");

        return dao.deleteById(PrimaryKey.eventKey(eventId));
    }
}
