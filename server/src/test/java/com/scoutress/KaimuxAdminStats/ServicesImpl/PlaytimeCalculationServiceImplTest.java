package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.Servicesimpl.PlaytimeCalculationServiceImpl;

public class PlaytimeCalculationServiceImplTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private LoginLogoutTimesRepository loginLogoutTimesRepository;

  @Mock
  private DailyPlaytimeRepository dailyPlaytimeRepository;

  @InjectMocks
  private PlaytimeCalculationServiceImpl playtimeCalculationService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCalculateDailyPlaytime_Success() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Arrays.asList(
        new LoginLogoutTimes(1L, 1, LocalDateTime.of(2024, 8, 10, 10, 0), LocalDateTime.of(2024, 8, 10, 12, 0),
            "Survival"));

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);
    when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1, LocalDate.of(2024, 8, 10))).thenReturn(null);

    playtimeCalculationService.calculateDailyPlaytime();

    verify(dailyPlaytimeRepository, times(1)).save(any(DailyPlaytime.class));
  }

  @Test
  public void testCalculateDailyPlaytime_MultipleDays() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Arrays.asList(
        new LoginLogoutTimes(1L, 1, LocalDateTime.of(2024, 8, 10, 10, 0), LocalDateTime.of(2024, 8, 10, 12, 0),
            "Survival"),
        new LoginLogoutTimes(2L, 1, LocalDateTime.of(2024, 8, 11, 13, 0), LocalDateTime.of(2024, 8, 11, 15, 0),
            "Survival"));

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);
    when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1, LocalDate.of(2024, 8, 10))).thenReturn(null);
    when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1, LocalDate.of(2024, 8, 11))).thenReturn(null);

    playtimeCalculationService.calculateDailyPlaytime();

    ArgumentCaptor<DailyPlaytime> captor = ArgumentCaptor.forClass(DailyPlaytime.class);
    verify(dailyPlaytimeRepository, times(2)).save(captor.capture());

    List<DailyPlaytime> savedPlaytimes = captor.getAllValues();
    assertEquals(2, savedPlaytimes.size());

    DailyPlaytime firstSave = savedPlaytimes.get(0);
    assertEquals(2.0, firstSave.getTotalSurvivalPlaytime(), "First day Survival playtime should be 2.0 hours");

    DailyPlaytime secondSave = savedPlaytimes.get(1);
    assertEquals(2.0, secondSave.getTotalSurvivalPlaytime(), "Second day Survival playtime should be 2.0 hours");
  }

  @Test
  public void testCalculateDailyPlaytime_NoLoginLogoutTimes() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Collections.emptyList();

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);

    playtimeCalculationService.calculateDailyPlaytime();

    verify(dailyPlaytimeRepository, times(0)).save(any(DailyPlaytime.class));
  }

  @Test
  public void testCalculateDailyPlaytime_MultipleServers() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Arrays.asList(
        new LoginLogoutTimes(1L, 1, LocalDateTime.of(2024, 8, 10, 10, 0), LocalDateTime.of(2024, 8, 10, 12, 0),
            "Survival"));

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);
    when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1, LocalDate.of(2024, 8, 10))).thenReturn(null);

    playtimeCalculationService.calculateDailyPlaytime();

    ArgumentCaptor<DailyPlaytime> captor = ArgumentCaptor.forClass(DailyPlaytime.class);
    verify(dailyPlaytimeRepository, times(1)).save(captor.capture());

    List<DailyPlaytime> savedPlaytimes = captor.getAllValues();
    assertEquals(1, savedPlaytimes.size());

    DailyPlaytime firstSave = savedPlaytimes.get(0);
    System.out.println("Survival Playtime: " + firstSave.getTotalSurvivalPlaytime());
    assertEquals(2.0, firstSave.getTotalSurvivalPlaytime(), "Survival playtime should be 2.0 hours");
  }

  @Test
  public void testCalculateDailyPlaytime_ExistingDailyPlaytimeUpdated() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Arrays.asList(
        new LoginLogoutTimes(1L, 1, LocalDateTime.of(2024, 8, 10, 10, 0), LocalDateTime.of(2024, 8, 10, 12, 0),
            "Survival"));

    DailyPlaytime existingPlaytime = new DailyPlaytime();
    existingPlaytime.setEmployeeId(1);
    existingPlaytime.setDate(LocalDate.of(2024, 8, 10));
    existingPlaytime.setTotalSurvivalPlaytime(1.0);

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);
    when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1, LocalDate.of(2024, 8, 10))).thenReturn(existingPlaytime);

    playtimeCalculationService.calculateDailyPlaytime();

    assertEquals(3.0, existingPlaytime.getTotalSurvivalPlaytime());
    verify(dailyPlaytimeRepository, times(1)).save(existingPlaytime);
  }

  @Test
  public void testCalculateDailyPlaytime_RepositorySaveFails() {
    Employee employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe", "john.doe@example.com",
        LocalDate.of(2020, 1, 1));
    List<Employee> employees = Collections.singletonList(employee);
    List<LoginLogoutTimes> loginLogoutTimes = Arrays.asList(
        new LoginLogoutTimes(1L, 1, LocalDateTime.of(2024, 8, 10, 10, 0), LocalDateTime.of(2024, 8, 10, 12, 0),
            "Survival"));

    when(employeeRepository.findAll()).thenReturn(employees);
    when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(loginLogoutTimes);
    when(dailyPlaytimeRepository.save(any(DailyPlaytime.class))).thenThrow(new RuntimeException("Save failed"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      playtimeCalculationService.calculateDailyPlaytime();
    });
    assertEquals("Save failed", exception.getMessage());
  }

  @Test
  public void testUpdatePlaytimeForServer_Survival() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalSurvivalPlaytime(2.0);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "Survival", 3.0);

    assertEquals(5.0, dailyPlaytime.getTotalSurvivalPlaytime());
    assertEquals(5.0, dailyPlaytime.getTotalPlaytime());
  }

  @Test
  public void testUpdatePlaytimeForServer_Skyblock() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalSkyblockPlaytime(1.0);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "Skyblock", 2.0);

    assertEquals(3.0, dailyPlaytime.getTotalSkyblockPlaytime());
    assertEquals(3.0, dailyPlaytime.getTotalPlaytime());
  }

  @Test
  public void testUpdatePlaytimeForServer_Creative() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalCreativePlaytime(4.0);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "Creative", 2.0);

    assertEquals(6.0, dailyPlaytime.getTotalCreativePlaytime());
    assertEquals(6.0, dailyPlaytime.getTotalPlaytime());
  }

  @Test
  public void testUpdatePlaytimeForServer_BoxPVP() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalBoxpvpPlaytime(3.0);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "BoxPVP", 1.0);

    System.out.println("BoxPVP Playtime after update: " + dailyPlaytime.getTotalBoxpvpPlaytime());
    assertEquals(4.0, dailyPlaytime.getTotalBoxpvpPlaytime(), "BoxPVP playtime should be 4.0 hours");
    assertEquals(4.0, dailyPlaytime.getTotalPlaytime(), "Total playtime should be 4.0 hours");
  }

  @Test
  public void testUpdatePlaytimeForServer_Prison() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalPrisonPlaytime(2.5);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "Prison", 3.5);

    assertEquals(6.0, dailyPlaytime.getTotalPrisonPlaytime());
    assertEquals(6.0, dailyPlaytime.getTotalPlaytime());
  }

  @Test
  public void testUpdatePlaytimeForServer_Events() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();
    dailyPlaytime.setTotalEventsPlaytime(0.5);

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "Events", 1.5);

    assertEquals(2.0, dailyPlaytime.getTotalEventsPlaytime());
    assertEquals(2.0, dailyPlaytime.getTotalPlaytime());
  }

  @Test
  public void testUpdatePlaytimeForServer_InvalidServer() {
    DailyPlaytime dailyPlaytime = new DailyPlaytime();

    playtimeCalculationService.updatePlaytimeForServer(dailyPlaytime, "InvalidServer", 2.0);

    assertEquals(0.0, dailyPlaytime.getTotalPlaytime());
  }
}
