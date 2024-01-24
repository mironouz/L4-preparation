package service;

import config.StoreData;
import dao.IdGenerator;
import dao.InMemoryStore;
import dao.TicketDao;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultTicketServiceTest {

    private static final String DUMMY_NAME = "Dummy user";
    private static final String DUMMY_EMAIL = "dummy@email.com";
    private static final int PAGE_SIZE = 2;
    private static final String DUMMY_TITLE = "Dummy title";
    private static Date DUMMY_DATE;
    private TicketService sut;
    private TicketDao dao;
    private EventService eventService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        LocalDate ld = LocalDate.of(2023, 12, 31);
        DUMMY_DATE = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        dao = Mockito.mock(TicketDao.class);
        eventService = Mockito.mock(EventService.class);
        userService = Mockito.mock(UserService.class);

        sut = new DefaultTicketService(dao,eventService, userService);
    }

    @Nested
    @DisplayName("Testing bookTicket method")
    class TestTicketBooking {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    user,2,Dummy Name1,dummy@email.com
                                                    event,1,Dummy title1,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    event,4,Dummy title4,2023-12-31
                                                    ticket,5,BAR,2,3,6
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = DefaultTicketService.initiate(store, idGenerator);
        }

        @Test
        @DisplayName("When user and event exists")
        void testBookTicket() {
            // given
            User dummyUser = UserFactory.create(PAGE_SIZE, DUMMY_NAME, DUMMY_EMAIL);
            Event dummyEvent = EventFactory.create(1, DUMMY_TITLE, DUMMY_DATE);

            // when
            Ticket ticket = sut.bookTicket(dummyUser.getId(), dummyEvent.getId(), 4, Ticket.Category.BAR);

            // then
            assertThat(ticket)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("eventId", dummyEvent.getId())
                    .hasFieldOrPropertyWithValue("userId", dummyUser.getId())
                    .hasFieldOrPropertyWithValue("place", 4)
                    .hasFieldOrPropertyWithValue("category", Ticket.Category.BAR)
                    .extracting(Ticket::getId).isNotEqualTo(Long.MIN_VALUE);
        }

        @Test
        @DisplayName("When user does not exist")
        void testBookTicketWhenNoUserExists() {
            // given
            long nonExistingUserId = 1;
            long existingEventId = 1;

            // when

            // then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.bookTicket(nonExistingUserId, existingEventId, 4, Ticket.Category.BAR),
                    "IllegalArgumentException was expected");
        }

        @Test
        @DisplayName("When user does not exist")
        void testBookTicketWhenNoEventExists() {
            // given
            long existingUserId = PAGE_SIZE;
            long nonExistingEventId = PAGE_SIZE;

            // when

            // then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.bookTicket(existingUserId, nonExistingEventId, 4, Ticket.Category.BAR),
                    "IllegalArgumentException was expected");
        }

        @Test
        void testBookTicketWhenPlaceIsOccupied() {
            // given
            long existingUserId = PAGE_SIZE;
            long existingEventId = 3;
            int occupiedPlace = 6;

            // when

            // then
            assertThrows(IllegalStateException.class,
                    () -> sut.bookTicket(existingUserId, existingEventId, occupiedPlace, Ticket.Category.BAR),
                    "IllegalStateException was expected");
        }
    }

    @Nested
    @DisplayName("Testing getBookedTickets by events method")
    class TestGetBookedTicketsByEvent {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    user,2,Dummy Name1,dummy@email.com
                                                    event,1,Dummy title1,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    event,4,Dummy title4,2023-12-31
                                                    ticket,5,BAR,2,3,6
                                                    ticket,6,BAR,2,3,7
                                                    ticket,7,BAR,2,3,8
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());
            sut = DefaultTicketService.initiate(store, idGenerator);
        }

        @Test
        @DisplayName("Get all booked tickets for specified event")
        void testBookingRetrieval() {
            // given
            Event srcEvent = EventFactory.create(3, "Dummy event", new Date());

            // when
            List<Ticket> tickets = sut.getBookedTickets(srcEvent, PAGE_SIZE, 1);

            // then
            assertThat(tickets)
                    .as("tickets cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(PAGE_SIZE);
            // TODO extend with further assertions
            // TODO assert that all the returned tickets belong to the same input user
        }

//        @Test
//        @DisplayName("Tickets should be sorted by event date in descending order")
//        void testBookingRetrievalOrderedByDate() {
//            fail("Not yet implemented");
//        }
    }

    @Nested
    @DisplayName("Get all booked tickets for specified user")
    class TestGetBookedTicketsByUser {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    user,2,Dummy Name1,dummy@email.com
                                                    event,1,Dummy title1,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    event,4,Dummy title4,2023-12-31
                                                    ticket,5,BAR,2,3,6
                                                    ticket,6,BAR,2,3,7
                                                    ticket,7,BAR,2,3,8
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());
            sut = DefaultTicketService.initiate(store, idGenerator);
        }

        @Test
        @DisplayName("Get all booked tickets for specified user")
        void testBookingRetrieval() {
            // given
            User srcUser = UserFactory.create(2, DUMMY_NAME, DUMMY_EMAIL);

            // when
            List<Ticket> tickets = sut.getBookedTickets(srcUser, PAGE_SIZE, 1);

            // then
            assertThat(tickets)
                    .as("tickets cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(PAGE_SIZE);

            // TODO extend with further assertions
            // TODO assert that all the returned tickets belong to the same input user
        }

//        @Test
//        @DisplayName("Tickets should be sorted in by user email in ascending order.")
//        void testBookingRetrievalOrderedByUserEmail() {
//            fail("Not yet implemented");
//        }
    }

    @Nested
    @DisplayName("Test cancelTicket Method")
    class TestCancelTicket {

        @BeforeEach
        void setUp() {

            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    user,2,Dummy Name1,dummy@email.com
                                                    event,1,Dummy title1,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    event,4,Dummy title4,2023-12-31
                                                    ticket,5,BAR,2,3,6
                                                    ticket,6,BAR,2,3,7
                                                    ticket,7,BAR,2,3,8
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());
            sut = DefaultTicketService.initiate(store, idGenerator);
        }

        @Test
        @DisplayName("When ticket exists")
        void whenEventExists() {
            // given

            // when
            boolean ticketCancelResult = sut.cancelTicket(5);

            // then
            assertThat(ticketCancelResult)
                    .as("Event should be deleted").isTrue();
        }

        @Test
        @DisplayName("When ticket does not exists")
        void whenEventDoesNotExists() {
            boolean ticketCancelResult = sut.cancelTicket(1);

            // then
            assertThat(ticketCancelResult)
                    .as("Event should not be deleted").isFalse();
        }
    }
}