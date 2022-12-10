package net.manga.core.util.collection;

public abstract class ReferencedIterator<T, U> implements java.util.Iterator<T> {
    private final java.util.Iterator<U> backingIterator;
    protected T next;

    public ReferencedIterator(java.util.Iterator<U> weakIterator) {
        this.backingIterator = weakIterator;
        this.advance();
    }

    private void advance() {
        while (this.backingIterator.hasNext()) {
            final U nextU = this.backingIterator.next();
            if ((this.next = this.extractKey(nextU)) != null) {
                return;
            }
        }
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public final T next() {
        final T next = this.next;
        this.advance();
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


    protected abstract T extractKey(U u);
}
