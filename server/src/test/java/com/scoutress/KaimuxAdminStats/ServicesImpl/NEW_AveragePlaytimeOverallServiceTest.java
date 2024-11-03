package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_Employee;
import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;
import com.scoutress.KaimuxAdminStats.Services.playtime.NEW_AveragePlaytimeOverallService;

public class NEW_AveragePlaytimeOverallServiceTest {

  @Mock
  private NEW_DataExtractingService dataExtractingService;

  @Mock
  private NEW_AveragePlaytimeOverallRepository averagePlaytimeRepository;

  @InjectMocks
  private NEW_AveragePlaytimeOverallService averagePlaytimeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testCalculateAveragePlaytime() {
    List<NEW_DailyPlaytime> playtimes = Arrays.asList(
        new NEW_DailyPlaytime(1L, (short) 1, 120, LocalDate.now().minusDays(10), "Test"),
        new NEW_DailyPlaytime(2L, (short) 1, 180, LocalDate.now().minusDays(9), "Test"));
    List<NEW_EmployeeCodes> employeeAids = Arrays.asList(
        new NEW_EmployeeCodes(1L, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1,
            (short) 1, (short) 1, (short) 1));
    List<NEW_Employee> employees = Arrays.asList(
        new NEW_Employee((short) 1, "firstName", "lastName", "email", "phone", "role", LocalDate.now().minusDays(11)));

    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(playtimes);
    when(dataExtractingService.getAidsFromEmployeeCodes()).thenReturn(employeeAids);
    when(dataExtractingService.getAllEmployees()).thenReturn(employees);

    List<NEW_AveragePlaytimeOverall> result = averagePlaytimeService.calculateAveragePlaytime(playtimes, employeeAids,
        employees);

    assertEquals(1, result.size());
    assertEquals(25.0, result.get(0).getPlaytime(), 0.001);
  }

  @Test
  void testSaveAveragePlaytime() {
    NEW_AveragePlaytimeOverall averagePlaytime = new NEW_AveragePlaytimeOverall(1L, (short) 1, 150.0);
    List<NEW_AveragePlaytimeOverall> averagePlaytimeList = Arrays.asList(averagePlaytime);

    averagePlaytimeService.saveAveragePlaytime(averagePlaytimeList);

    verify(averagePlaytimeRepository, times(1)).save(averagePlaytime);
  }
}
