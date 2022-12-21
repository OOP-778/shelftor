package net.manga.api.reference;

import lombok.NonNull;

@FunctionalInterface
public interface EntryReferenceFactory<T> {
    EntryReference<T> createReference(@NonNull T value);
}
