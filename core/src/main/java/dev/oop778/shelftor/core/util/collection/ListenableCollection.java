package dev.oop778.shelftor.core.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;

public class ListenableCollection<T> implements Collection<T> {
    private Collection<T> backing;
    private final Collection<AddListener<T>> addListeners;
    private final Collection<RemoveListener<T>> removeListeners;

    public ListenableCollection(Collection<T> wrapping, boolean concurrent) {
        this.backing = wrapping;

        this.addListeners = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.removeListeners = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
    }

    protected void setBacking(Collection<T> backing) {
        this.backing = backing;
    }

    public Runnable onAdd(AddListener<T> listener) {
        this.addListeners.add(listener);
        return () -> this.addListeners.remove(listener);
    }

    public Runnable onRemove(RemoveListener<T> listener) {
        this.removeListeners.add(listener);
        return () -> this.removeListeners.remove(listener);
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
        return this.backing.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new ListenableIterator(this.backing.iterator());
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.backing.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return this.backing.toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (this.backing.add(t)) {
            this.addListeners.forEach(listener -> listener.onAdd(t));
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (this.backing.remove(o)) {
            this.removeListeners.forEach(listener -> listener.onRemove((T) o));
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.backing.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return this.backing.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.backing.retainAll(c);
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    public class ListenableIterator implements Iterator<T> {
        private final Iterator<T> wrapping;
        private T current;

        public ListenableIterator(Iterator<T> wrapping) {
            this.wrapping = wrapping;
        }

        @Override
        public boolean hasNext() {
            return this.wrapping.hasNext();
        }

        @Override
        public T next() {
            this.current = this.wrapping.next();
            return this.current;
        }

        @Override
        public void remove() {
            if (this.current != null) {
                ListenableCollection.this.removeListeners.forEach(listener -> listener.onRemove(this.current));
            }

            this.wrapping.remove();
        }
    }

    @FunctionalInterface
    public interface AddListener<T> {
        void onAdd(@NotNull T value);
    }

    @FunctionalInterface
    public interface RemoveListener<T> {
        void onRemove(@NotNull T value);
    }
}
