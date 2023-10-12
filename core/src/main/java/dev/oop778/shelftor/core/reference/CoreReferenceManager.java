package dev.oop778.shelftor.core.reference;

import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.EntryReferenceQueue;
import dev.oop778.shelftor.api.reference.ReferenceManager;
import dev.oop778.shelftor.api.util.Closeable;
import dev.oop778.shelftor.core.reference.queue.CoreSimpleReferenceQueue;
import dev.oop778.shelftor.core.reference.queue.CoreWeakReferenceQueue;
import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import dev.oop778.shelftor.core.util.log.LogDebug;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoreReferenceManager<T> implements ReferenceManager<T> {
    private final CoreShelfSettings settings;
    private final Function<T, EntryReference<T>> referenceFactory;
    private final EntryReferenceQueue<T> referenceQueue;
    @Getter
    private final Map<Integer, EntryReference<T>> referenceMap;
    private final Collection<Consumer<EntryReference<T>>> removeListeners;
    private final Collection<Consumer<EntryReference<T>>> addListeners;
    private final Collection<Consumer<EntryReference<T>>> accessListeners;

    public CoreReferenceManager(CoreShelf<T> store) {
        this.settings = store.getSettings();
        this.referenceFactory = this.createReferenceFactory();
        this.referenceQueue = this.settings.isWeak() ? new CoreWeakReferenceQueue<>() : new CoreSimpleReferenceQueue<>(this.settings);
        this.referenceMap = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.removeListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.addListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.accessListeners = this.settings.isConcurrent() ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
    }

    private Function<T, EntryReference<T>> createReferenceFactory() {
        return (value) -> {
            if (this.settings.isWeak()) {
                return EntryReference.weak(value, !this.settings.isHashable(), this.referenceQueue);
            }

            return EntryReference.strong(value, !this.settings.isHashable(), this.referenceQueue);
        };
    }

    @Override
    public EntryReference<T> getOrCreateReference(@NotNull T value) {
        return this.referenceMap.computeIfAbsent(this.hash(value), ($) -> this.announce(this.referenceFactory.apply(value)));
    }

    private EntryReference<T> announce(EntryReference<T> apply) {
        this.addListeners.forEach((listener) -> listener.accept(apply));
        return apply;
    }

    @Override
    public boolean releaseReference(@NotNull EntryReference<T> reference) {
        final EntryReference<T> remove = this.referenceMap.remove(reference.hashCode());
        if (remove == null) {
            return false;
        }

        return remove.dispose();
    }

    @Override
    public boolean releaseReference(@NonNull T value) {
        final EntryReference<T> existingReference = this.referenceMap.get(this.hash(value));
        if (existingReference == null) {
            return false;
        }

        return this.releaseReference(existingReference);
    }

    @Override
    public EntryReference<T> createFetchingReference(@NotNull T value) {
        return EntryReference.strong(value, !this.settings.isHashable(), null);
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

    private int hash(T value) {
        return this.settings.isHashable() ? value.hashCode() : System.identityHashCode(value);
    }

    @Override
    public void dump(Collection<String> lines) {
        lines.add("== ReferenceManager ==");
        lines.add("References = " + this.referenceMap.size());
        lines.add("Remove listeners = " + this.removeListeners.size());
        lines.add("Add listeners = " + this.addListeners.size());
        lines.add("Access listeners = " + this.accessListeners.size());
    }

    public void callReferenceAccess(EntryReference<T> reference) {
        LogDebug.log("Calling reference access");
        this.accessListeners.forEach((listener) -> listener.accept(reference));
    }

    public @Nullable EntryReference<T> getRealReference(T value) {
        return this.referenceMap.get(this.hash(value));
    }

    public EntryReference<T> add(T value) {
        return this.referenceFactory.apply(value);
    }

    public void runRemoveQueue() {
        if (this.referenceQueue == null) {
            return;
        }

        EntryReference<T> reference;
        while ((reference = this.referenceQueue.safePoll()) != null) {
            final EntryReference<T> finalReference = reference;
            this.removeListeners.forEach((listener) -> listener.accept(finalReference));

            LogDebug.log("Removed reference: %s", finalReference);
            if (finalReference instanceof EntryReference.ListenableDisposable) {
                ((EntryReference.ListenableDisposable) finalReference).postListenDispose();
            }
        }
    }
}
