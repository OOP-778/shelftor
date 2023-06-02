package dev.oop778.shelftor.core.reference.queue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.NonNull;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.EntryReferenceQueue;
import dev.oop778.shelftor.api.shelf.ShelfSettings;
import org.jetbrains.annotations.Nullable;

public class CoreSimpleReferenceQueue<T> implements EntryReferenceQueue<T> {
    protected final Queue<EntryReference<T>> backingQueue;

    public CoreSimpleReferenceQueue(ShelfSettings settings) {
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
