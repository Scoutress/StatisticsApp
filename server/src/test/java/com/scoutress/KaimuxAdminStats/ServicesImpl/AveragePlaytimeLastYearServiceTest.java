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

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeLastYear;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeLastYearRepository;
import com.scoutress.KaimuxAdminStats.servicesimpl.DataExtractingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesimpl.playtime.AveragePlaytimeLastYearServiceImpl;

class AveragePlaytimeLastYearServiceTest {

  @InjectMocks
  private AveragePlaytimeLastYearServiceImpl service;

  @Mock
  private DataExtractingServiceImpl dataExtractingService;

  @Mock
  private AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testHandleAveragePlaytime() {
    // Arrange
    List<DailyPlaytime> dailyPlaytimes = Arrays.asList(
        new DailyPlaytime(1L, (short) 1, 60.0, LocalDate.now(), "note"),
        new DailyPlaytime(2L, (short) 1, 120.0, LocalDate.now().minusDays(1), "note"));

    List<EmployeeCodes> employeeCodes = Arrays.asList(
        new EmployeeCodes(1L, (short) 1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0,
            (short) 0, (short) 0, (short) 0));

    List<Employee> employees = Arrays.asList(
        new Employee((short) 1, "John Doe", "Department", "Position", "Email", "Phone", LocalDate.now()));

    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(dailyPlaytimes);
    when(dataExtractingService.getAllEmployeeCodes()).thenReturn(employeeCodes);
    when(dataExtractingService.getAllEmployees()).thenReturn(employees);

    // Act
    service.handleAveragePlaytime();

    // Assert
    verify(averagePlaytimeLastYearRepository, times(1)).save(any());
  }

  @Test
  void testCalculateAveragePlaytime() {
    // Arrange
    List<DailyPlaytime> dailyPlaytimes = Arrays.asList(
        new DailyPlaytime(1L, (short) 1, 60.0, LocalDate.now(), "note"), // First instance with a String
        new DailyPlaytime(2L, (short) 1, 120.0, LocalDate.now().minusDays(1), "note") // Include a String for the
    // second instance
    );

    List<EmployeeCodes> employeeCodes = Arrays.asList(
        new EmployeeCodes(1L, (short) 1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0,
            (short) 0, (short) 0, (short) 0));

    List<Employee> employees = Arrays.asList(
        new Employee((short) 1, "John Doe", "Department", "Position", "Email", "Phone", LocalDate.now()));

    // Act
    List<AveragePlaytimeLastYear> averagePlaytimes = service.calculateAveragePlaytime(dailyPlaytimes, employeeCodes,
        employees);

    // Assert
    assertEquals(1, averagePlaytimes.size());
    assertEquals((short) 1, averagePlaytimes.get(0).getAid());
    assertEquals((60 + 120) / 365.0, averagePlaytimes.get(0).getPlaytime());
  }

  @Test
  void testSaveAveragePlaytime() {
    // Arrange
    AveragePlaytimeLastYear averagePlaytime = new AveragePlaytimeLastYear();
    averagePlaytime.setAid((short) 1);
    averagePlaytime.setPlaytime(100.0);
    List<AveragePlaytimeLastYear> averagePlaytimeList = Arrays.asList(averagePlaytime);

    // Act
    service.saveAveragePlaytime(averagePlaytimeList);

    // Assert
    verify(averagePlaytimeLastYearRepository, times(1)).save(averagePlaytime);
  }
}
