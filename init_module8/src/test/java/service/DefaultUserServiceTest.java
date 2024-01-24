package service;

import config.StoreData;
import dao.*;
import model.User;
import model.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

class DefaultUserServiceTest {

    private static final String DUMMY_EMAIL = "dummy@email.com";
    private static final String DUMMY_NAME = "Dummy name";
    private static final User DUMMY_USER = UserFactory.create(1L, DUMMY_NAME, DUMMY_EMAIL);
    private UserDao dao;
    private UserService sut;

    @BeforeEach
    void setUp() {
        dao = Mockito.mock(UserDao.class);
        sut = new DefaultUserService(dao);
    }

    @Nested
    @DisplayName("Testing getUserById method")
    class TestGetUserById {
        @Test
        @DisplayName("When user id exists")
        void whenUserExists() {
            given(dao.findById(any()))
                .willReturn(Optional.of(DUMMY_USER));

            User user = sut.getUserById(1);

            assertEqualsUserWithValues(1, DUMMY_NAME, DUMMY_EMAIL, user);
        }

        @Test
        @DisplayName("When user id exists")
        void whenUserDoesNotExist() {
            given(dao.findById(any()))
                    .willReturn(Optional.empty());

            User user = sut.getUserById(1);

            assertThat(user)
                .as("User should be null").isNull();
        }

    }

    @Nested
    @DisplayName("Testing getUserByEmail method")
    class TestGetUserByEmail {

        @Test
        @DisplayName("When called with null email")
        void whenCalledWithNullEmail() {
            given(dao.findByEmail(any())).willReturn(null);

            assertThrows(NullPointerException.class,
                    () -> sut.getUserByEmail(null),
                    "NullPointerException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When user was found by email")
        void whenUserWasFoundByEmail() {
            given(dao.findByEmail(DUMMY_EMAIL))
                    .willReturn(DUMMY_USER);

            User user = sut.getUserByEmail(DUMMY_EMAIL);

            assertEqualsUserWithValues(1L, DUMMY_NAME, DUMMY_EMAIL, user);
        }

        @Test
        @DisplayName("When no user was found by email")
        void testGetUserByEmail_whenNoEmailWasFound() {
            given(dao.findByEmail(DUMMY_EMAIL))
                    .willReturn(null);

            User user = sut.getUserByEmail(DUMMY_EMAIL);

            assertThat(user).isNull();
        }
    }

    @Nested
    @DisplayName("Testing getUsersByName method")
    class TestGetUsersByName {

