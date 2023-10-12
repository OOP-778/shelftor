package dev.oop778.shelftor.api.shelf;

import dev.oop778.shelftor.api.dumpable.Dumpable;

public interface ShelfSettings extends Dumpable {
    boolean isConcurrent();

    boolean isHashable();

    boolean isWeak();
}
