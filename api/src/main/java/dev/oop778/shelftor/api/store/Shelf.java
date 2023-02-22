package dev.oop778.shelftor.api.store;

import dev.oop778.shelftor.api.Shelftor;
import dev.oop778.shelftor.api.index.Indexable;
import dev.oop778.shelftor.api.query.Queryable;
import java.util.Collection;

public interface Shelf<T> extends Collection<T>, Indexable<T>, Queryable<T> {

    static <T> StoreBuilder<T, ?> builder() {
        return Shelftor.get().createBuilder();
    }
}