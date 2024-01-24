package service;

import config.StoreData;
import dao.*;
import model.Event;
import model.EventFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

class DefaultEventServiceTest {

    private EventService sut;
    private EventDao dao;
    private static Date DUMMY_DATE;
    private static String DUMMY_TITLE = "Dummy title";
    private static Event DUMMY_EVENT;

    @BeforeEach
    void setUp() {
        dao = Mockito.mock(EventDao.class);

        sut = new DefaultEventService(dao);

        LocalDate ld = LocalDate.of(2023, 12, 31);
        DUMMY_DATE = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        DUMMY_EVENT = EventFactory.create(1, DUMMY_TITLE, DUMMY_DATE);
    }

    @Nested
    @DisplayName("Testing getEventById method")
    class TestGetEventById {
        @Test
        @DisplayName("When event id exists")
        void whenEventExists() {
            given(dao.findById(any()))
                    .willReturn(Optional.of(DUMMY_EVENT));

            Event actual = sut.getEventById(1);

            assertEqualEvent(DUMMY_EVENT, actual);
        }

        @Test
        @DisplayName("When Event id exists")
        void whenEventDoesNotExist() {
            given(dao.findById(any()))
                    .willReturn(Optional.empty());

            Event Event = sut.getEventById(1);

            assertThat(Event)
                    .as("Event should be null").isNull();
        }

    }

    @Nested
    @DisplayName("Testing getEventByTitle method")
    class TestGetEventByTitle {

