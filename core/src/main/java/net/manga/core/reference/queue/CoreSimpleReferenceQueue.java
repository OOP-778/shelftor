package net.manga.core.reference.queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.NonNull;
import net.manga.api.reference.EntryReference;
import net.manga.api.reference.EntryReferenceQueue;
import net.manga.api.store.StoreSettings;
import org.jetbrains.annotations.Nullable;

public class CoreSimpleReferenceQueue<T> implements EntryReferenceQueue<T> {
    protected final Queue<EntryReference<T>> backingQueue;

    public CoreSimpleReferenceQueue(StoreSettings settings) {
        this.backingQueue = settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new LinkedList<>();
    }

    @Override
    public @Nullable EntryReference<T> safePoll() {
        return this.backingQueue.poll();
    }

    @Override
    public void safeOffer(@NonNull EntryReference<T> value) {
        this.backingQueue.add(value);
    }
}
