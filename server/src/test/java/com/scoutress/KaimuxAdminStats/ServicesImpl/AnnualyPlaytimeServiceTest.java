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

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.servicesimpl.DataExtractingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesimpl.playtime.AnnualyPlaytimeServiceImpl;

class AnnualyPlaytimeServiceTest {

  @Mock
  private DataExtractingServiceImpl dataExtractingService;

  @Mock
  private AnnualPlaytimeRepository annualPlaytimeRepository;

  @InjectMocks
  private AnnualyPlaytimeServiceImpl annualyPlaytimeService;

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

    DailyPlaytime playtime1 = new DailyPlaytime(null, (short) 1, 1000.0, dateWithinLastYear, "Server1");
    DailyPlaytime playtime2 = new DailyPlaytime(null, (short) 1, 2000.0, dateWithinLastYear, "Server1");
    DailyPlaytime playtime3 = new DailyPlaytime(null, (short) 1, 1500.0, dateOlderThanOneYear, "Server1");

    List<DailyPlaytime> dailyPlaytimes = Arrays.asList(playtime1, playtime2, playtime3);

    // Call the method to test
    List<AnnualPlaytime> result = annualyPlaytimeService.calculateAnnualPlaytime(dailyPlaytimes);

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

    DailyPlaytime playtime1 = new DailyPlaytime(null, (short) 1, 1000.0, dateWithinLastYear, "Server1");
    DailyPlaytime playtime2 = new DailyPlaytime(null, (short) 2, 2000.0, dateWithinLastYear, "Server2");

    List<DailyPlaytime> dailyPlaytimes = Arrays.asList(playtime1, playtime2);
    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(dailyPlaytimes);

    // Call the method to test
    annualyPlaytimeService.handleAnnualPlaytime();

    // Capture and verify the saved data
    verify(dataExtractingService, times(1)).getDailyPlaytimeData();
    verify(annualPlaytimeRepository, times(2)).save(any(AnnualPlaytime.class));
  }
}
