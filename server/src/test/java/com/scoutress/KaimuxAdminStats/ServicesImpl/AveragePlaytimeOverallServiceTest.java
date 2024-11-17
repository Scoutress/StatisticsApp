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

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.DataExtractingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.AveragePlaytimeOverallServiceImpl;

public class AveragePlaytimeOverallServiceTest {

  @Mock
  private DataExtractingServiceImpl dataExtractingService;

  @Mock
  private AveragePlaytimeOverallRepository averagePlaytimeRepository;

  @InjectMocks
  private AveragePlaytimeOverallServiceImpl averagePlaytimeService;

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
    List<DailyPlaytime> playtimes = Arrays.asList(
        new DailyPlaytime(1L, (short) 1, 120.0, LocalDate.now().minusDays(10), "Test"),
        new DailyPlaytime(2L, (short) 1, 180.0, LocalDate.now().minusDays(9), "Test"));
    List<EmployeeCodes> employeeAids = Arrays.asList(
        new EmployeeCodes(1L, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1,
            (short) 1, (short) 1, (short) 1));
    List<Employee> employees = Arrays.asList(
        new Employee((short) 1, "firstName", "lastName", "email", "phone", "role", LocalDate.now().minusDays(11)));

    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(playtimes);
    when(dataExtractingService.getAllEmployeeCodes()).thenReturn(employeeAids);
    when(dataExtractingService.getAllEmployees()).thenReturn(employees);

    List<AveragePlaytimeOverall> result = averagePlaytimeService.calculateAveragePlaytime(playtimes, employeeAids,
        employees);

    assertEquals(1, result.size());
    assertEquals(25.0, result.get(0).getPlaytime(), 0.001);
  }

  @Test
  void testSaveAveragePlaytime() {
    AveragePlaytimeOverall averagePlaytime = new AveragePlaytimeOverall(1L, (short) 1, 150.0);
    List<AveragePlaytimeOverall> averagePlaytimeList = Arrays.asList(averagePlaytime);

    averagePlaytimeService.saveAveragePlaytime(averagePlaytimeList);

    verify(averagePlaytimeRepository, times(1)).save(averagePlaytime);
  }
}
