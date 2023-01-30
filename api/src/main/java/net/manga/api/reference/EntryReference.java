package net.manga.api.reference;

import net.manga.api.Manga;

public interface EntryReference<T> {
    T get();

    boolean dispose();

    boolean isIdentity();

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
        return Manga.get().createReferenceBuilder();
    }
}
