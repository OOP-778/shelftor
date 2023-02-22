package dev.oop778.shelftor.api.reference;

import dev.oop778.shelftor.api.util.Closeable;
import java.util.function.Consumer;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public interface ReferenceManager<T> {
    EntryReference<T> getOrCreateReference(@NonNull T value);

    void releaseReference(@NonNull EntryReference<T> reference);

    void releaseReference(@NonNull T value);

    // Create a fetching reference
    // No matter if it's weak or not, it will return strong either hashable or not
    // This is used just for fetching by keys, to not create a real java ref
    EntryReference<T> createFetchingReference(@NotNull T value);

    Closeable onReferenceRemove(Consumer<EntryReference<T>> referenceConsumer);

    Closeable onReferenceCreated(Consumer<EntryReference<T>> referenceConsumer);

    Closeable onReferenceAccess(Consumer<EntryReference<T>> referenceConsumer);
}
