package net.manga.core.reference.type;

import net.manga.api.reference.EntryReference;
import net.manga.core.reference.queue.CoreAbstractReferenceQueue;

public abstract class CoreAbstractEntryReference<T> implements EntryReference<T> {
    protected final CoreAbstractReferenceQueue<T> queue;
    protected final boolean identity;
    protected final int hashCode;

    public CoreAbstractEntryReference(CoreAbstractReferenceQueue<T> queue, T referent, boolean identity) {
        this.queue = queue;
        this.identity = identity;
        this.hashCode = identity ? System.identityHashCode(referent) : referent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof EntryReference<?>) {
            return this.hashCode == obj.hashCode();
        }

        return this.identity ? this.hashCode == System.identityHashCode(obj) : obj.hashCode() == this.hashCode;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
