package dev.oop778.shelftor.core.reference;

import dev.oop778.shelftor.core.reference.type.CoreStrongEntryReference;
import dev.oop778.shelftor.core.reference.type.CoreWeakEntryReference;
import lombok.NonNull;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.EntryReferenceBuilder;
import dev.oop778.shelftor.api.reference.EntryReferenceFactory;
import dev.oop778.shelftor.api.reference.EntryReferenceQueue;
import dev.oop778.shelftor.core.reference.queue.CoreSimpleReferenceQueue;
import dev.oop778.shelftor.core.reference.queue.CoreWeakReferenceQueue;

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
