package dev.oop778.shelftor.api;

import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.reference.EntryReferenceBuilder;
import dev.oop778.shelftor.api.store.ShelfBuilder;
import java.util.Objects;

public abstract class Shelftor {
    private static Shelftor INSTANCE;

    protected Shelftor() {
        INSTANCE = this;
    }

    public static Shelftor get() {
        if (INSTANCE == null) {
            try {
                Class.forName("dev.oop778.shelftor.core.CoreShelftor");
            } catch (ClassNotFoundException ignored) {}
        }

        Objects.requireNonNull(INSTANCE, "Shelftor has not been initialized");
        return INSTANCE;
    }

    public static void dispose() {
        INSTANCE = null;
    }

    public abstract Query createQuery();

    public abstract <T> ShelfBuilder<T, ?> createBuilder();

    public abstract <T> EntryReferenceBuilder<T> createReferenceBuilder();
}
