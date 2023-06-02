package dev.oop778.shelftor.api.reference;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

// Queue used for handling removing of queues
public interface EntryReferenceQueue<T> {
    @Nullable EntryReference<T> safePoll();

    void safeOffer(@NonNull EntryReference<T> reference);
}
