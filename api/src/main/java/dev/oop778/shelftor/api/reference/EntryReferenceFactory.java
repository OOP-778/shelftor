package dev.oop778.shelftor.api.reference;

import lombok.NonNull;

@FunctionalInterface
public interface EntryReferenceFactory<T> {
    EntryReference<T> createReference(@NonNull T value);
}
