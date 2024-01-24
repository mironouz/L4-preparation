package dao;

import com.google.common.base.Preconditions;
import model.Event;
import model.EventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventDaoImpl implements EventDao {

    private static final Logger LOG  = LoggerFactory.getLogger(EventDaoImpl.class);

    private final Store store;
    private final IdGenerator idGenerator;

    public EventDaoImpl(Store store, IdGenerator idGenerator) {
        this.store = store;
        this.idGenerator = idGenerator;
    }

    @Override
    public Event save(Event event) {
        Preconditions.checkNotNull(event, "Event cannot be null");

        Event tempEvent = EventFactory.create(
                event.getId() == Long.MIN_VALUE ? idGenerator.next() : event.getId(),
                event.getTitle(),
                event.getDate()
        );

        store.save(PrimaryKey.eventKey(tempEvent.getId()).id(), tempEvent);

        LOG.debug("Event entity saved {}", tempEvent);

        return tempEvent;
    }

    @Override
    public Optional<Event> findById(PrimaryKey primaryKey) {
        Optional<Object> result = store.get(primaryKey.id());
        return result.isPresent()
                ? Optional.of((Event) result.get())
                : Optional.empty();
    }

    @Override
    public Iterable<Event> findAll() {
        return store.getAll(Namespace.EVENT.prefixed())
                .stream()
                .map(x -> (Event) x)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Event event) {
        deleteById(PrimaryKey.eventKey(event.getId()));
    }

    @Override
    public boolean deleteById(PrimaryKey primaryKey) {
        return store.delete(primaryKey.id());
    }

    @Override
    public boolean existsById(PrimaryKey primaryKey) {
        return findById(primaryKey).isPresent();
    }

    @Override
    public Iterable<Event> findByTitle(String titleSegment) {
        Preconditions.checkNotNull(titleSegment, "titleSegment cannot be null");

        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(event -> event.getTitle().contains(titleSegment))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Event> findByDate(Date date) {
        Preconditions.checkNotNull(date, "Date cannot be null");

        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(event -> date.compareTo(event.getDate()) == 0)
                .collect(Collectors.toList());
    }
}
