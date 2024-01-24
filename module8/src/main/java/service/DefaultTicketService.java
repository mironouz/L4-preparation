package service;

import com.google.common.base.Preconditions;
import dao.*;
import model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultTicketService implements TicketService {
    private final TicketDao dao;
    private final EventService eventService;
    private final UserService userService;

    public static TicketService initiate(Store store, IdGenerator idGenerator) {
        return new DefaultTicketService(
                new TicketDaoImpl(store, idGenerator),
                new DefaultEventService(new EventDaoImpl(store, idGenerator)),
                new DefaultUserService(new UserDaoImpl(store, idGenerator))
        );
    }

    public static TicketService initiate(Store store, IdGenerator idGenerator, UserService userService, EventService eventService) {
        return new DefaultTicketService(
                new TicketDaoImpl(store, idGenerator),
                eventService,
                userService
        );
    }

    public DefaultTicketService(TicketDao dao, EventService eventService, UserService userService) {
        this.dao = dao;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        User user = userService.getUserById(userId);
        Preconditions.checkArgument(user != null, "Non-existent userId");

        Event event = eventService.getEventById(eventId);
        Preconditions.checkArgument(event != null, "Non-existent eventId");

        Optional<Ticket> ticketPlaceCheck = StreamSupport.stream(dao.findByEventId(event.getId()).spliterator(), false)
                .filter(ticket -> ticket.getPlace() == place)
                .findFirst();
        Preconditions.checkState(ticketPlaceCheck.isEmpty(), "Place already occupied");

        Ticket tempTicket = TicketFactory.createNonPersisted(event, user, category, place);
        return dao.save(tempTicket);
    }

    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        Preconditions.checkNotNull(user, "user cannot be null");
        Preconditions.checkArgument(pageNum > 0, "pageNum must be greater than 0");
        Preconditions.checkArgument(pageSize > 0 && pageSize <= 100, "pageSize must be between 1 and 100");

        int skipCount = (pageNum - 1) * pageSize;

        // TODO implement "Tickets should be sorted by event date in descending order."

        return StreamSupport.stream(dao.findByUserId(user.getId()).spliterator(), false)
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        Preconditions.checkNotNull(event, "event cannot be null");
        Preconditions.checkArgument(pageNum > 0, "pageNum must be greater than 0");
        Preconditions.checkArgument(pageSize > 0 && pageSize <= 100, "pageSize must be between 1 and 100");

        int skipCount = (pageNum - 1) * pageSize;

        // TODO implement "Tickets should be sorted in by user email in ascending order."

        return StreamSupport.stream(dao.findByEventId(event.getId()).spliterator(), false)
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public boolean cancelTicket(long ticketId) {
        Preconditions.checkArgument(ticketId > 0, "ticketId must be greater than 0");

        return dao.deleteById(PrimaryKey.ticketKey(ticketId));
    }
}
