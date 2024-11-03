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

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;
import com.scoutress.KaimuxAdminStats.Services.playtime.NEW_DailyPlaytimeService;

class NEW_DailyPlaytimeServiceTest {

  @Mock
  private NEW_DailyPlaytimeRepository dailyPlaytimeRepository;

  @Mock
  private NEW_DataExtractingService dataExtractingService;

  @InjectMocks
  private NEW_DailyPlaytimeService dailyPlaytimeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testHandleDailyPlaytime() {
    NEW_SessionDuration session1 = new NEW_SessionDuration();
    session1.setAid((short) 1);
    session1.setServer("Server1");
    session1.setDate(LocalDate.of(2024, 11, 1));
    session1.setSingleSessionDuration(120);

    NEW_SessionDuration session2 = new NEW_SessionDuration();
    session2.setAid((short) 1);
    session2.setServer("Server1");
    session2.setDate(LocalDate.of(2024, 11, 1));
    session2.setSingleSessionDuration(180);

    List<NEW_SessionDuration> sessions = Arrays.asList(session1, session2);

    when(dataExtractingService.getSessionDurations()).thenReturn(sessions);

    dailyPlaytimeService.handleDailyPlaytime();

    verify(dailyPlaytimeRepository, times(1)).save(any());
  }

  @Test
  void testCalculateDailyPlaytime() {
    NEW_SessionDuration session1 = new NEW_SessionDuration();
    session1.setAid((short) 1);
    session1.setServer("Server1");
    session1.setDate(LocalDate.of(2024, 11, 1));
    session1.setSingleSessionDuration(120);

    NEW_SessionDuration session2 = new NEW_SessionDuration();
    session2.setAid((short) 1);
    session2.setServer("Server1");
    session2.setDate(LocalDate.of(2024, 11, 1));
    session2.setSingleSessionDuration(180);

    List<NEW_SessionDuration> sessions = Arrays.asList(session1, session2);

    List<NEW_DailyPlaytime> result = dailyPlaytimeService.calculateDailyPlaytime(sessions);

    assertEquals(1, result.size(), "There should be 1 daily playtime entry.");
    NEW_DailyPlaytime dailyPlaytime = result.get(0);
    assertEquals((short) 1, dailyPlaytime.getAid());
    assertEquals("Server1", dailyPlaytime.getServer());
    assertEquals(LocalDate.of(2024, 11, 1), dailyPlaytime.getDate());
    assertEquals(300, dailyPlaytime.getTime(), "The total playtime should be 300 seconds.");
  }

  @Test
  void testSaveCalculatedPlaytime() {
    NEW_DailyPlaytime dailyPlaytime = new NEW_DailyPlaytime();
    dailyPlaytime.setAid((short) 1);
    dailyPlaytime.setServer("Server1");
    dailyPlaytime.setDate(LocalDate.of(2024, 11, 1));
    dailyPlaytime.setTime(300);

    dailyPlaytimeService.saveCalculatedPlaytime(Arrays.asList(dailyPlaytime));

    verify(dailyPlaytimeRepository, times(1)).save(dailyPlaytime);
  }
}
