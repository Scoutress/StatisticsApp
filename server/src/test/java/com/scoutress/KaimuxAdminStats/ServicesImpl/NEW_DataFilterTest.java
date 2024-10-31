package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataFilterService;

public class NEW_DataFilterTest {

  @Test
  public void testSessionsFilterByAid() {
    long time = 1698578400;

    NEW_DataFilterService dataFilterService = new NEW_DataFilterService();

    List<NEW_SessionDataItem> dataList = Arrays.asList(
        new NEW_SessionDataItem((long) 1, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 2, (short) 20, time, true, "survival"),
        new NEW_SessionDataItem((long) 3, (short) 10, time, false, "survival"),
        new NEW_SessionDataItem((long) 4, (short) 20, time, false, "survival"),
        new NEW_SessionDataItem((long) 5, (short) 15, time, true, "survival"),
        new NEW_SessionDataItem((long) 6, (short) 15, time, false, "survival"),
        new NEW_SessionDataItem((long) 7, (short) 75, time, true, "survival"),
        new NEW_SessionDataItem((long) 8, (short) 75, time, false, "survival"),
        new NEW_SessionDataItem((long) 9, (short) 20, time, true, "survival"),
        new NEW_SessionDataItem((long) 10, (short) 100, time, true, "survival"));

    List<NEW_SessionDataItem> expected = Arrays.asList(
        new NEW_SessionDataItem((long) 1, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 3, (short) 10, time, false, "survival"));

    List<NEW_SessionDataItem> result = dataFilterService.sessionsFilterByAid(dataList, (short) 10);

    assertEquals(expected, result);
  }

  @Test
  public void testSessionsFilterByAction() {
    long time = 1698578400;

    NEW_DataFilterService dataFilterService = new NEW_DataFilterService();

    List<NEW_SessionDataItem> dataList = Arrays.asList(
        new NEW_SessionDataItem((long) 1, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 2, (short) 10, time, false, "survival"),
        new NEW_SessionDataItem((long) 3, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 4, (short) 10, time, false, "survival"),
        new NEW_SessionDataItem((long) 5, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 6, (short) 10, time, false, "survival"),
        new NEW_SessionDataItem((long) 7, (short) 75, time, true, "survival"),
        new NEW_SessionDataItem((long) 8, (short) 10, time, false, "survival"),
        new NEW_SessionDataItem((long) 9, (short) 10, time, true, "skyblock"),
        new NEW_SessionDataItem((long) 10, (short) 20, time, false, "survival"));

    List<NEW_SessionDataItem> adminSessions = dataFilterService.sessionsFilterByAid(dataList, (short) 10);

    List<NEW_SessionDataItem> expectedLogins = Arrays.asList(
        new NEW_SessionDataItem((long) 1, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 3, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 5, (short) 10, time, true, "survival"),
        new NEW_SessionDataItem((long) 9, (short) 10, time, true, "skyblock"));
    List<NEW_SessionDataItem> actualLogins = dataFilterService.filterByAction(adminSessions, true);

    assertEquals(expectedLogins, actualLogins);
  }
}