        private final int PAGE_SIZE = 2;
        @Test
        @DisplayName("When pageSize is negative")
        void testWithNegativePageSize() {
            given(dao.findByName(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getUsersByName("Dummy Name", -1, 2),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When pageNumber is non-positive")
        void testWithNonPositivePageNumber() {
            given(dao.findByName(any())).willReturn(null);

            assertThrows(IllegalArgumentException.class,
                    () -> sut.getUsersByName("Dummy Name", PAGE_SIZE, 0),
                    "IllegalArgumentException was expected");

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("When less than one page of users were found")
        void testGetUsersByNameWhenOnePageOfUsersWereFound() {
            given(dao.findByName("Dummy Name"))
                .willReturn(
                    Arrays.asList(DUMMY_USER)
                );

            List<User> usersFirstPage = sut.getUsersByName("Dummy Name", PAGE_SIZE, 1);
            List<User> usersSecondPage = sut.getUsersByName("Dummy Name", PAGE_SIZE, 2);

            assertThat(usersFirstPage)
                .as("First users page cannot be null").isNotNull()
                .as("ChunkSize should less than or equal to %d ", PAGE_SIZE).hasSize(1);

            assertThat(usersSecondPage)
                .as("Second users page cannot be null").isNotNull()
                .as("Second users page must be empty", PAGE_SIZE).isEmpty();
        }

        @Test
        @DisplayName("When two pages of users were found")
        void testGetUsersByName() {
            given(dao.findByName("Dummy Name"))
                    .willReturn(
                            Arrays.asList(
                                    UserFactory.create(1, "Dummy Name1", DUMMY_EMAIL),
                                    UserFactory.create(2, "Dummy Name2", DUMMY_EMAIL),
                                    UserFactory.create(5, "Dummy Name5", DUMMY_EMAIL)
                            )
                    );

            List<User> usersFirstPage = sut.getUsersByName("Dummy Name", PAGE_SIZE, 1);
            List<User> usersSecondPage = sut.getUsersByName("Dummy Name", PAGE_SIZE, 2);
            List<User> usersThirdPage = sut.getUsersByName("Dummy Name", PAGE_SIZE, 3);

            assertThat(usersFirstPage)
                    .as("First users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(PAGE_SIZE);

            assertThat(usersSecondPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("ChunkSize should less than or equal to %d", PAGE_SIZE).hasSize(1);

            assertThat(usersThirdPage)
                    .as("Second users page cannot be null").isNotNull()
                    .as("Second users page must be empty", PAGE_SIZE).isEmpty();
        }

    }

    @Nested
    @DisplayName("Testing createUser method")
    class TestCreateUser {

        @BeforeEach
        void setUp() {
            StoreData sti = StoreData.fromString("""
                                                    user,1,Dummy Name1,dummy@email.com
                                                    user,2,Dummy Name2,dummy@email.com
                                                    user,3,Dummy Name3,dummy@email.com
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultUserService(
                    new UserDaoImpl(store, new IdGenerator())
            );
        }

        @Test
        void testCreateUser() {
            // given
            User tempUser = UserFactory.createNonPersisted(DUMMY_NAME, DUMMY_EMAIL);

            // when
            User createdUser = sut.createUser(tempUser);

            // then
            assertThat(createdUser)
                    .isNotNull()
                    .isNotSameAs(tempUser)
                    .hasFieldOrPropertyWithValue("name", DUMMY_NAME)
                    .hasFieldOrPropertyWithValue("email", DUMMY_EMAIL)
                    .extracting(User::getId)
                    .as("ID must not match").isNotEqualTo(tempUser.getId());
        }
    }

    @Nested
    @DisplayName("Testing updateUser method")
    class TestUpdateUser {

        @BeforeEach
        void setUp() {
            StoreData sti = StoreData.fromString("""
                                                    user,1,Dummy Name1,dummy@email.com
                                                    """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultUserService(
                    new UserDaoImpl(store, new IdGenerator())
            );
        }

        @Test
        @DisplayName("When existing user is updated")
        void updateUserWhenExists() {
            // given
            String dummyName = "Dummy name 4";
            String dummyEmail = "dummy4@email.com";
            User originalUser = UserFactory.create(1, dummyName, dummyEmail);

            // when
            User updatedUser = sut.updateUser(originalUser);

            User queriedUser = sut.getUserById(1);

            // then
            assertThat(updatedUser)
                    .isNotNull()
                    .isNotSameAs(originalUser)
                    .hasFieldOrPropertyWithValue("name", dummyName)
                    .hasFieldOrPropertyWithValue("email", dummyEmail)
                    .extracting(User::getId)
                    .as("ID must match").isEqualTo(originalUser.getId());
        }

        @Test
        @DisplayName("When non-existing user is updated")
        void updateNonExistentUser() {
            // given
            String dummyName = "Dummy name 4";
            String dummyEmail = "dummy4@email.com";
            User originalUser = UserFactory.create(10, dummyName, dummyEmail);

            // when

            // then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.updateUser(originalUser),
                    "IllegalArgumentException was expected");
        }
    }

    @Nested
    @DisplayName("Test Delete User by ID Method")
    class TestDeleteUser {

        @BeforeEach
        void setUp() {

            StoreData sti = StoreData.fromString("""
                                                   user,1,Dummy Name1,dummy@email.com
                                                   user,2,Dummy Name2,dummy@email.com
                                                   user,3,Dummy Name3,dummy@email.com
                                                   """);

            InMemoryStore store = new InMemoryStore(sti.load());

            sut = new DefaultUserService(
                    new UserDaoImpl(store, new IdGenerator())
            );
        }

        @Test
        @DisplayName("When user exists")
        void whenUserExists() {
            // given

            // when
            User userToBeDeleted = sut.getUserById(1);

            boolean deleted = sut.deleteUser(1);

            User userDeleted = sut.getUserById(1);

            // then
            assertThat(userToBeDeleted)
                .isNotNull()
                .extracting(User::getId).isEqualTo(1L);

            assertThat(deleted)
                .as("User should be deleted").isTrue();

            assertThat(userDeleted)
                .isNull();
        }

        @Test
        @DisplayName("When user does not exists")
        void whenUserDoesNotExists() {
            // given

            // when
            User userToBeDeleted = sut.getUserById(4);

            boolean deleted = sut.deleteUser(4);

            // then
            assertThat(userToBeDeleted)
                    .isNull();

            assertThat(deleted)
                    .as("User should not be deleted").isFalse();
        }
    }

    private static void assertEqualsUserWithValues(long expectedId, String expectedName, String expectedEmail, User actual) {
        assertNotNull(actual, "User cannot be null");
        assertEquals(expectedId, actual.getId(), "User expectedId must match");
        assertEquals(expectedName, actual.getName(), "User name must match");
        assertEquals(expectedEmail, actual.getEmail(), "User email must match");
    }
}