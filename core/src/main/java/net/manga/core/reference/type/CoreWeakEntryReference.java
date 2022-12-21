package net.manga.core.reference.type;

import java.lang.ref.WeakReference;
import java.util.Optional;
import net.manga.core.reference.queue.CoreAbstractReferenceQueue;
import net.manga.core.reference.queue.CoreWeakReferenceQueue;
import org.jetbrains.annotations.Nullable;

public class CoreWeakEntryReference<T> extends CoreAbstractEntryReference<T> {
    private final WeakReference<T> backingReference;

    public CoreWeakEntryReference(@Nullable CoreAbstractReferenceQueue<T> queue, T referent, boolean identity) {
        super(queue, referent, identity);
        this.backingReference = new WeakReference<>(
            referent,
            Optional.ofNullable(queue)
                .map(($) -> ((CoreWeakReferenceQueue<T>) queue).getBackingGcQueue())
                .orElse(null)
        );
    }

    @Override
    public T get() {
        return this.backingReference.get();
    }

    @Override
    public boolean dispose() {
        if (this.get() == null) {
            return false;
        }

        this.backingReference.clear();
        this.queue.offer(this);

        return true;
    }
}
