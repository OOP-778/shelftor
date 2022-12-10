package net.manga.api.index.comparison;

public interface ComparisonPolicy<T> {
  boolean supports(Class<?> clazz);

  T createComparable(T item);
}
