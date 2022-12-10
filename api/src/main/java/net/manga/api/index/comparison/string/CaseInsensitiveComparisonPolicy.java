package net.manga.api.index.comparison.string;

import java.util.Locale;
import net.manga.api.index.comparison.ComparisonPolicy;

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
