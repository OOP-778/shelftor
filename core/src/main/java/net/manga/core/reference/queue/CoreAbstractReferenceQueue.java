package net.manga.core.reference.queue;

import lombok.NonNull;
import net.manga.api.reference.EntryReference;
import net.manga.api.reference.EntryReferenceQueue;
import net.manga.api.store.StoreSettings;
import org.jetbrains.annotations.NotNull;

public abstract class CoreAbstractReferenceQueue<T> implements EntryReferenceQueue<T> {
    protected StoreSettings settings;

    protected CoreAbstractReferenceQueue(@NotNull StoreSettings settings) {
        this.settings = settings;
    }

    public abstract void offer(@NonNull EntryReference<T> value);
}
