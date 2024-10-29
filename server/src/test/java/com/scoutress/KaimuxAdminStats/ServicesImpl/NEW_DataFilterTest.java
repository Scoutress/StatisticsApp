package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;
import com.scoutress.KaimuxAdminStats.Services.DataFilter;

public class NEW_DataFilterTest {

  @Test
  public void testSessionsFilterByAid() {
    LocalDateTime fixedTime = LocalDateTime.of(2024, 10, 29, 12, 00);

    List<DataItem> dataList = Arrays.asList(
        new DataItem(1, (short) 10, fixedTime, true),
        new DataItem(2, (short) 20, fixedTime, false),
        new DataItem(3, (short) 10, fixedTime, true));

    List<DataItem> expected = Arrays.asList(
        new DataItem(1, (short) 10, fixedTime, true),
        new DataItem(3, (short) 10, fixedTime, true));

    List<DataItem> result = DataFilter.sessionsFilterByAid(dataList, (short) 10);

    assertEquals(expected, result);
  }
}
