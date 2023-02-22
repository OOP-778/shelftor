package dev.oop778.shelftor.core;

import dev.oop778.shelftor.api.Shelftor;
import dev.oop778.shelftor.api.reference.EntryReferenceBuilder;
import dev.oop778.shelftor.api.store.ShelfBuilder;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.core.query.CoreQuery;
import dev.oop778.shelftor.core.reference.CoreEntryReferenceBuilder;
import dev.oop778.shelftor.core.shelf.CoreShelfBuilder;

public class CoreShelftor extends Shelftor {

    static {
        new CoreShelftor();
    }

    @Override
    public Query createQuery() {
        return new CoreQuery();
    }

    @Override
    public <T> ShelfBuilder<T, ?> createBuilder() {
        return new CoreShelfBuilder<>();
    }

    @Override
    public <T> EntryReferenceBuilder<T> createReferenceBuilder() {
        return new CoreEntryReferenceBuilder<>();
    }
}
