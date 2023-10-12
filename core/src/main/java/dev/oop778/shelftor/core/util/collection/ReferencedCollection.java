package dev.oop778.shelftor.core.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.ReferenceManager;
import dev.oop778.shelftor.core.reference.CoreReferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReferencedCollection<T> implements Collection<T> {
    private final Collection<EntryReference<T>> backing;
    private final CoreReferenceManager<T> referenceManager;
    private Function<T, EntryReference<T>> referenceFunction;

    public ReferencedCollection(
        Collection<EntryReference<T>> backing,
        @Nullable ReferenceManager<T> referenceManager
    ) {
        this.backing = backing;
        if ((this.referenceManager = (CoreReferenceManager<T>) referenceManager) != null) {
            this.referenceManager.onReferenceRemove(this.backing::remove);
            this.referenceFunction = this.referenceManager::createFetchingReference;
        }
    }

    public void setFetcher(Function<T, EntryReference<T>> referenceFunction) {
        this.referenceFunction = referenceFunction;
    }

    public boolean addReference(EntryReference<T> reference) {
        return this.backing.add(reference);
    }

    public boolean removeReference(EntryReference<T> reference) {
        return this.backing.remove(reference);
    }

    @Override
    public int size() {
        this.runRemoveInvalid();
        return this.backing.size();
    }

    @Override
    public boolean isEmpty() {
        this.runRemoveInvalid();
        return this.backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        this.runRemoveInvalid();
        return this.backing.contains(this.referenceFunction.apply((T) o));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        this.runRemoveInvalid();
        return new WrappedSmartIterator<EntryReference<T>, T>(this.backing.iterator()) {
            @Override
            protected T transform(EntryReference<T> reference) {
                return reference.get();
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        this.runRemoveInvalid();
        return this.backing.stream()
            .map(EntryReference::get)
            .filter(Objects::nonNull)
            .toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        this.runRemoveInvalid();
        return this.backing.stream()
            .map(EntryReference::get)
            .filter(Objects::nonNull)
            .toArray(($) -> a);
    }

    @Override
    public boolean add(T t) {
        this.runRemoveInvalid();

        final EntryReference<T> reference = this.referenceManager.getOrCreateReference(t);
        final boolean add = this.backing.add(reference);

        if (!add) {
            this.referenceManager.releaseReference(reference);
        }

        return add;
    }

    @Override
    public boolean remove(Object o) {
        this.runRemoveInvalid();
        final EntryReference<T> reference = this.referenceManager.createFetchingReference((T) o);
        return this.backing.remove(reference);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (final Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean changed = false;
        for (final T t : c) {
            if (this.add(t)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            if (this.remove(o)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.clear();
        for (final Object o : c) {
            this.add((T) o);
        }

        return true;
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    protected void runRemoveInvalid() {
        if (this.referenceManager == null) {
            return;
        }

        this.referenceManager.runRemoveQueue();
    }

    public Collection<T> unmodifiable() {
        return Collections.unmodifiableCollection(this);
    }

    public Collection<EntryReference<T>> getBacking() {
        return this.backing;
    }

    @Override
    public String toString() {
        return this.backing.toString();
    }
}
