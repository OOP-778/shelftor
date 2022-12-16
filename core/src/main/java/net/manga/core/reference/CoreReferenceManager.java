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
import net.manga.api.util.Closeable;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.store.MangaStoreSettings;
import org.jetbrains.annotations.NotNull;

public class CoreReferenceManager<T> implements ReferenceManager<T> {
    private final MangaStoreSettings settings;
    private final Function<T, ValueReference<T>> referenceFactory;
    private final ReferenceQueue<T> referenceQueue;
    private final Map<Integer, ValueReference<T>> referenceMap;
    private final Collection<Consumer<ValueReference<T>>> removeListeners;
    private final Collection<Consumer<ValueReference<T>>> addListeners;
    private final Collection<Consumer<ValueReference<T>>> accessListeners;

    public CoreReferenceManager(MangaCoreStore<T> store) {
        this.settings = store.getSettings();
        this.referenceFactory = this.createReferenceFactory();
        this.referenceQueue = this.settings.isWeak() ? new ReferenceQueue<>() : null;
        this.referenceMap = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.removeListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.addListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.accessListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
    }

    @Override
    public ValueReference<T> getOrCreateReference(@NotNull T value) {
        return this.referenceMap.computeIfAbsent(this.hash(value), ($) -> this.announce(this.referenceFactory.apply(value)));
    }

    private ValueReference<T> announce(ValueReference<T> apply) {
        this.addListeners.forEach((listener) -> listener.accept(apply));
        return apply;
    }

    private int hash(T value) {
        return this.settings.isHashable() ? value.hashCode() : System.identityHashCode(value);
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
            if (this.settings.isWeak()) {
                return ValueReference.weak(value, !this.settings.isHashable(), this.referenceQueue);
            }

            return ValueReference.strong(value, !this.settings.isHashable());
        };
    }

    public ValueReference<T> add(T value) {
        return this.referenceFactory.apply(value);
    }

    @Override
    public Closeable onReferenceRemove(Consumer<ValueReference<T>> referenceConsumer) {
        this.removeListeners.add(referenceConsumer);
        return () -> this.removeListeners.remove(referenceConsumer);
    }

    @Override
    public Closeable onReferenceCreated(Consumer<ValueReference<T>> referenceConsumer) {
        this.addListeners.add(referenceConsumer);
        return () -> this.addListeners.remove(referenceConsumer);
    }

    @Override
    public Closeable onReferenceAccess(Consumer<ValueReference<T>> referenceConsumer) {
        this.accessListeners.add(referenceConsumer);
        return () -> this.accessListeners.remove(referenceConsumer);
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
