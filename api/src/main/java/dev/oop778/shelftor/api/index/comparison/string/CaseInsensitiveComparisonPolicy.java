package dev.oop778.shelftor.api.index.comparison.string;

import dev.oop778.shelftor.api.index.comparison.ComparisonPolicy;
import java.util.Locale;

public class CaseInsensitiveComparisonPolicy implements ComparisonPolicy<String> {

  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz == String.class;
  }

  @Override
  public String createComparable(final String item) {
    return item.toLowerCase(Locale.getDefault());
  }
}
