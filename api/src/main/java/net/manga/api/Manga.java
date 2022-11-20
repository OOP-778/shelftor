package net.manga.api;

import net.manga.api.builder.StoreBuilder;
import net.manga.api.query.Query;

public abstract class Manga {
    private static Manga INSTANCE;

    protected Manga() {
        INSTANCE = this;
    }

    public static Manga get() {
        return INSTANCE;
    }

    public static void dispose() {
        INSTANCE = null;
    }

    public abstract Query createQuery();

    public abstract <T> StoreBuilder<T, ?> createBuilder();
}
