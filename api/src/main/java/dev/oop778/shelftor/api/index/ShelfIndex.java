package dev.oop778.shelftor.api.index;

import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import dev.oop778.shelftor.api.reference.EntryReference;
import org.jetbrains.annotations.Unmodifiable;

public interface ShelfIndex<T, K> {
    void index(EntryReference<T> reference);

    default T getFirst(@NonNull K key) {
        return this.findFirst(key).orElse(null);
    }

    Optional<T> findFirst(@NonNull K key);

    @Unmodifiable
    Collection<T> get(@NonNull K key);

    String getName();
}
