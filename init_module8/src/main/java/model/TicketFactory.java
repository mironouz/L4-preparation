package model;

public class TicketFactory {

    public static Ticket create(long id, Event event, User user, Ticket.Category category, int place) {
        return new TicketEntity(id, event, user, category, place);
    }

    public static Ticket createNonPersisted(Event event, User user, Ticket.Category category, int place) {
        return new TicketEntity(event, user, category, place);
    }

    public static Ticket clone(Ticket ticket) {
        return new TicketEntity((TicketEntity) ticket);
    }


}
