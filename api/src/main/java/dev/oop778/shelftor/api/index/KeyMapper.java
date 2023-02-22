package dev.oop778.shelftor.api.index;

@FunctionalInterface
public interface KeyMapper<K, V> {
  K map(V value);
}
