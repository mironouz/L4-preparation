package dao;

import java.util.Optional;

public interface Dao<T, ID> {

        <S extends T> S save(S entity);

        Optional<T> findById(ID primaryKey);

        Iterable<T> findAll();

        void delete(T entity);

        boolean deleteById(ID primaryKey);

        boolean existsById(ID primaryKey);
}
