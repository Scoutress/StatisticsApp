package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.servicesImpl.DataFilterServiceImpl;

public class DataFilterTest {

  @Test
  public void testSessionsFilterByAid() {
    long time = 1698578400;

    DataFilterServiceImpl dataFilterService = new DataFilterServiceImpl();

    List<SanitizedSessionData> dataList = Arrays.asList(
        new SanitizedSessionData((long) 1, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 2, (short) 20, time, true, "survival"),
        new SanitizedSessionData((long) 3, (short) 10, time, false, "survival"),
        new SanitizedSessionData((long) 4, (short) 20, time, false, "survival"),
        new SanitizedSessionData((long) 5, (short) 15, time, true, "survival"),
        new SanitizedSessionData((long) 6, (short) 15, time, false, "survival"),
        new SanitizedSessionData((long) 7, (short) 75, time, true, "survival"),
        new SanitizedSessionData((long) 8, (short) 75, time, false, "survival"),
        new SanitizedSessionData((long) 9, (short) 20, time, true, "survival"),
        new SanitizedSessionData((long) 10, (short) 100, time, true, "survival"));

    List<SanitizedSessionData> expected = Arrays.asList(
        new SanitizedSessionData((long) 1, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 3, (short) 10, time, false, "survival"));

    List<SanitizedSessionData> result = dataFilterService.filterSanitizedSessionsByAid(dataList, (short) 10);

    assertEquals(expected, result);
  }

  @Test
  public void testSessionsFilterByAction() {
    long time = 1698578400;

    DataFilterServiceImpl dataFilterService = new DataFilterServiceImpl();

    List<SanitizedSessionData> dataList = Arrays.asList(
        new SanitizedSessionData((long) 1, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 2, (short) 10, time, false, "survival"),
        new SanitizedSessionData((long) 3, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 4, (short) 10, time, false, "survival"),
        new SanitizedSessionData((long) 5, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 6, (short) 10, time, false, "survival"),
        new SanitizedSessionData((long) 7, (short) 75, time, true, "survival"),
        new SanitizedSessionData((long) 8, (short) 10, time, false, "survival"),
        new SanitizedSessionData((long) 9, (short) 10, time, true, "skyblock"),
        new SanitizedSessionData((long) 10, (short) 20, time, false, "survival"));

    List<SanitizedSessionData> adminSessions = dataFilterService.filterSanitizedSessionsByAid(dataList, (short) 10);

    List<SanitizedSessionData> expectedLogins = Arrays.asList(
        new SanitizedSessionData((long) 1, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 3, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 5, (short) 10, time, true, "survival"),
        new SanitizedSessionData((long) 9, (short) 10, time, true, "skyblock"));
    List<SanitizedSessionData> actualLogins = dataFilterService.filterSanitizedSessionsByAction(adminSessions,
        true);

    assertEquals(expectedLogins, actualLogins);
  }
}
