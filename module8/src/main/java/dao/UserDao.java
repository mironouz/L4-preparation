package dao;

import model.User;

public interface UserDao extends Dao<User, PrimaryKey>, UserQuery {
}
