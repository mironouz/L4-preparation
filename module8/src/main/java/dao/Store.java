package dao;

import java.util.List;
import java.util.Optional;

public interface Store {

    Optional<Object> get(String key);

    List<Object> getAll(String namespace);

    Object save(String key, Object obj);

    boolean delete(String key);
}
