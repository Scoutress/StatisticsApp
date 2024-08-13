package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.Complains;
import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
import com.scoutress.KaimuxAdminStats.Servicesimpl.ComplainsServiceImpl;

class ComplainsServiceImplTest {

  @Mock
  private ComplainsRepository complainsRepository;

  @InjectMocks
  private ComplainsServiceImpl complainsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void getAllComplains_ShouldReturnAllComplains_WhenComplainsExist() {
    Complains complain1 = new Complains(1L, 101, LocalDate.of(2023, 8, 13), 25.5);
    Complains complain2 = new Complains(2L, 102, LocalDate.of(2023, 8, 14), 30.0);
    List<Complains> complainsList = Arrays.asList(complain1, complain2);

    System.out.println("Before mocking: " + complainsList.size());
    when(complainsRepository.findAll()).thenReturn(complainsList);
    System.out.println("After mocking");

    List<Complains> result = complainsService.getAllComplains();
    System.out.println("Result size: " + result.size());

    assertEquals(2, result.size());
  }

  @Test
  void getAllComplains_ShouldReturnEmptyList_WhenNoComplainsExist() {
    when(complainsRepository.findAll()).thenReturn(Collections.emptyList());

    List<Complains> result = complainsService.getAllComplains();

    assertTrue(result.isEmpty());
  }

  @Test
  void getAllComplains_ShouldHandleLargeNumberOfComplains() {
    Complains complain1 = new Complains(1L, 101, LocalDate.of(2023, 8, 13), 25.5);
    Complains complain2 = new Complains(2L, 102, LocalDate.of(2023, 8, 14), 30.0);
    Complains complain3 = new Complains(3L, 103, LocalDate.of(2023, 8, 15), 35.0);
    List<Complains> complainsList = Arrays.asList(complain1, complain2, complain3);

    when(complainsRepository.findAll()).thenReturn(complainsList);

    List<Complains> result = complainsService.getAllComplains();

    assertEquals(3, result.size());
  }

  @Test
  void getAllComplains_ShouldReturnCorrectData_WhenComplainsExist() {
    Complains complain = new Complains(1L, 101, LocalDate.of(2023, 8, 13), 25.5);

    when(complainsRepository.findAll()).thenReturn(Collections.singletonList(complain));

    List<Complains> result = complainsService.getAllComplains();

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getId());
    assertEquals(101, result.get(0).getEmployeeId());
    assertEquals(LocalDate.of(2023, 8, 13), result.get(0).getDate());
    assertEquals(25.5, result.get(0).getComplainsCount());
  }
}
