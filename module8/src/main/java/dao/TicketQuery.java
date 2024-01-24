package dao;

import model.Ticket;

public interface TicketQuery {

    Iterable<Ticket> findByUserId(long userId);

    Iterable<Ticket> findByEventId(long eventId);
}
