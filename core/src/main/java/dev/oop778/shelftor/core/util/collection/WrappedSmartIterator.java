package dev.oop778.shelftor.core.util.collection;

import java.util.Iterator;

/**
 * Didn't think of a name for this iterator but:
 * 1. It does not ever allow nulls to be passed from iterator, hence first next value is gathered in hasNext, not
 * in next()
 * 2. It allows for transforming values IN to OUT
 * 3. It allows filtering for any overriding class
 */
public class WrappedSmartIterator<IN, OUT> implements Iterator<OUT> {
    protected final Iterator<IN> iterator;
    protected IN current;
    protected boolean advanced;

    public WrappedSmartIterator(Iterator<IN> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        if (!this.advanced) {
            this.advance();
            this.advanced = true;
        }

        return this.current != null;
    }

    @Override
    public OUT next() {
        if (!this.advanced) {
            this.advance();
        }

        this.advanced = false;
        return this.transform(this.current);
    }

    protected OUT transform(IN in) {
        return (OUT) in;
    }

    @Override
    public void remove() {
        if (this.current != null) {
            this.iterator.remove();
        }
    }

    private void advance() {
        this.current = null;
        while (this.iterator.hasNext()) {
            final IN next = this.iterator.next();
            if (this.validate(next)) {
                this.current = next;
                break;
            }
        }
    }

    protected boolean validate(IN value) {
        return true;
    }
}
