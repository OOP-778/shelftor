package net.manga.api.reference;

import org.jetbrains.annotations.Nullable;

// Queue used for handling removing of queues
public interface EntryReferenceQueue<T> {
   @Nullable EntryReference<T> poll();
}
