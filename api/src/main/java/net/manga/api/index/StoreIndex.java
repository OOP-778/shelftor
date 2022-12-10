package net.manga.api.index;

import java.util.List;
import java.util.Optional;
import net.manga.api.reference.ValueReference;

public interface StoreIndex<T> {
    void index(ValueReference<T> reference);

    default T getFirst(final Object key) {
        return this.findFirst(key).orElse(null);
    }

    Optional<T> findFirst(Object key);

    List<T> get(Object key);

    String getName();
}
