package com.scoutress.KaimuxAdminStats.ServicesImpl;

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

import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.RecommendationsServiceImpl;

class RecommendationsServiceImplTest {

  @Mock
  private ProductivityRepository productivityRepository;

  @InjectMocks
  private RecommendationsServiceImpl recommendationsServiceImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testGetAllProductivityData() {
    List<Productivity> mockData = List.of(
        new Productivity(1L, (short) 1, 1.5),
        new Productivity(2L, (short) 2, 3.0),
        new Productivity(3L, (short) 3, 4.5),
        new Productivity(4L, (short) 4, 6.0),
        new Productivity(5L, (short) 5, 7.5),
        new Productivity(6L, (short) 6, 9.0),
        new Productivity(7L, (short) 7, 10.5),
        new Productivity(8L, (short) 8, 12.0),
        new Productivity(9L, (short) 9, 13.5),
        new Productivity(10L, (short) 10, 15.0),
        new Productivity(11L, (short) 11, 16.5),
        new Productivity(12L, (short) 12, 18.0),
        new Productivity(13L, (short) 13, 19.5),
        new Productivity(14L, (short) 14, 21.0),
        new Productivity(15L, (short) 15, 22.5),
        new Productivity(16L, (short) 16, 24.0),
        new Productivity(17L, (short) 17, 25.5),
        new Productivity(18L, (short) 18, 27.0),
        new Productivity(19L, (short) 19, 28.5),
        new Productivity(20L, (short) 20, 30.0));

    when(productivityRepository.findAll()).thenReturn(mockData);

    List<Productivity> result = recommendationsServiceImpl.getAllProductivityData();

    assertEquals(20, result.size());
    assertEquals((short) 1, result.get(0).getEmployeeId());
    assertEquals(1.5, result.get(0).getValue());
    assertEquals((short) 20, result.get(19).getEmployeeId());
    assertEquals(30.0, result.get(19).getValue());
    verify(productivityRepository, times(1)).findAll();
  }
}
