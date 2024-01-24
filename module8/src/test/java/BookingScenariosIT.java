import dao.InMemoryStore;
import facade.BookingFacade;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/beans.xml")
public class BookingScenariosIT {

    @Autowired
    private ApplicationContext appCtx;

    private BookingFacade sut;
    private InMemoryStore store;

    @BeforeEach
    void setUp() {
        sut = appCtx.getBean("bookingFacade", BookingFacade.class);

        // clean up the store before every test
        store = (InMemoryStore) appCtx.getBean("store");
        store.clear();
    }

    @Test
    @DisplayName("Test full user creation flow (create->update->delete)")
    void testUserFlow() {
        // given
        String name = "Dummy user";
        String email = "dummy@email.com";
        long userId = 1L;

        assertThat(store).as("store is empty").extracting(InMemoryStore::size).isEqualTo(0);
        assertThat(sut.getUserById(userId)).as("user wasn't found").isNull();

        // when - create
        User createdUser = sut.createUser(UserFactory.create(1, name, email));

        // then - create
        assertThat(createdUser).as("user creation was successful").isNotNull();
        User queriedUser = sut.getUserById(userId);
        assertThat(queriedUser)
                .as("user is found").isNotNull()
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("email", email);
        assertThat(store).as("store has the value").extracting(InMemoryStore::size).isEqualTo(1);

        // when - update
        String testName = "Test user";
        String testEmail = "test@email.com";
        User updatedUser = sut.updateUser(UserFactory.create(userId, testName, testEmail));

        // then - update
        assertThat(updatedUser).as("user update was successful").isNotNull();
        User testUser = sut.getUserById(userId);
        assertThat(testUser)
                .as("user is found").isNotNull()
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("name", testName)
                .hasFieldOrPropertyWithValue("email", testEmail);
        assertThat(store).as("store has the value").extracting(InMemoryStore::size).isEqualTo(1);

        // when - delete
        boolean deleted = sut.deleteUser(userId);

        // then - delete
        assertThat(deleted).as("user was deleted").isTrue();
        assertThat(sut.getUserById(userId)).as("user wasn't found").isNull();
        assertThat(store).as("store is empty").extracting(InMemoryStore::size).isEqualTo(0);
    }


    @Test
    void testTicketFlow() {

        // given
        String name = "Dummy user1";
        String email = "dummy@email.com";
        long userId1 = 1L;
        long userId2 = 2L;

        long eventId1 = 1L;
        long eventId2 = 2L;
        String title = "Dummy title";
        LocalDate ld = LocalDate.of(2023, 12, 31);
        Date date1 = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date date2 = new Date();
        assertThat(store).as("store is empty").extracting(InMemoryStore::size).isEqualTo(0);

        // when
        User user1 = sut.createUser(UserFactory.create(userId1, name, email));
        User user2 = sut.createUser(UserFactory.create(userId2, name, email));

        Event event1 = sut.createEvent(EventFactory.create(eventId1, title, date1));
        Event event2 = sut.createEvent(EventFactory.create(eventId2, title, date2));

        Ticket ticket1 = sut.bookTicket(user1.getId(), event1.getId(), 4, Ticket.Category.BAR);
        assertThrows(IllegalStateException.class,
                () -> sut.bookTicket(user1.getId(), event1.getId(), 4, Ticket.Category.STANDARD),
                "IllegalStateException expected, as the place is already booked");
        Ticket ticket2 = sut.bookTicket(user1.getId(), event1.getId(), 5, Ticket.Category.STANDARD);
        Ticket ticket3 = sut.bookTicket(user1.getId(), event2.getId(), 4, Ticket.Category.PREMIUM);
        Ticket ticket4 = sut.bookTicket(user2.getId(), event2.getId(), 5, Ticket.Category.PREMIUM);
        Ticket ticket5 = sut.bookTicket(user2.getId(), event2.getId(), 6, Ticket.Category.PREMIUM);

        // then
        assertThat(user1).isNotNull();
        assertThat(user2).isNotNull();
        assertThat(event1).isNotNull();
        assertThat(event2).isNotNull();
        assertThat(ticket1).isNotNull();
        assertThat(ticket2).isNotNull();
        assertThat(ticket3).isNotNull();
        assertThat(ticket4).isNotNull();
        assertThat(ticket5).isNotNull();
        assertThat(store).extracting(InMemoryStore::size).isEqualTo(9);
        assertThat(sut.getUsersByName(name, 3, 1)).hasSize(2);
        assertThat(sut.getEventsByTitle(title, 3, 1)).hasSize(2);
        assertThat(sut.getBookedTickets(user1, 4, 1)).hasSize(3);
        assertThat(sut.getBookedTickets(event2, 4, 1)).hasSize(3);

        assertThat(sut.cancelTicket(ticket1.getId())).as("ticket1 should be cancelled").isTrue();
        assertThat(sut.cancelTicket(ticket2.getId())).as("ticket2 should be cancelled").isTrue();
        assertThat(sut.cancelTicket(ticket3.getId())).as("ticket3 should be cancelled").isTrue();
        assertThat(sut.cancelTicket(Long.MAX_VALUE)).as("Non existent ticket shouldn't be cancelled").isFalse();

        assertThat(sut.getBookedTickets(user1, 4, 1)).as("user1 should have no ticket reservation").isEmpty();
        assertThat(store).as("store size").extracting(InMemoryStore::size).isEqualTo(6);

        assertThrows(IllegalStateException.class,
                () -> sut.deleteEvent(event2.getId()),
                "IllegalStateException expected, as event2 has booked tickets");
        assertThrows(IllegalStateException.class,
                () -> sut.deleteUser(user2.getId()),
                "IllegalStateException expected, as user2 has booked tickets");

        assertThat(sut.cancelTicket(ticket4.getId())).as("ticket4 should be cancelled").isTrue();
        assertThat(sut.cancelTicket(ticket5.getId())).as("ticket5 should be cancelled").isTrue();
        assertThat(sut.deleteEvent(event2.getId())).as("event2 should be deleted").isTrue();
        assertThat(sut.deleteUser(user2.getId())).as("user2 should be deleted").isTrue();
        assertThat(store).as("store size after cancellation").extracting(InMemoryStore::size).isEqualTo(2);
    }
}
