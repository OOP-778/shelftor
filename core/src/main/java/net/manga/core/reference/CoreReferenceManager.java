package net.manga.core.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.ValueReference;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.store.MangaStoreSettings;
import org.jetbrains.annotations.NotNull;

public class CoreReferenceManager<T> implements ReferenceManager<T> {
    private final MangaStoreSettings settings;
    private final Function<T, ValueReference<T>> referenceFactory;
    private final ReferenceQueue<T> referenceQueue;
    private final Map<Integer, ValueReference<T>> referenceMap;
    private final Collection<Consumer<ValueReference<T>>> removeListeners;

    public CoreReferenceManager(MangaCoreStore<T> store) {
        this.settings = store.getSettings();
        this.referenceFactory = this.createReferenceFactory();
        this.referenceQueue = this.settings.isWeakKeys() ? new ReferenceQueue<>() : null;
        this.referenceMap = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.removeListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
    }

    @Override
    public ValueReference<T> createReference(@NotNull T value) {
        return this.referenceFactory.apply(value);
    }

    @Override
    public void releaseReference(@NotNull ValueReference<T> reference) {
        reference.clear();
    }

    @Override
    public ValueReference<T> createFetchingReference(@NotNull T value) {
        return ValueReference.strong(value, !this.settings.isHashable());
    }

    private Function<T, ValueReference<T>> createReferenceFactory() {
        return (value) -> {
            if (this.settings.isWeakKeys()) {
                return ValueReference.weak(value, !this.settings.isHashable(), this.referenceQueue);
            }

            return ValueReference.strong(value, !this.settings.isHashable());
        };
    }

    public Function<T, ValueReference<T>> getReferenceFactory() {
        return this.referenceFactory;
    }

    public ValueReference<T> add(T value) {
        return this.referenceFactory.apply(value);
    }

    @Override
    public Runnable onReferenceRemove(Consumer<ValueReference<T>> referenceConsumer) {
        this.removeListeners.add(referenceConsumer);
        return () -> this.removeListeners.remove(referenceConsumer);
    }

    public void runRemoveQueue() {
        if (this.referenceQueue == null) {
            return;
        }

        Reference<? extends T> reference;
        while ((reference = this.referenceQueue.poll()) != null) {
            final Reference<? extends T> finalReference = reference;
            this.removeListeners.forEach((listener) -> listener.accept((ValueReference<T>) finalReference));
        }
    }
}
