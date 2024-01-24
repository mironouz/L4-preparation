package facade;

import service.EventService;
import service.TicketService;
import service.UserService;

public class BookingFacadeFactory {

    public static BookingFacade create(UserService userService, EventService eventService, TicketService ticketService) {
        return new DefaultBookingFacade(userService, eventService, ticketService);
    }

}
