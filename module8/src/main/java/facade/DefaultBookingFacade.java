package facade;

import model.Event;
import model.Ticket;
import model.User;
import service.EventService;
import service.TicketService;
import service.UserService;

import java.util.Date;
import java.util.List;

class DefaultBookingFacade implements BookingFacade {

    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    public DefaultBookingFacade(UserService userService, EventService eventService, TicketService ticketService) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
    }


    public Event getEventById(long id) {
        return eventService.getEventById(id);
    }

    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        return eventService.getEventsByTitle(title, pageSize, pageNum);
    }

    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        return eventService.getEventsForDay(day, pageSize, pageNum);
    }

    public Event createEvent(Event event) {
        return eventService.createEvent(event);
    }

    public Event updateEvent(Event event) {
        return eventService.updateEvent(event);
    }

    public boolean deleteEvent(long eventId) {
        checkBookedTicketsByEvent(eventService.getEventById(eventId));
        return eventService.deleteEvent(eventId);
    }

    private void checkBookedTicketsByEvent(Event event) {
        if (event != null) {
            List<Ticket> bookedTickets = ticketService.getBookedTickets(event, 1, 1);
            if(!bookedTickets.isEmpty()) {
                throw new IllegalStateException("Event (%d) has booked tickets".formatted(event.getId()));
            }
        }
    }

    public User getUserById(long userId) {
        return userService.getUserById(userId);
    }

    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        return userService.getUsersByName(name, pageSize, pageNum);
    }

    public User createUser(User user) {
        return userService.createUser(user);
    }

    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    public boolean deleteUser(long userId) {
        checkBookedTicketsByUser(userService.getUserById(userId));
        return userService.deleteUser(userId);
    }

    private void checkBookedTicketsByUser(User user) {
        if (user != null) {
            List<Ticket> bookedTickets = ticketService.getBookedTickets(user, 1, 1);
            if(!bookedTickets.isEmpty()) {
                throw new IllegalStateException("User (%d) has booked tickets".formatted(user.getId()));
            }
        }

    }

    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        return ticketService.bookTicket(userId, eventId, place, category);
    }

    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(user, pageSize, pageNum);
    }

    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(event, pageSize, pageNum);
    }

    public boolean cancelTicket(long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }
}
