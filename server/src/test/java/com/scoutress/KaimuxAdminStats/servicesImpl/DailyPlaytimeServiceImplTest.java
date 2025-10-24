package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.DailyPlaytimeServiceImpl;

class DailyPlaytimeServiceImplTest {

  @Mock
  private DailyPlaytimeRepository dailyPlaytimeRepository;

  @Mock
  private ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private DailyPlaytimeServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldSkipWhenNoEmployees() {
    when(employeeRepository
        .findAll())
        .thenReturn(List.of());

    service.handleDailyPlaytime();
    verify(processedPlaytimeSessionsRepository, never()).findByEmployeeId(any());
  }

  @Test
  void shouldSkipEmployeeWithNoSessions() {
    Employee emp = new Employee();
    emp.setId((short) 1);

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    when(processedPlaytimeSessionsRepository
        .findByEmployeeId((short) 1))
        .thenReturn(List.of());

    service.handleDailyPlaytime();
    verify(dailyPlaytimeRepository, never()).save(any());
  }

  @Test
  void shouldCalculateAndSaveValidPlaytime() {
    Employee emp = new Employee();
    emp.setId((short) 1);

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    SessionDuration s1 = new SessionDuration();
    s1.setEmployeeId((short) 1);
    s1.setServer("Survival");
    s1.setDate(LocalDate.of(2025, 10, 19));
    s1.setSingleSessionDurationInSec(7200);

    when(processedPlaytimeSessionsRepository
        .findByEmployeeId((short) 1))
        .thenReturn(List.of(s1));

    when(dailyPlaytimeRepository
        .findByEmployeeIdAndDateAndServer(any(), any(), any()))
        .thenReturn(null);

    service.handleDailyPlaytime();

    ArgumentCaptor<DailyPlaytime> captor = ArgumentCaptor.forClass(DailyPlaytime.class);
    verify(dailyPlaytimeRepository).save(captor.capture());

    DailyPlaytime saved = captor.getValue();
    assertEquals((short) 1, saved.getEmployeeId());
    assertEquals("survival", saved.getServer());
    assertEquals(LocalDate.of(2025, 10, 19), saved.getDate());
    assertEquals(2.0, saved.getTimeInHours(), 0.001);
  }

  @Test
  void shouldUpdateExistingRecord() {
    Employee emp = new Employee();
    emp.setId((short) 1);

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    SessionDuration s = new SessionDuration();
    s.setEmployeeId((short) 1);
    s.setServer("Survival");
    s.setDate(LocalDate.of(2025, 10, 19));
    s.setSingleSessionDurationInSec(3600);

    DailyPlaytime existing = new DailyPlaytime();
    existing.setEmployeeId((short) 1);
    existing.setServer("survival");
    existing.setDate(LocalDate.of(2025, 10, 19));
    existing.setTimeInHours(1.5);

    when(processedPlaytimeSessionsRepository
        .findByEmployeeId((short) 1))
        .thenReturn(List.of(s));

    when(dailyPlaytimeRepository
        .findByEmployeeIdAndDateAndServer(any(), any(), any()))
        .thenReturn(existing);

    service.handleDailyPlaytime();

    ArgumentCaptor<DailyPlaytime> captor = ArgumentCaptor.forClass(DailyPlaytime.class);
    verify(dailyPlaytimeRepository).save(captor.capture());
    assertEquals(1.0, captor.getValue().getTimeInHours(), 0.001);
  }

  @Test
  void shouldIgnoreZeroHourEntries() {
    Employee emp = new Employee();
    emp.setId((short) 1);

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    SessionDuration s = new SessionDuration();
    s.setEmployeeId((short) 1);
    s.setServer("Survival");
    s.setDate(LocalDate.of(2025, 10, 19));
    s.setSingleSessionDurationInSec(0);

    when(processedPlaytimeSessionsRepository
        .findByEmployeeId((short) 1))
        .thenReturn(List.of(s));

    service.handleDailyPlaytime();
    verify(dailyPlaytimeRepository, never()).save(any());
  }

  @Test
  void shouldRemoveDuplicatePlaytimes() {
    DailyPlaytime p1 = new DailyPlaytime();
    p1.setEmployeeId((short) 1);
    p1.setServer("survival");
    p1.setDate(LocalDate.of(2025, 10, 19));
    p1.setTimeInHours(5.0);

    DailyPlaytime p2 = new DailyPlaytime();
    p2.setEmployeeId((short) 1);
    p2.setServer("survival");
    p2.setDate(LocalDate.of(2025, 10, 19));
    p2.setTimeInHours(5.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(p1, p2));

    service.removeDuplicateDailyPlaytimes();
    verify(dailyPlaytimeRepository).deleteAll(anyList());
  }

  @Test
  void shouldSkipDuplicateRemovalIfEmpty() {
    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of());

    service.removeDuplicateDailyPlaytimes();
    verify(dailyPlaytimeRepository, never()).deleteAll(any());
  }

  @Test
  void shouldCalculateTotalPlaytimeForGivenDays() {
    DailyPlaytime p1 = new DailyPlaytime();
    p1.setEmployeeId((short) 1);
    p1.setDate(LocalDate.now().minusDays(1));
    p1.setTimeInHours(2.0);

    DailyPlaytime p2 = new DailyPlaytime();
    p2.setEmployeeId((short) 1);
    p2.setDate(LocalDate.now().minusDays(5));
    p2.setTimeInHours(3.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(p1, p2));

    Double result = service.getSumOfPlaytimeByEmployeeIdAndDuration((short) 1, (short) 3);
    assertEquals(2.0, result, 0.001);
  }

  @Test
  void shouldHandleRepositorySaveExceptionGracefully() {
    Employee emp = new Employee();
    emp.setId((short) 1);

    when(employeeRepository
        .findAll())
        .thenReturn(List.of(emp));

    SessionDuration s = new SessionDuration();
    s.setEmployeeId((short) 1);
    s.setServer("Survival");
    s.setDate(LocalDate.of(2025, 10, 19));
    s.setSingleSessionDurationInSec(3600);

    when(processedPlaytimeSessionsRepository
        .findByEmployeeId((short) 1))
        .thenReturn(List.of(s));

    when(dailyPlaytimeRepository
        .findByEmployeeIdAndDateAndServer(any(), any(), any()))
        .thenReturn(null);

    doThrow(new RuntimeException("DB error")).when(dailyPlaytimeRepository).save(any());

    assertDoesNotThrow(() -> service.handleDailyPlaytime());
  }

  @Test
  void shouldHandleDuplicateGroupsCorrectly() {
    List<DailyPlaytime> list = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      DailyPlaytime p = new DailyPlaytime();
      p.setEmployeeId((short) 1);
      p.setServer("survival");
      p.setDate(LocalDate.of(2025, 10, 19));
      p.setTimeInHours(2.0);
      list.add(p);
    }

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(list);

    service.removeDuplicateDailyPlaytimes();
    verify(dailyPlaytimeRepository).deleteAll(anyList());
  }
}
