package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;
import com.scoutress.KaimuxAdminStats.Services.DataFilter;

public class NEW_DataFilterTest {

  @Test
  public void testSessionsFilterByAid() {
    long time = 1698578400;

    List<DataItem> dataList = Arrays.asList(
        new DataItem(1, (short) 10, time, true),
        new DataItem(2, (short) 20, time, false),
        new DataItem(3, (short) 10, time, true));

    List<DataItem> expected = Arrays.asList(
        new DataItem(1, (short) 10, time, true),
        new DataItem(3, (short) 10, time, true));

    List<DataItem> result = DataFilter.sessionsFilterByAid(dataList, (short) 10);

    assertEquals(expected, result);
  }

  @Test
  public void testSessionsFilterByAction() {
    long time = 1698578400;

    List<DataItem> dataList = Arrays.asList(
        new DataItem(1, (short) 10, time, true),
        new DataItem(2, (short) 10, time, false),
        new DataItem(3, (short) 10, time, true),
        new DataItem(4, (short) 20, time, false));

    List<DataItem> adminSessions = DataFilter.sessionsFilterByAid(dataList, (short) 10);

    List<DataItem> expectedLogins = Arrays.asList(
        new DataItem(1, (short) 10, time, true),
        new DataItem(3, (short) 10, time, true));
    List<DataItem> actualLogins = DataFilter.filterByAction(adminSessions, true);

    assertEquals(expectedLogins, actualLogins);
  }
}
