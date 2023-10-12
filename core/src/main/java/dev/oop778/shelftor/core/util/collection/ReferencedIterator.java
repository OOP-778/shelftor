package dev.oop778.shelftor.core.util.collection;

import java.util.Iterator;

public abstract class ReferencedIterator<T, U> extends WrappedSmartIterator<U, T> {

    public ReferencedIterator(Iterator<U> iterator) {
        super(iterator);
    }
}
