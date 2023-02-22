package dev.oop778.shelftor.api.index.comparison.date;

import dev.oop778.shelftor.api.index.comparison.ComparisonPolicy;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffsetDateTimeComparisonPolicy implements ComparisonPolicy<OffsetDateTime> {

  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz == OffsetDateTime.class;
  }

  @Override
  public OffsetDateTime createComparable(final OffsetDateTime offsetDateTime) {
    return offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
  }
}
