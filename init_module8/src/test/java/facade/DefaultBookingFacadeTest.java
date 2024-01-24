package facade;

import dao.IdGenerator;
import dao.InMemoryStore;
import dao.Store;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DefaultBookingFacadeTest {

    public static final String USER_NAME = "Dummy name";
    public static final String USER_EMAIL = "dummy@email.com";
    private BookingFacade sut;
    private Date lastDayOf2023;
    private UserService spiedUserService;
    private EventService spiedEventService;
    private TicketService spiedTicketService;

    @BeforeEach
    void setUp() {
        IdGenerator idGenerator = new IdGenerator();
        Store store = new InMemoryStore();
        spiedUserService = spy(DefaultUserService.initiate(store, idGenerator));
        spiedEventService = spy(DefaultEventService.initiate(store, idGenerator));
        spiedTicketService = spy(DefaultTicketService.initiate(store, idGenerator, spiedUserService, spiedEventService));

        sut = BookingFacadeFactory.create(spiedUserService, spiedEventService, spiedTicketService);

        LocalDate ld = LocalDate.of(2023, 12, 31);
        lastDayOf2023 = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Nested
    @DisplayName("Testing UserService facade section")
    class TestUserServiceSection {
        @Test
        @DisplayName("delegates to userService->getUserById()")
        void getUserById() {
            // given
            long userId = 1;

            // when
            sut.getUserById(userId);

            // then
            verify(spiedUserService).getUserById(userId);
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("delegates to userService->getUserByEmail()")
        void getUserByEmail() {
            // given

            // when
            User user = sut.getUserByEmail(USER_EMAIL);

            // then
            verify(spiedUserService).getUserByEmail(USER_EMAIL);
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("delegates to userService->getUsersByName()")
        void getUsersByName() {
            // given
            int pageSize = 2;
            int pageNum = 1;

            // when
            List<User> users = sut.getUsersByName(USER_NAME, pageSize, pageNum);

            // then
            verify(spiedUserService).getUsersByName(USER_NAME, pageSize, pageNum);
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("delegates to userService->createUser()")
        void createUser() {
            // given
            User toCreate = UserFactory.createNonPersisted(USER_NAME, USER_EMAIL);

            // when
            sut.createUser(toCreate);

            // then
            verify(spiedUserService).createUser(toCreate);
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("delegates to userService->updateUser()")
        void updateUser() {
            // given
            User toCreate = UserFactory.create(1, USER_NAME, USER_EMAIL);
            User toUpdate = UserFactory.create(1, "Dummy name1", "dummy1@email.com");
            sut.createUser(toCreate); // createUser is needed to avoid updateUser failure

            // when
            sut.updateUser(toUpdate);

            // then
            verify(spiedUserService).createUser(toCreate);
            verify(spiedUserService).updateUser(toUpdate);
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("delegates to userService->deleteUser()")
        void deleteUser() {
            // given

            // when
            sut.deleteUser(1);

            // then
            verify(spiedUserService).deleteUser(1);
            verify(spiedUserService).getUserById(1); // for ticket validation
            verifyNoMoreInteractions(spiedUserService);
        }

        @Test
        @DisplayName("When user has active booked tickets")
        void whenUserHasBookedTickets() {
            // given
            long userId1 = 1L;
            String name = "Dummy user1";
            String email = "dummy@email.com";

            long eventId1 = 1L;
            String title = "Dummy title";
            LocalDate ld = LocalDate.of(2023, 12, 31);
            Date date1 = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            User user = sut.createUser(UserFactory.create(userId1, name, email));
            Event event = sut.createEvent(EventFactory.create(eventId1, title, date1));
            Ticket ticket = sut.bookTicket(user.getId(), event.getId(), 4, Ticket.Category.BAR);

            // when

            // then
            assertThrows(IllegalStateException.class,
                    () -> sut.deleteUser(1),
                    "IllegalStateException expected, as user has booked tickets");
        }
    }

    @Nested
    @DisplayName("Testing EventService facade section")
    class TestEventServiceSection {
        @Test
        @DisplayName("delegates to eventService->getEventById()")
        void getEventById() {
            // given
            long eventId = 1;

            // when
            sut.getEventById(eventId);

            // then
            verify(spiedEventService).getEventById(eventId);
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("delegates to eventService->getEventByTitle()")
        void getEventsByTitle() {
            // given
            int pageSize = 2;
            int pageNum = 1;
            String title = "Dummy title";

            // when
            sut.getEventsByTitle(title, pageSize, pageNum);

            // then
            verify(spiedEventService).getEventsByTitle(title, pageSize, pageNum);
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("delegates to eventService->getEventsForDay()")
        void getEventsForDay() {
            // given
            int pageSize = 2;
            int pageNum = 1;

            // when
            sut.getEventsForDay(lastDayOf2023, pageSize, pageNum);

            // then
            verify(spiedEventService).getEventsForDay(lastDayOf2023, pageSize, pageNum);
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("delegates to eventService->createEvent()")
        void createEvent() {
            // given
            Event toCreate = EventFactory.createNonPersisted("Dummy title", lastDayOf2023);

            // when
            sut.createEvent(toCreate);

            // then
            verify(spiedEventService).createEvent(toCreate);
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("delegates to eventService->updateEvent()")
        void updateEvent() {
            // given
            Event toCreate = EventFactory.create(1, "Dummy title", lastDayOf2023);
            Event toUpdate = EventFactory.create(1, "Dummy title1", lastDayOf2023);
            sut.createEvent(toCreate); // createEvent is needed to avoid updateEvent failure

            // when
            sut.updateEvent(toUpdate);

            // then
            verify(spiedEventService).createEvent(toCreate);
            verify(spiedEventService).updateEvent(toUpdate);
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("delegates to eventService->deleteEvent()")
        void deleteEvent() {
            // given

            // when
            sut.deleteEvent(1);

            // then
            verify(spiedEventService).deleteEvent(1);
            verify(spiedEventService).getEventById(1); // for ticket validation
            verifyNoMoreInteractions(spiedEventService);
        }

        @Test
        @DisplayName("When event has active booked tickets")
        void whenEventHasBookedTickets() {
            // given
            long userId1 = 1L;
            String name = "Dummy user1";
            String email = "dummy@email.com";

            long eventId1 = 1L;
            String title = "Dummy title";
            LocalDate ld = LocalDate.of(2023, 12, 31);
            Date date1 = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            User user = sut.createUser(UserFactory.create(userId1, name, email));
            Event event = sut.createEvent(EventFactory.create(eventId1, title, date1));
            Ticket ticket = sut.bookTicket(user.getId(), event.getId(), 4, Ticket.Category.BAR);

            // when

            // then
            assertThrows(IllegalStateException.class,
                    () -> sut.deleteEvent(1),
                    "IllegalStateException expected, as event has booked tickets");
        }

    }

    @Nested
    @DisplayName("Testing TicketService facade section")
    class TestTicketServiceSection {
        @Test
        @DisplayName("delegates to ticketService->bookTicket()")
        void bookTicket() {
            // given
            User dummyUser = sut.createUser(UserFactory.createNonPersisted(USER_NAME, USER_EMAIL));
            Event dummyEvent = sut.createEvent(EventFactory.createNonPersisted("Dummy title", lastDayOf2023));

            // when
            Ticket createdTicket = sut.bookTicket(dummyUser.getId(), dummyEvent.getId(), 4, Ticket.Category.BAR);

            // then
            verify(spiedTicketService).bookTicket(dummyUser.getId(), dummyEvent.getId(), 4, Ticket.Category.BAR);
            // The userService and eventService has many invocations.
            // Seems overkill to verify each and every non-related invocation
            verifyNoMoreInteractions(spiedTicketService);
        }

        @Test
        @DisplayName("delegates to ticketService->getBookedTickets(Event)")
        void whenGetBookedTicketsByEvent() {
            // given
            Event eventToFilter = EventFactory.createNonPersisted("Dummy title", lastDayOf2023);

            // when
            sut.getBookedTickets(eventToFilter, 2, 1);

            // then
            verify(spiedTicketService).getBookedTickets(any(Event.class), eq(2), eq(1));
            // The userService and eventService has many invocations.
            // Seems overkill to verify each and every non-related invocation
            verifyNoMoreInteractions(spiedTicketService);
        }

        @Test
        @DisplayName("delegates to ticketService->getBookedTickets(User)")
        void testGetBookedTicketsByUser() {
            // given
            User userToFilter = UserFactory.create(1, USER_NAME, USER_EMAIL);

            // when
            sut.getBookedTickets(userToFilter, 2, 1);

            // then
            verify(spiedTicketService).getBookedTickets(any(User.class), eq(2), eq(1));
            // The userService and eventService has many invocations.
            // Seems overkill to verify each and every non-related invocation
            verifyNoMoreInteractions(spiedTicketService);
        }

        @Test
        @DisplayName("delegates to ticketService->cancelTicket()")
        void cancelTicket() {
            // given

            // when
            sut.cancelTicket(1);

            // then
            verify(spiedTicketService).cancelTicket(1);
            verifyNoMoreInteractions(spiedTicketService);
        }
    }
}