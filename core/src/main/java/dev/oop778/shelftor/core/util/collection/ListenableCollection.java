package dev.oop778.shelftor.core.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;

public class ListenableCollection<T> implements Collection<T> {
    protected Collection<T> backing;
    private final Collection<AddListener<T>> addListeners;
    private final Collection<RemoveListener<T>> removeListeners;
    private final Collection<AccessListener<T>> accessListeners;

    public ListenableCollection(Collection<T> wrapping, boolean concurrent) {
        this.backing = wrapping;

        this.addListeners = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.removeListeners = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
        this.accessListeners = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayList<>();
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

    public Runnable onAccess(AccessListener<T> listener) {
        this.accessListeners.add(listener);
        return () -> this.accessListeners.remove(listener);
    }

    @FunctionalInterface
    public interface AddListener<T> {
        void onAdd(@NotNull T value);
    }

    @FunctionalInterface
    public interface RemoveListener<T> {
        void onRemove(@NotNull T value);
    }

    @FunctionalInterface
    public interface AccessListener<T> {
        boolean onAccess(@NotNull T value);
    }

    public class ListenableIterator extends WrappedSmartIterator<T, T> {

        public ListenableIterator(Iterator<T> wrapping) {
            super(wrapping);
        }

        @Override
        public void remove() {
            if (this.current != null) {
                ListenableCollection.this.removeListeners.forEach(listener -> listener.onRemove(this.current));
            }

            super.remove();
        }

        @Override
        protected boolean validate(T value) {
            return this.onAccess(value);
        }

        private boolean onAccess(T value) {
            for (final AccessListener<T> accessListener : ListenableCollection.this.accessListeners) {
                if (!accessListener.onAccess(value)) {
                    return false;
                }
            }

            return true;
        }
    }
}
