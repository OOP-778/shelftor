package dev.oop778.shelftor.api;

import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.reference.EntryReferenceBuilder;
import dev.oop778.shelftor.api.store.StoreBuilder;

public abstract class Shelftor {
    private static Shelftor INSTANCE;

    protected Shelftor() {
        INSTANCE = this;
    }

    public static Shelftor get() {
        return INSTANCE;
    }

    public static void dispose() {
        INSTANCE = null;
    }

    public abstract Query createQuery();

    public abstract <T> StoreBuilder<T, ?> createBuilder();

    public abstract <T> EntryReferenceBuilder<T> createReferenceBuilder();
}
