package net.manga.api.index.comparison;

public class DefaultComparisonPolicy<V> implements ComparisonPolicy<V> {

    @Override
    public boolean supports(final Class<?> clazz) {
        return Comparable.class.isAssignableFrom(clazz);
    }

    @Override
    public V createComparable(final V item) {
        return item;
    }
}