        private final int PAGE_SIZE = 2;
        @Test
        @DisplayName("When pageSize is negative")
        void testWithNegativePageSize() {
            given(dao.findByTitle(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getEventsByTitle("Dummy title", -1, 2),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When pageNumber is non-positive")
        void testWithNonPositivePageNumber() {
            given(dao.findByTitle(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getEventsByTitle("Dummy title", PAGE_SIZE, 0),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When less than one page of events were found")
        void testGetEventsByTitleWhenOnePageOfEventsWereFound() {
            given(dao.findByTitle("Dummy title"))
                    .willReturn(
                            Arrays.asList(DUMMY_EVENT)
                    );

            List<Event> eventsFirstPage = sut.getEventsByTitle("Dummy title", PAGE_SIZE, 1);
            List<Event> eventsSecondPage = sut.getEventsByTitle("Dummy title", PAGE_SIZE, 2);

            assertThat(eventsFirstPage)
                    .as("First events page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d ", PAGE_SIZE).hasSize(1);

            assertThat(eventsSecondPage)
                    .as("Second events page cannot be null").isNotNull()
                    .as("Second events page must be empty", PAGE_SIZE).isEmpty();
        }

        @Test
        @DisplayName("When two pages of events were found")
        void testGetUsersByName() {
            // given
            given(dao.findByTitle("Dummy title"))
                    .willReturn(
                            Arrays.asList(
                                    EventFactory.create(1, "Dummy title1", DUMMY_DATE),
                                    EventFactory.create(2, "Dummy title2", DUMMY_DATE),
                                    EventFactory.create(5, "Dummy title5", DUMMY_DATE)
                            )
                    );

            // when
            List<Event> eventsFirstPage = sut.getEventsByTitle("Dummy title", PAGE_SIZE, 1);
            List<Event> eventsSecondPage = sut.getEventsByTitle("Dummy title", PAGE_SIZE, 2);
            List<Event> eventsThirdPage = sut.getEventsByTitle("Dummy title", PAGE_SIZE, 3);

            // then
            assertThat(eventsFirstPage)
                    .as("First users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(PAGE_SIZE);

            assertThat(eventsSecondPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(1);

            assertThat(eventsThirdPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("Second users page must be empty", PAGE_SIZE).isEmpty();
        }
    }

//        @Test
//        @DisplayName("Title is matched using 'contains' approach")
//        void getEventsByPartialTitleMatch() {
//            fail("Not yet implemented");
//        }

    @Nested
    @DisplayName("Testing getEventForDay method")
    class TestGetEventsByDate {

        private final int PAGE_SIZE = 2;
        @Test
        @DisplayName("When pageSize is negative")
        void testWithNegativePageSize() {
            given(dao.findByDate(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getEventsForDay(DUMMY_DATE, -1, 2),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When pageNumber is non-positive")
        void testWithNonPositivePageNumber() {
            given(dao.findByDate(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 0),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When less than one page of events were found")
        void testGetEventsByTitleWhenOnePageOfEventsWereFound() {
            // given
            given(dao.findByDate(DUMMY_DATE))
                    .willReturn(
                            Arrays.asList(DUMMY_EVENT)
                    );

            // when
            List<Event> eventsFirstPage = sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 1);
            List<Event> eventsSecondPage = sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 2);

            // then
            assertThat(eventsFirstPage)
                    .as("First events page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d ", PAGE_SIZE).hasSize(1);

            assertThat(eventsSecondPage)
                    .as("Second events page cannot be null").isNotNull()
                    .as("Second events page must be empty", PAGE_SIZE).isEmpty();
        }

        @Test
        @DisplayName("When two pages of events were found")
        void testGetUsersByName() {
            // given
            given(dao.findByDate(DUMMY_DATE))
                    .willReturn(
                            Arrays.asList(
                                    EventFactory.create(1, "Dummy title1", DUMMY_DATE),
                                    EventFactory.create(2, "Dummy title2", DUMMY_DATE),
                                    EventFactory.create(5, "Dummy title5", DUMMY_DATE)
                            )
                    );

            // when
            List<Event> eventsFirstPage = sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 1);
            List<Event> eventsSecondPage = sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 2);
            List<Event> eventsThirdPage = sut.getEventsForDay(DUMMY_DATE, PAGE_SIZE, 3);

            // then
            assertThat(eventsFirstPage)
                    .as("First users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(PAGE_SIZE);

            assertThat(eventsSecondPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(1);

            assertThat(eventsThirdPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("Second users page must be empty", PAGE_SIZE).isEmpty();
        }

//        @Test
//        @DisplayName("In case nothing was found, empty list is returned")
//        void getEventsByDateWhenNoMatch() {
//            fail("Not yet implemented");
//        }
    }


    @Nested
    @DisplayName("Testing createEvent method")
    class TestCreateEvent {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    event,1,Dummy title1,2023-12-31
                                                    event,2,Dummy title2,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultEventService(new EventDaoImpl(store, idGenerator));
        }

        @Test
        void testCreateEvent() {
            // given
            Event tempEvent = EventFactory.createNonPersisted(DUMMY_TITLE, DUMMY_DATE);

            // when
            Event createdEvent = sut.createEvent(tempEvent);

            Event queriedEvent = sut.getEventById(createdEvent.getId());

            // then
            assertThat(createdEvent)
                    .isNotNull()
                    .isNotSameAs(tempEvent)
                    .hasFieldOrPropertyWithValue("title", DUMMY_TITLE)
                    .hasFieldOrPropertyWithValue("date", DUMMY_DATE)
                    .extracting(Event::getId)
                    .as("ID must not match").isNotEqualTo(tempEvent.getId());

            assertThat(queriedEvent)
                    .isNotNull()
                    .isSameAs(createdEvent);
        }
    }

    @Nested
    @DisplayName("Testing updateEvent method")
    class TestUpdateEvent {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    event,1,Dummy title1,2023-12-31
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultEventService(new EventDaoImpl(store, idGenerator));
        }

        @Test
        @DisplayName("When existing event is updated")
        void updateEventWhenExists() {
            // given
            String dummyTitle = "Dummy name 4";
            Date dummyDate = new Date();
            Event originalEvent = EventFactory.create(1, dummyTitle, dummyDate);

            // when
            Event updatedUser = sut.updateEvent(originalEvent);

            Event queriedUser = sut.getEventById(1);

            // then
            assertThat(updatedUser)
                    .isNotNull()
                    .isNotSameAs(originalEvent)
                    .hasFieldOrPropertyWithValue("title", dummyTitle)
                    .hasFieldOrPropertyWithValue("date", dummyDate)
                    .extracting(Event::getId)
                    .as("ID must match").isEqualTo(originalEvent.getId());
        }

        @Test
        @DisplayName("When non-existing event is updated")
        void updateNonExistentUser() {
            // given
            String dummyTitle = "Dummy name 4";
            Date dummyDate = new Date();
            Event originalEvent = EventFactory.create(2, dummyTitle, dummyDate);

            // when

            // then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.updateEvent(originalEvent),
                    "IllegalArgumentException was expected");
        }
    }

    @Nested
    @DisplayName("Test Delete Event by ID Method")
    class TestDeleteEvent {

        @BeforeEach
        void setUp() {
            IdGenerator idGenerator = new IdGenerator();

            StoreData sti = StoreData.fromString("""
                                                    event,1,Dummy title1,2023-12-31
                                                    event,2,Dummy title2,2023-12-31
                                                    event,3,Dummy title3,2023-12-31
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultEventService(new EventDaoImpl(store, idGenerator));
        }

        @Test
        @DisplayName("When event exists")
        void whenEventExists() {
            // given

            // when
            Event eventToBeDeleted = sut.getEventById(1);

            boolean deleted = sut.deleteEvent(1);

            Event eventDeleted = sut.getEventById(1);

            // then
            assertThat(eventToBeDeleted)
                    .isNotNull()
                    .extracting(Event::getId).isEqualTo(1L);

            assertThat(deleted)
                    .as("Event should be deleted").isTrue();

            assertThat(eventDeleted)
                    .isNull();
        }

        @Test
        @DisplayName("When event does not exists")
        void whenEventDoesNotExists() {
            // given

            // when
            Event eventToBeDeleted = sut.getEventById(4);

            boolean deleted = sut.deleteEvent(4);

            // then
            assertThat(eventToBeDeleted)
                    .isNull();

            assertThat(deleted)
                    .as("Event should not be deleted").isFalse();
        }
    }

    private static void assertEqualEvent(Event expected, Event actual) {
        assertNotNull(actual, "Updated event cannot be null");
        assertEquals(expected.getId(), actual.getId(), "Event id must match");
        assertEquals(expected.getTitle(), actual.getTitle(), "Event title must match");
        assertEquals(expected.getDate(), actual.getDate(), "Event date must match");
    }

}