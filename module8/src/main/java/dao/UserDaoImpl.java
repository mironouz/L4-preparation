package dao;

import com.google.common.base.Preconditions;
import model.User;
import model.UserFactory;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserDaoImpl implements UserDao {

    private final Store store;
    private final IdGenerator idGenerator;

    public UserDaoImpl(Store store, IdGenerator idGenerator) {
        this.store = store;
        this.idGenerator = idGenerator;
    }

    @Override
    public User save(User user) {
        Preconditions.checkNotNull(user, "User cannot be null");

        User tempUser = UserFactory.create(
                                        user.getId() == Long.MIN_VALUE ? idGenerator.next() : user.getId(),
                                        user.getName(),
                                        user.getEmail()
                                    );

        store.save(PrimaryKey.userKey(tempUser.getId()).id(), tempUser);

        return tempUser;
    }

    @Override
    public Optional<User> findById(PrimaryKey primaryKey) {
        Optional<Object> result = store.get(primaryKey.id());
        return result.isPresent()
                ? Optional.of((User) result.get())
                : Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        return store.getAll(Namespace.USER.prefixed())
                .stream()
                .map(x -> (User) x)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        deleteById(PrimaryKey.userKey(user.getId()));
    }

    @Override
    public boolean deleteById(PrimaryKey primaryKey) {
        return store.delete(primaryKey.id());
    }

    @Override
    public boolean existsById(PrimaryKey primaryKey) {
        return findById(primaryKey).isPresent();
    }

    @Override
    public User findByEmail(String email) {
        Preconditions.checkNotNull(email, "Email cannot be null");

        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Iterable<User> findByName(String nameSegment) {
        Preconditions.checkNotNull(nameSegment, "nameSegment cannot be null");

        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(user -> user.getName().contains(nameSegment))
                .collect(Collectors.toList());
    }
}
