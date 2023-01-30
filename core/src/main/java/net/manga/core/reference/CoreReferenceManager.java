package net.manga.core.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.NonNull;
import net.manga.api.reference.EntryReference;
import net.manga.api.reference.EntryReferenceQueue;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.util.Closeable;
import net.manga.core.reference.queue.CoreSimpleReferenceQueue;
import net.manga.core.reference.queue.CoreWeakReferenceQueue;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.store.MangaStoreSettings;
import net.manga.core.util.log.LogDebug;
import org.jetbrains.annotations.NotNull;

public class CoreReferenceManager<T> implements ReferenceManager<T> {
    private final MangaStoreSettings settings;
    private final Function<T, EntryReference<T>> referenceFactory;
    private final EntryReferenceQueue<T> referenceQueue;
    private final Map<Integer, EntryReference<T>> referenceMap;
    private final Collection<Consumer<EntryReference<T>>> removeListeners;
    private final Collection<Consumer<EntryReference<T>>> addListeners;
    private final Collection<Consumer<EntryReference<T>>> accessListeners;

    public CoreReferenceManager(MangaCoreStore<T> store) {
        this.settings = store.getSettings();
        this.referenceFactory = this.createReferenceFactory();
        this.referenceQueue = this.settings.isWeak() ? new CoreWeakReferenceQueue<>() : new CoreSimpleReferenceQueue<>(this.settings);
        this.referenceMap = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.removeListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.addListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.accessListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
    }

    @Override
    public EntryReference<T> getOrCreateReference(@NotNull T value) {
        return this.referenceMap.computeIfAbsent(this.hash(value), ($) -> this.announce(this.referenceFactory.apply(value)));
    }

    private EntryReference<T> announce(EntryReference<T> apply) {
        this.addListeners.forEach((listener) -> listener.accept(apply));
        return apply;
    }

    private int hash(T value) {
        return this.settings.isHashable() ? value.hashCode() : System.identityHashCode(value);
    }

    @Override
    public void releaseReference(@NotNull EntryReference<T> reference) {
        reference.dispose();
    }

    @Override
    public void releaseReference(@NonNull T value) {
        final EntryReference<T> existingReference = this.referenceMap.remove(this.hash(value));
        if (existingReference != null) {
            existingReference.dispose();
        }
    }

    @Override
    public EntryReference<T> createFetchingReference(@NotNull T value) {
        return EntryReference.strong(value, !this.settings.isHashable(), null);
    }

    private Function<T, EntryReference<T>> createReferenceFactory() {
        return (value) -> {
            if (this.settings.isWeak()) {
                return EntryReference.weak(value, !this.settings.isHashable(), this.referenceQueue);
            }

            return EntryReference.strong(value, !this.settings.isHashable(), this.referenceQueue);
        };
    }

    public EntryReference<T> add(T value) {
        return this.referenceFactory.apply(value);
    }

    @Override
    public Closeable onReferenceRemove(Consumer<EntryReference<T>> referenceConsumer) {
        this.removeListeners.add(referenceConsumer);
        return () -> this.removeListeners.remove(referenceConsumer);
    }

    @Override
    public Closeable onReferenceCreated(Consumer<EntryReference<T>> referenceConsumer) {
        this.addListeners.add(referenceConsumer);
        return () -> this.addListeners.remove(referenceConsumer);
    }

    @Override
    public Closeable onReferenceAccess(Consumer<EntryReference<T>> referenceConsumer) {
        this.accessListeners.add(referenceConsumer);
        return () -> this.accessListeners.remove(referenceConsumer);
    }

    public void runRemoveQueue() {
        if (this.referenceQueue == null) {
            return;
        }

        EntryReference<T> reference;
        while ((reference = this.referenceQueue.safePoll()) != null) {
            final EntryReference<T> finalReference = reference;
            this.removeListeners.forEach((listener) -> listener.accept(finalReference));

            LogDebug.log("[ReferenceManager] Removed reference: %s", finalReference);
        }
    }

    public Map<Integer, EntryReference<T>> getReferenceMap() {
        return this.referenceMap;
    }
}
