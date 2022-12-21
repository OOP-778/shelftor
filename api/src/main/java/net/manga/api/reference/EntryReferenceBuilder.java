package net.manga.api.reference;

import lombok.NonNull;

public interface EntryReferenceBuilder<T> {
    EntryReferenceBuilder<T> weak();

    EntryReferenceBuilder<T> identity(boolean identity);

    EntryReferenceBuilder<T> managedBy(@NonNull EntryReferenceQueue<T> queue);

    EntryReferenceFactory<T> buildFactory();

    EntryReference<T> build(@NonNull T value);
}
