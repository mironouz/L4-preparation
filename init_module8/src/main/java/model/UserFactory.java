package model;

public class UserFactory {

    public static User create(long id, String name, String email) {
        return new UserEntity(id, name, email);
    }

    public static User createNonPersisted(String name, String email) {
        return new UserEntity(name, email);
    }
}
