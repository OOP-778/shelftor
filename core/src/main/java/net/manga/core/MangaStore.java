package net.manga.core;

public class MangaStore<T> {

    public MangaStore(CoreMangaStoreBuilder builder) {

    }

    public static <T> CoreMangaStoreBuilder<T, ?> builder() {
        return new CoreMangaStoreBuilder<>();
    }
}
