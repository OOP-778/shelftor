package dev.oop778.shelftor.api.index.comparison;

public interface ComparisonPolicy<T> {
  boolean supports(Class<?> clazz);

  T createComparable(T item);
}
