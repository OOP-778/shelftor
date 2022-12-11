package net.manga.api.index;

import java.util.Collection;
import java.util.Optional;
import net.manga.api.reference.ValueReference;
import org.jetbrains.annotations.Unmodifiable;

public interface StoreIndex<T, K> {
    void index(ValueReference<T> reference);

    default T getFirst(K key) {
        return this.findFirst(key).orElse(null);
    }

    Optional<T> findFirst(K key);

    @Unmodifiable
    Collection<T> get(K key);

    String getName();
}
