package net.manga.core.reference;

import lombok.NonNull;
import net.manga.api.reference.EntryReference;
import net.manga.api.reference.EntryReferenceBuilder;
import net.manga.api.reference.EntryReferenceFactory;
import net.manga.api.reference.EntryReferenceQueue;
import net.manga.core.reference.queue.CoreSimpleReferenceQueue;
import net.manga.core.reference.queue.CoreWeakReferenceQueue;
import net.manga.core.reference.type.CoreStrongEntryReference;
import net.manga.core.reference.type.CoreWeakEntryReference;

public class CoreEntryReferenceBuilder<T> implements EntryReferenceBuilder<T> {
    private boolean weak = false;
    private boolean identity = true;
    private EntryReferenceQueue<T> queue;

    @Override
    public EntryReferenceBuilder<T> weak() {
        this.weak = true;
        return this;
    }

    @Override
    public EntryReferenceBuilder<T> identity(boolean identity) {
        this.identity = identity;
        return this;
    }

    @Override
    public EntryReferenceBuilder<T> managedBy(EntryReferenceQueue<T> queue) {
        this.queue = queue;
        return this;
    }

    @Override
    public EntryReferenceFactory<T> buildFactory() {
        return (value) -> {
            if (this.weak) {
                return new CoreWeakEntryReference<>(
                    ((CoreWeakReferenceQueue<T>) this.queue),
                    value,
                    this.identity
                );
            }

            return new CoreStrongEntryReference<>(
                ((CoreSimpleReferenceQueue<T>) this.queue),
                value,
                this.identity
            );
        };
    }

    @Override
    public EntryReference<T> build(@NonNull T value) {
        return this.buildFactory().createReference(value);
    }
}
