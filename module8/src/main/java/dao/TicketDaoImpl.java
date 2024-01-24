package dao;

import com.google.common.base.Preconditions;
import model.Ticket;
import model.TicketFactory;

import java.util.Optional;
import java.util.stream.Collectors;

public class TicketDaoImpl implements TicketDao {

    private final Store store;
    private final IdGenerator idGenerator;

    public TicketDaoImpl(Store store, IdGenerator idGenerator) {
        this.store = store;
        this.idGenerator = idGenerator;
    }

    @Override
    public Ticket save(Ticket ticket) {
        Preconditions.checkNotNull(ticket, "Ticket cannot be null");

        Ticket tempTicket = TicketFactory.clone(ticket);
        tempTicket.setId(Long.MIN_VALUE == ticket.getId() ? idGenerator.next() : ticket.getId());

        store.save(PrimaryKey.ticketKey(tempTicket.getId()).id(), tempTicket);

        return tempTicket;
    }

    @Override
    public Optional<Ticket> findById(PrimaryKey primaryKey) {
        Optional<Object> result = store.get(primaryKey.id());
        return result.isPresent()
                ? Optional.of((Ticket) result.get())
                : Optional.empty();

    }

    @Override
    public Iterable<Ticket> findAll() {
        return store.getAll(Namespace.TICKET.prefixed())
                .stream()
                .map(x -> (Ticket) x)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Ticket entity) {
        deleteById(PrimaryKey.ticketKey(entity.getId()));
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
    public Iterable<Ticket> findByUserId(long userId) {
        return store.getAll(Namespace.TICKET.prefixed())
                .stream()
                .map(x -> (Ticket) x)
                .filter(ticket -> ticket.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Ticket> findByEventId(long eventId) {
        return store.getAll(Namespace.TICKET.prefixed())
                .stream()
                .map(x -> (Ticket) x)
                .filter(ticket -> ticket.getEventId() == eventId)
                .collect(Collectors.toList());
    }
}
