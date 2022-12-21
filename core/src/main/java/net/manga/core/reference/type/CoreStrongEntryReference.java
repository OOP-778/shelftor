package net.manga.core.reference.type;

import java.util.concurrent.atomic.AtomicReference;
import net.manga.core.reference.queue.CoreAbstractReferenceQueue;

public class CoreStrongEntryReference<T> extends CoreAbstractEntryReference<T> {
    private final AtomicReference<T> referent;

    public CoreStrongEntryReference(CoreAbstractReferenceQueue<T> queue, T referent, boolean identity) {
        super(queue, referent, identity);
        this.referent = new AtomicReference<>(referent);
    }

    @Override
    public T get() {
        return this.referent.get();
    }

    @Override
    public boolean dispose() {
        if (this.get() == null) {
            return false;
        }

        this.referent.set(null);
        this.queue.offer(this);

        return true;
    }
}
