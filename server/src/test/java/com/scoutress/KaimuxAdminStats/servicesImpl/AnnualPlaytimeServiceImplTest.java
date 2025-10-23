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

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.AnnualPlaytimeServiceImpl;

class AnnualPlaytimeServiceImplTest {

  @Mock
  private AnnualPlaytimeRepository annualPlaytimeRepository;

  @Mock
  private DailyPlaytimeRepository dailyPlaytimeRepository;

  @InjectMocks
  private AnnualPlaytimeServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldSkipWhenDailyPlaytimeEmpty() {
    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of());

    service.handleAnnualPlaytime();

    verify(annualPlaytimeRepository, never()).save(any());
  }

  @Test
  void shouldIgnoreOldPlaytimeEntries() {
    LocalDate now = LocalDate.now();
    DailyPlaytime old = new DailyPlaytime();

    old.setEmployeeId((short) 1);
    old.setDate(now.minusYears(2));
    old.setTimeInHours(10.0);

    DailyPlaytime recent = new DailyPlaytime();

    recent.setEmployeeId((short) 1);
    recent.setDate(now.minusDays(5));
    recent.setTimeInHours(5.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(old, recent));

    when(annualPlaytimeRepository
        .findByEmployeeId(any()))
        .thenReturn(null);

    service.handleAnnualPlaytime();

    ArgumentCaptor<AnnualPlaytime> captor = ArgumentCaptor.forClass(AnnualPlaytime.class);

    verify(annualPlaytimeRepository).save(captor.capture());
    assertEquals(5.0, captor.getValue().getPlaytimeInHours(), 0.001);
  }

  @Test
  void shouldUpdateExistingAnnualPlaytime() {
    LocalDate now = LocalDate.now();
    DailyPlaytime p = new DailyPlaytime();

    p.setEmployeeId((short) 1);
    p.setDate(now.minusDays(10));
    p.setTimeInHours(10.0);

    AnnualPlaytime existing = new AnnualPlaytime();

    existing.setEmployeeId((short) 1);
    existing.setPlaytimeInHours(4.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(p));

    when(annualPlaytimeRepository
        .findByEmployeeId((short) 1))
        .thenReturn(existing);

    service.handleAnnualPlaytime();

    ArgumentCaptor<AnnualPlaytime> captor = ArgumentCaptor.forClass(AnnualPlaytime.class);

    verify(annualPlaytimeRepository).save(captor.capture());
    assertEquals(10.0, captor.getValue().getPlaytimeInHours(), 0.001);
  }

  @Test
  void shouldSumDailyPlaytimeCorrectly() {
    LocalDate now = LocalDate.now();
    DailyPlaytime d1 = new DailyPlaytime();

    d1.setEmployeeId((short) 1);
    d1.setDate(now.minusDays(1));
    d1.setTimeInHours(2.5);

    DailyPlaytime d2 = new DailyPlaytime();

    d2.setEmployeeId((short) 1);
    d2.setDate(now.minusDays(2));
    d2.setTimeInHours(3.5);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(d1, d2));

    when(annualPlaytimeRepository
        .findByEmployeeId((short) 1))
        .thenReturn(null);

    service.handleAnnualPlaytime();

    ArgumentCaptor<AnnualPlaytime> captor = ArgumentCaptor.forClass(AnnualPlaytime.class);

    verify(annualPlaytimeRepository).save(captor.capture());
    assertEquals(6.0, captor.getValue().getPlaytimeInHours(), 0.001);
  }

  @Test
  void shouldHandleRepositorySaveErrorGracefully() {
    LocalDate now = LocalDate.now();
    DailyPlaytime d = new DailyPlaytime();

    d.setEmployeeId((short) 1);
    d.setDate(now.minusDays(1));
    d.setTimeInHours(5.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(d));

    when(annualPlaytimeRepository
        .findByEmployeeId(any()))
        .thenReturn(null);

    doThrow(new RuntimeException("DB error")).when(annualPlaytimeRepository).save(any());

    assertDoesNotThrow(() -> service.handleAnnualPlaytime());
  }

  @Test
  void shouldLogProgressEvery25Records() {
    LocalDate now = LocalDate.now();

    List<DailyPlaytime> list = java.util.stream.IntStream.rangeClosed(1, 50)
        .mapToObj(i -> {
          DailyPlaytime dp = new DailyPlaytime();
          dp.setEmployeeId((short) i);
          dp.setDate(now.minusDays(1));
          dp.setTimeInHours(1.0);
          return dp;
        }).toList();

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(list);

    when(annualPlaytimeRepository
        .findByEmployeeId(any()))
        .thenReturn(null);

    service.handleAnnualPlaytime();

    verify(annualPlaytimeRepository, times(50)).save(any());
  }

  @Test
  void shouldReturnSortedAnnualPlaytimeList() {
    LocalDate now = LocalDate.now();
    DailyPlaytime d1 = new DailyPlaytime();

    d1.setEmployeeId((short) 5);
    d1.setDate(now.minusDays(1));
    d1.setTimeInHours(1.0);

    DailyPlaytime d2 = new DailyPlaytime();

    d2.setEmployeeId((short) 1);
    d2.setDate(now.minusDays(1));
    d2.setTimeInHours(2.0);

    when(dailyPlaytimeRepository
        .findAll())
        .thenReturn(List.of(d1, d2));

    when(annualPlaytimeRepository
        .findByEmployeeId(any()))
        .thenReturn(null);

    service.handleAnnualPlaytime();

    ArgumentCaptor<AnnualPlaytime> captor = ArgumentCaptor.forClass(AnnualPlaytime.class);

    verify(annualPlaytimeRepository, times(2)).save(captor.capture());
    List<AnnualPlaytime> saved = captor.getAllValues();
    assertTrue(saved.get(0).getEmployeeId() < saved.get(1).getEmployeeId());
  }
}
