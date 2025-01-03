// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
// import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
// import
// com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
// import com.scoutress.KaimuxAdminStats.servicesImpl.DataExtractingServiceImpl;
// import
// com.scoutress.KaimuxAdminStats.servicesImpl.playtime.DailyPlaytimeServiceImpl;

// class DailyPlaytimeServiceTest {

// @Mock
// private DailyPlaytimeRepository dailyPlaytimeRepository;

// @Mock
// private DataExtractingServiceImpl dataExtractingService;

// @InjectMocks
// private DailyPlaytimeServiceImpl dailyPlaytimeService;

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// void customTestWithManualSetUpInvocation() {
// setUp();
// }

// @Test
// void testHandleDailyPlaytime() {
// SessionDuration session1 = new SessionDuration();
// session1.setAid((short) 1);
// session1.setServer("Server1");
// session1.setDate(LocalDate.of(2024, 11, 1));
// session1.setSingleSessionDuration(120);

// SessionDuration session2 = new SessionDuration();
// session2.setAid((short) 1);
// session2.setServer("Server1");
// session2.setDate(LocalDate.of(2024, 11, 1));
// session2.setSingleSessionDuration(180);

// List<SessionDuration> sessions = Arrays.asList(session1, session2);

// when(dataExtractingService.getSessionDurations()).thenReturn(sessions);

// dailyPlaytimeService.handleDailyPlaytime();

// verify(dailyPlaytimeRepository, times(1)).save(any());
// }

// @Test
// void testCalculateDailyPlaytime() {
// SessionDuration session1 = new SessionDuration();
// session1.setAid((short) 1);
// session1.setServer("Server1");
// session1.setDate(LocalDate.of(2024, 11, 1));
// session1.setSingleSessionDuration(120);

// SessionDuration session2 = new SessionDuration();
// session2.setAid((short) 1);
// session2.setServer("Server1");
// session2.setDate(LocalDate.of(2024, 11, 1));
// session2.setSingleSessionDuration(180);

// List<SessionDuration> sessions = Arrays.asList(session1, session2);

// List<DailyPlaytime> result =
// dailyPlaytimeService.calculateDailyPlaytime(sessions);

// assertEquals(1, result.size(), "There should be 1 daily playtime entry.");
// DailyPlaytime dailyPlaytime = result.get(0);
// assertEquals((short) 1, dailyPlaytime.getEmployeeId());
// assertEquals("Server1", dailyPlaytime.getServer());
// assertEquals(LocalDate.of(2024, 11, 1), dailyPlaytime.getDate());
// assertEquals(300, dailyPlaytime.getTime(), "The total playtime should be 300
// seconds.");
// }

// @Test
// void testSaveCalculatedPlaytime() {
// DailyPlaytime dailyPlaytime = new DailyPlaytime();
// dailyPlaytime.setEmployeeId((short) 1);
// dailyPlaytime.setServer("Server1");
// dailyPlaytime.setDate(LocalDate.of(2024, 11, 1));
// dailyPlaytime.setTime(300.0);

// dailyPlaytimeService.saveCalculatedPlaytime(Arrays.asList(dailyPlaytime));

// verify(dailyPlaytimeRepository, times(1)).save(dailyPlaytime);
// }
// }
