package net.manga.core.reference.queue;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import net.manga.api.reference.EntryReference;
import net.manga.api.store.StoreSettings;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CoreWeakReferenceQueue<T> extends CoreSimpleReferenceQueue<T> {
    private final ReferenceQueue<T> backingGcQueue;

    public CoreWeakReferenceQueue(StoreSettings settings) {
        super(settings);
        this.backingGcQueue = new ReferenceQueue<>();
    }

    @Override
    public @Nullable EntryReference<T> poll() {
        final EntryReference<T> poll = super.poll();
        if (poll != null) {
            return null;
        }

        final Reference<? extends T> poll2 = this.backingGcQueue.poll();
        return (EntryReference<T>) poll2;
    }

    public ReferenceQueue<T> getBackingGcQueue() {
        return this.backingGcQueue;
    }
}
