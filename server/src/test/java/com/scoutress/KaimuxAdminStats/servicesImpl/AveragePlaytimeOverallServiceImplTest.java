package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.AveragePlaytimeOverallServiceImpl;

class AveragePlaytimeOverallServiceImplTest {

  @Mock
  private AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;

  @Mock
  private DailyPlaytimeRepository dailyPlaytimeRepository;

  @Mock
  private EmployeeCodesRepository employeeCodesRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private AveragePlaytimeOverallServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldSkipWhenNoPlaytimeData() {
    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of());

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of());

    when(employeeRepository
        .findAll())
        .thenReturn(List.of());

    service.handleAveragePlaytime();
    verify(averagePlaytimeOverallRepository, never()).save(any());
  }

  @Test
  void shouldSkipInvalidEmployeesWithoutCode() {
    DailyPlaytime dp = new DailyPlaytime();

    dp.setEmployeeId((short) 1);
    dp.setDate(LocalDate.now());
    dp.setTimeInHours(5.0);

    Employee emp = new Employee();

    emp.setId((short) 1);
    emp.setJoinDate(LocalDate.now().minusDays(10));

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(dp));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of());

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    service.handleAveragePlaytime();
    verify(averagePlaytimeOverallRepository, never()).save(any());
  }

  @Test
  void shouldSkipEmployeesWithoutJoinDate() {
    DailyPlaytime dp = new DailyPlaytime();

    dp.setEmployeeId((short) 1);
    dp.setDate(LocalDate.now());
    dp.setTimeInHours(10.0);

    Employee emp = new Employee();

    emp.setId((short) 1);
    emp.setJoinDate(null);

    EmployeeCodes code = new EmployeeCodes();

    code.setEmployeeId((short) 1);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(dp));

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of(code));

    service.handleAveragePlaytime();
    verify(averagePlaytimeOverallRepository, never()).save(any());
  }

  @Test
  void shouldCalculateAndSaveAveragePlaytime() {
    LocalDate today = LocalDate.now();
    Employee emp = new Employee();

    emp.setId((short) 1);
    emp.setJoinDate(today.minusDays(10));

    EmployeeCodes code = new EmployeeCodes();

    code.setEmployeeId((short) 1);

    DailyPlaytime d1 = new DailyPlaytime();

    d1.setEmployeeId((short) 1);
    d1.setDate(today.minusDays(5));
    d1.setTimeInHours(10.0);

    DailyPlaytime d2 = new DailyPlaytime();

    d2.setEmployeeId((short) 1);
    d2.setDate(today.minusDays(2));
    d2.setTimeInHours(6.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(d1, d2));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of(code));

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    when(averagePlaytimeOverallRepository.findByEmployeeId(any())).thenReturn(null);

    service.handleAveragePlaytime();

    ArgumentCaptor<AveragePlaytimeOverall> captor = ArgumentCaptor.forClass(AveragePlaytimeOverall.class);
    verify(averagePlaytimeOverallRepository).save(captor.capture());

    AveragePlaytimeOverall saved = captor.getValue();
    assertEquals((short) 1, saved.getEmployeeId());
    assertTrue(saved.getPlaytime() > 0);
  }

  @Test
  void shouldUpdateExistingAveragePlaytime() {
    LocalDate today = LocalDate.now();
    Employee emp = new Employee();

    emp.setId((short) 1);
    emp.setJoinDate(today.minusDays(5));

    EmployeeCodes code = new EmployeeCodes();

    code.setEmployeeId((short) 1);

    DailyPlaytime dp = new DailyPlaytime();

    dp.setEmployeeId((short) 1);
    dp.setDate(today.minusDays(1));
    dp.setTimeInHours(5.0);

    AveragePlaytimeOverall existing = new AveragePlaytimeOverall();

    existing.setEmployeeId((short) 1);
    existing.setPlaytime(1.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(dp));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of(code));

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    when(averagePlaytimeOverallRepository
        .findByEmployeeId((short) 1))
        .thenReturn(existing);

    service.handleAveragePlaytime();

    ArgumentCaptor<AveragePlaytimeOverall> captor = ArgumentCaptor.forClass(AveragePlaytimeOverall.class);
    verify(averagePlaytimeOverallRepository).save(captor.capture());

    assertTrue(captor.getValue().getPlaytime() > 1.0);
  }

  @Test
  void shouldHandleRepositoryErrorGracefully() {
    LocalDate today = LocalDate.now();
    Employee emp = new Employee();

    emp.setId((short) 1);
    emp.setJoinDate(today.minusDays(2));

    EmployeeCodes code = new EmployeeCodes();

    code.setEmployeeId((short) 1);

    DailyPlaytime dp = new DailyPlaytime();

    dp.setEmployeeId((short) 1);
    dp.setDate(today.minusDays(1));
    dp.setTimeInHours(5.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(dp));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of(code));

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    when(averagePlaytimeOverallRepository.findByEmployeeId(any())).thenReturn(null);
    doThrow(new RuntimeException("DB error")).when(averagePlaytimeOverallRepository).save(any());

    assertDoesNotThrow(() -> service.handleAveragePlaytime());
  }

  @Test
  void shouldProcessMultipleEmployeesSortedById() {
    LocalDate today = LocalDate.now();

    Employee e1 = new Employee();

    e1.setId((short) 2);
    e1.setJoinDate(today.minusDays(5));

    Employee e2 = new Employee();

    e2.setId((short) 1);
    e2.setJoinDate(today.minusDays(5));

    EmployeeCodes c1 = new EmployeeCodes();
    c1.setEmployeeId((short) 1);

    EmployeeCodes c2 = new EmployeeCodes();
    c2.setEmployeeId((short) 2);

    DailyPlaytime d1 = new DailyPlaytime();
    d1.setEmployeeId((short) 2);
    d1.setDate(today.minusDays(2));
    d1.setTimeInHours(2.0);

    DailyPlaytime d2 = new DailyPlaytime();
    d2.setEmployeeId((short) 1);
    d2.setDate(today.minusDays(1));
    d2.setTimeInHours(3.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(d1, d2));

    when(employeeCodesRepository
        .findAll())
        .thenReturn(List.of(c1, c2));

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(e1, e2));

    when(averagePlaytimeOverallRepository
        .findByEmployeeId(any()))
        .thenReturn(null);

    service.handleAveragePlaytime();

    ArgumentCaptor<AveragePlaytimeOverall> captor = ArgumentCaptor.forClass(AveragePlaytimeOverall.class);
    verify(averagePlaytimeOverallRepository, times(2)).save(captor.capture());

    List<AveragePlaytimeOverall> saved = captor.getAllValues();
    assertTrue(saved.get(0).getEmployeeId() < saved.get(1).getEmployeeId());
  }
}
