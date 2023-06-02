package dev.oop778.shelftor.core.reference.type;

import java.util.concurrent.atomic.AtomicReference;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.core.reference.queue.CoreSimpleReferenceQueue;

public class CoreStrongEntryReference<T> implements EntryReference<T>, EntryReference.ListenableDisposable {
    private final AtomicReference<T> referentReference;
    private final CoreRererenceProps<T> props;
    private final CoreSimpleReferenceQueue<T> queue;

    public CoreStrongEntryReference(CoreSimpleReferenceQueue<T> queue, T referent, boolean identity) {
        this.referentReference = new AtomicReference<>(referent);
        this.props = new CoreRererenceProps<>(identity, referent);
        this.queue = queue;
    }

    @Override
    public T get() {
        return this.referentReference.get();
    }

    @Override
    public boolean dispose() {
        if (this.get() == null) {
            return false;
        }

        this.queue.safeOffer(this);

        return true;
    }

    @Override
    public boolean isIdentity() {
        return this.props.isIdentity();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof EntryReference<?>) {
            return this.hashCode() == obj.hashCode();
        }

        return this.isIdentity() ? this.hashCode() == System.identityHashCode(obj) : obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return this.props.getHashCode();
    }

    @Override
    public void postListenDispose() {
        this.referentReference.set(null);
    }
}
