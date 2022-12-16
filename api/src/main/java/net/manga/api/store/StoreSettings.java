package net.manga.api.store;

public interface StoreSettings {
    boolean isConcurrent();

    boolean isHashable();

    boolean isWeak();
}
