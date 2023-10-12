package dev.oop778.shelftor.api.shelf;

import dev.oop778.shelftor.api.Shelftor;
import dev.oop778.shelftor.api.dumpable.Dumpable;
import dev.oop778.shelftor.api.index.Indexable;
import dev.oop778.shelftor.api.query.Queryable;
import java.util.Collection;

public interface Shelf<T> extends Collection<T>, Indexable<T>, Queryable<T>, Dumpable {

    static <T> ShelfBuilder<T, ?> builder() {
        return Shelftor.get().createBuilder();
    }
}
