package service;

import com.google.common.base.Preconditions;
import dao.*;
import model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultUserService implements UserService {

    public static UserService initiate(Store store, IdGenerator idGenerator) {
        return new DefaultUserService(new UserDaoImpl(store, idGenerator));
    }

    private final UserDao dao;

    public DefaultUserService(UserDao dao) {
        this.dao = Preconditions.checkNotNull(dao, "DAO object cannot be null");
    }

    @Override
    public User getUserById(long userId) {
        Optional<User> user = dao.findById(PrimaryKey.userKey(userId));
        return user.orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        Preconditions.checkNotNull(email, "Email address cannot be null");

        return dao.findByEmail(email);
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkArgument(pageNum > 0, "pageNum must be greater than 0");
        Preconditions.checkArgument(pageSize > 0 && pageSize <= 100, "pageSize must be between 1 and 100");

        int skipCount = (pageNum - 1) * pageSize;

        return StreamSupport.stream(dao.findByName(name).spliterator(), false)
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) {
        Preconditions.checkNotNull(user, "User cannot be null");

        return dao.save(user);
    }

    @Override
    public User updateUser(User user) {
        Preconditions.checkNotNull(user, "User cannot be null");
        Preconditions.checkArgument(dao.existsById(PrimaryKey.userKey(user.getId())), "User identified by id must exist is the store");

        return dao.save(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        Preconditions.checkArgument(userId > 0, "userId must be greater than 0");

        return dao.deleteById(PrimaryKey.userKey(userId));
    }
}
