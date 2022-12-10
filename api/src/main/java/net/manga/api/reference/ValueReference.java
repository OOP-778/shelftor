package net.manga.api.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// This reference wrapping allows having a reference to a value without keeping the value alive.
// Also it allows querying in maps by the value itself since hashcode checks are done on the object
public interface ValueReference<T> {

    static <T> Weak<T> hashableWeak(T value) {
        return weak(value, false, null);
    }

    static <T> Weak<T> identityWeak(T value) {
        return weak(value, true, null);
    }

    static <T> Weak<T> weak(T value, boolean identity, ReferenceQueue<T> queue) {
        return new Weak<>(value, identity, queue);
    }

    static <T> Strong<T> hashableStrong(T value) {
        return new Strong<>(value, false);
    }

    static <T> Strong<T> identityStrong(T value) {
        return new Strong<>(value, true);
    }

    static <T> Strong<T> strong(T value, boolean identity) {
        return new Strong<>(value, identity);
    }

    @Nullable
    T get();

    void clear();

    class Strong<T> implements ValueReference<T> {
        private final int hashCode;
        private final boolean identity;

        private T referent;

        public Strong(@NotNull T referent, boolean identity) {
            this.referent = referent;
            this.hashCode = identity ? System.identityHashCode(referent) : referent.hashCode();
            this.identity = identity;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (obj instanceof ValueReference) {
                return this.hashCode == obj.hashCode();
            }

            return this.identity ? this.hashCode == System.identityHashCode(obj) : obj.hashCode() == this.hashCode;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        @Nullable
        public T get() {
            return this.referent;
        }

        @Override
        public void clear() {
            this.referent = null;
        }
    }

    class Weak<T> extends WeakReference<T> implements ValueReference<T> {
        private final int hashCode;
        private final boolean identity;

        public Weak(T referent, boolean identity, ReferenceQueue<T> queue) {
            super(referent, queue);
            this.identity = identity;
            this.hashCode = identity ? System.identityHashCode(referent) : referent.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (obj instanceof ValueReference) {
                return this.hashCode == obj.hashCode();
            }

            return this.identity ? this.hashCode == System.identityHashCode(obj) : obj.hashCode() == this.hashCode;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
