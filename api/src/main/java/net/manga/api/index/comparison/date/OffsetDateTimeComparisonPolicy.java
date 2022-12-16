package net.manga.api.index.comparison.date;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import net.manga.api.index.comparison.ComparisonPolicy;

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
