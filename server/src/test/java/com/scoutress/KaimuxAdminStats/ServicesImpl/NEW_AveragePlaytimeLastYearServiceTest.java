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

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_Employee;
import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_AveragePlaytimeLastYear;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_AveragePlaytimeLastYearRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;
import com.scoutress.KaimuxAdminStats.Services.playtime.NEW_AveragePlaytimeLastYearService;

class NEW_AveragePlaytimeLastYearServiceTest {

  @InjectMocks
  private NEW_AveragePlaytimeLastYearService service;

  @Mock
  private NEW_DataExtractingService dataExtractingService;

  @Mock
  private NEW_AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository;

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
    List<NEW_DailyPlaytime> dailyPlaytimes = Arrays.asList(
        new NEW_DailyPlaytime(1L, (short) 1, 60, LocalDate.now(), "note"),
        new NEW_DailyPlaytime(2L, (short) 1, 120, LocalDate.now().minusDays(1), "note"));

    List<NEW_EmployeeCodes> employeeCodes = Arrays.asList(
        new NEW_EmployeeCodes(1L, (short) 1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0,
            (short) 0, (short) 0, (short) 0));

    List<NEW_Employee> employees = Arrays.asList(
        new NEW_Employee((short) 1, "John Doe", "Department", "Position", "Email", "Phone", LocalDate.now()));

    when(dataExtractingService.getDailyPlaytimeData()).thenReturn(dailyPlaytimes);
    when(dataExtractingService.getAidsFromEmployeeCodes()).thenReturn(employeeCodes);
    when(dataExtractingService.getAllEmployees()).thenReturn(employees);

    // Act
    service.handleAveragePlaytime();

    // Assert
    verify(averagePlaytimeLastYearRepository, times(1)).save(any());
  }

  @Test
  void testCalculateAveragePlaytime() {
    // Arrange
    List<NEW_DailyPlaytime> dailyPlaytimes = Arrays.asList(
        new NEW_DailyPlaytime(1L, (short) 1, 60, LocalDate.now(), "note"), // First instance with a String
        new NEW_DailyPlaytime(2L, (short) 1, 120, LocalDate.now().minusDays(1), "note") // Include a String for the
                                                                                        // second instance
    );

    List<NEW_EmployeeCodes> employeeCodes = Arrays.asList(
        new NEW_EmployeeCodes(1L, (short) 1, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0,
            (short) 0, (short) 0, (short) 0));

    List<NEW_Employee> employees = Arrays.asList(
        new NEW_Employee((short) 1, "John Doe", "Department", "Position", "Email", "Phone", LocalDate.now()));

    // Act
    List<NEW_AveragePlaytimeLastYear> averagePlaytimes = service.calculateAveragePlaytime(dailyPlaytimes, employeeCodes,
        employees);

    // Assert
    assertEquals(1, averagePlaytimes.size());
    assertEquals((short) 1, averagePlaytimes.get(0).getAid());
    assertEquals((60 + 120) / 365.0, averagePlaytimes.get(0).getPlaytime());
  }

  @Test
  void testSaveAveragePlaytime() {
    // Arrange
    NEW_AveragePlaytimeLastYear averagePlaytime = new NEW_AveragePlaytimeLastYear();
    averagePlaytime.setAid((short) 1);
    averagePlaytime.setPlaytime(100.0);
    List<NEW_AveragePlaytimeLastYear> averagePlaytimeList = Arrays.asList(averagePlaytime);

    // Act
    service.saveAveragePlaytime(averagePlaytimeList);

    // Assert
    verify(averagePlaytimeLastYearRepository, times(1)).save(averagePlaytime);
  }
}
