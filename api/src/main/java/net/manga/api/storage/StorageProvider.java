package net.manga.api.storage;

import java.util.Collection;

@FunctionalInterface
public interface StorageProvider {
    <T> Collection<T> provide();
}
