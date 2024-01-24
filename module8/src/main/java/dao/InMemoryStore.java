package dao;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryStore.class);

    private final ConcurrentHashMap<String, Object> store;

    public InMemoryStore() {
        store = new ConcurrentHashMap<>();
    }

    public InMemoryStore(Map<String, Object> store) {
        Preconditions.checkNotNull(store, "Store cannot be null");
        this.store = new ConcurrentHashMap<>(store);
    }

    public void init(Map<String, Object> store) {
        Preconditions.checkNotNull(store, "Store cannot be null");
        Preconditions.checkState(this.store.isEmpty(), "Internal store must be empty. Size is ", this.store.size());
        this.store.putAll(store);
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public List<Object> getAll(String namespace) {
        return store
                .entrySet()
                .stream()
                    .filter(entry -> entry.getKey().startsWith(namespace))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
    }

    @Override
    public Object save(String key, Object obj) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkNotNull(obj, "Savable object cannot be null");
        return store.put(key, obj);
    }

    @Override
    public boolean delete(String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");

        return store.remove(key) != null;
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
        LOG.info("InMemoryStore was cleaned up.");
    }
}
