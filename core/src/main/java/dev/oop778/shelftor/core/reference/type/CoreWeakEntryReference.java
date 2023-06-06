package dev.oop778.shelftor.core.reference.type;

import java.lang.ref.WeakReference;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.core.reference.queue.CoreWeakReferenceQueue;
import org.jetbrains.annotations.Nullable;

public class CoreWeakEntryReference<T> extends WeakReference<T> implements EntryReference<T> {
    private final CoreRererenceProps<T> props;
    private volatile boolean marked;

    public CoreWeakEntryReference(@Nullable CoreWeakReferenceQueue<T> queue, T referent, boolean identity) {
        super(referent, queue);
        this.props = new CoreRererenceProps<>(identity, referent);
    }

    @Override
    public T get() {
        return super.get();
    }

    @Override
    public boolean isMarked() {
        return this.marked;
    }

    @Override
    public boolean dispose() {
        if (this.get() == null) {
            return false;
        }

        this.marked = true;
        super.enqueue();
        super.clear();

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
}
