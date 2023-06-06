package dev.oop778.shelftor.api.reference;

import dev.oop778.shelftor.api.Shelftor;

public interface EntryReference<T> {
    T get();

    boolean dispose();

    boolean isIdentity();

    default boolean isMarked() {
        return false;
    }

    static <T> EntryReference<T> hashableWeak(T value) {
        return weak(value, false, null);
    }

    static <T> EntryReference<T> identityWeak(T value) {
        return weak(value, true, null);
    }

    static <T> EntryReference<T> weak(T value, boolean identity, EntryReferenceQueue<T> queue) {
        return EntryReference.<T>builder()
            .weak()
            .identity(identity)
            .managedBy(queue)
            .build(value);
    }

    static <T> EntryReference<T> hashableStrong(T value) {
        return strong(value, false, null);
    }

    static <T> EntryReference<T> identityStrong(T value) {
        return strong(value, true, null);
    }

    static <T> EntryReference<T> strong(T value, boolean identity, EntryReferenceQueue<T> queue) {
        return EntryReference.<T>builder()
            .identity(identity)
            .managedBy(queue)
            .build(value);
    }

    static <T> EntryReferenceBuilder<T> builder() {
        return Shelftor.get().createReferenceBuilder();
    }

    interface ListenableDisposable {
        void postListenDispose();
    }
}
