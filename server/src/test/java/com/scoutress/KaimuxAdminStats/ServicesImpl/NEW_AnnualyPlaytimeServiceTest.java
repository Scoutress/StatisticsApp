package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.NEW_AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_AnnualyPlaytimeService;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;

class NEW_AnnualyPlaytimeServiceTest {

  @Mock
  private NEW_DataExtractingService dataExtractingService;

  @Mock
  private NEW_AnnualPlaytimeRepository annualPlaytimeRepository;

  @InjectMocks
  private NEW_AnnualyPlaytimeService annualyPlaytimeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testCalculateAnnualPlaytime() {
    // Prepare test data
    LocalDate now = LocalDate.now();
    LocalDate dateWithinLastYear = now.minusMonths(6);
    LocalDate dateOlderThanOneYear = now.minusYears(2);

    NEW_DailyPlaytime playtime1 = new NEW_DailyPlaytime(null, (short) 1, 1000, dateWithinLastYear, "Server1");
    NEW_DailyPlaytime playtime2 = new NEW_DailyPlaytime(null, (short) 1, 2000, dateWithinLastYear, "Server1");
    NEW_DailyPlaytime playtime3 = new NEW_DailyPlaytime(null, (short) 1, 1500, dateOlderThanOneYear, "Server1");

    List<NEW_DailyPlaytime> dailyPlaytimes = Arrays.asList(playtime1, playtime2, playtime3);

    // Call the method to test
    List<NEW_AnnualPlaytime> result = annualyPlaytimeService.calculateAnnualPlaytime(dailyPlaytimes);

    // Verify the result
    assertEquals(1, result.size());
    assertEquals((short) 1, result.get(0).getAid());
    assertEquals(3000, result.get(0).getPlaytime());
  }

  @Test
  void testHandleAnnualPlaytime() {
    // Prepare test data
    LocalDate now = LocalDate.now();
    LocalDate dateWithinLastYear = now.minusMonths(6);

    NEW_DailyPlaytime playtime1 = new NEW_DailyPlaytime(null, (short) 1, 1000, dateWithinLastYear, "Server1");
    NEW_DailyPlaytime playtime2 = new NEW_DailyPlaytime(null, (short) 2, 2000, dateWithinLastYear, "Server2");

    List<NEW_DailyPlaytime> dailyPlaytimes = Arrays.asList(playtime1, playtime2);
    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(dailyPlaytimes);

    // Call the method to test
    annualyPlaytimeService.handleAnnualPlaytime();

    // Capture and verify the saved data
    verify(dataExtractingService, times(1)).getDailyPlaytimeData();
    verify(annualPlaytimeRepository, times(2)).save(any(NEW_AnnualPlaytime.class));
  }
}
