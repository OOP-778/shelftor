package net.manga.core.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.ValueReference;
import org.jetbrains.annotations.NotNull;

public class ReferencedCollection<T> implements Collection<T> {
    private final Collection<ValueReference<T>> backing;
    private final ReferenceManager<T> referenceManager;

    public ReferencedCollection(
        Collection<ValueReference<T>> backing,
        ReferenceManager<T> referenceManager
    ) {
        this.backing = backing;
        this.referenceManager = referenceManager;
        this.referenceManager.onReferenceRemove(this.backing::remove);
    }

    public boolean addReference(ValueReference<T> reference) {
        return this.backing.add(reference);
    }

    public boolean removeReference(ValueReference<T> reference) {
        return this.backing.remove(reference);
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public boolean isEmpty() {
        return this.backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.backing.contains(this.referenceManager.createFetchingReference((T) o));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new ReferencedIterator<T, ValueReference<T>>(this.backing.iterator()) {
            @Override
            protected T extractKey(ValueReference<T> reference) {
                return reference.get();
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.backing.stream()
            .map(ValueReference::get)
            .filter(Objects::nonNull)
            .toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return this.backing.stream()
            .map(ValueReference::get)
            .filter(Objects::nonNull)
            .toArray(($) -> a);
    }

    @Override
    public boolean add(T t) {
        final ValueReference<T> reference = this.referenceManager.createReference(t);
        final boolean add = this.backing.add(reference);

        if (!add) {
            this.referenceManager.releaseReference(reference);
        }

        return add;
    }

    @Override
    public boolean remove(Object o) {
        final ValueReference<T> reference = this.referenceManager.createFetchingReference((T) o);
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
}
