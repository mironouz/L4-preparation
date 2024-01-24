package dao;

import model.User;

public interface UserQuery {

    User findByEmail(String email);

    Iterable<User> findByName(String name);
}
