package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.RecommendationsServiceImpl;

class RecommendationsServiceImplTest {

  @Mock
  private ProductivityRepository productivityRepository;

  @Mock
  private AnnualPlaytimeRepository annualPlaytimeRepository;

  @Mock
  private EmployeeRepository employeeRepository;

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

  @Test
  void testGetAllAnnualPlaytimeData() {
    List<AnnualPlaytime> mockData = List.of(
        new AnnualPlaytime(1L, (short) 1, 1.5),
        new AnnualPlaytime(2L, (short) 2, 3.0),
        new AnnualPlaytime(3L, (short) 3, 4.5),
        new AnnualPlaytime(4L, (short) 4, 6.0),
        new AnnualPlaytime(5L, (short) 5, 7.5),
        new AnnualPlaytime(6L, (short) 6, 9.0),
        new AnnualPlaytime(7L, (short) 7, 10.5),
        new AnnualPlaytime(8L, (short) 8, 12.0),
        new AnnualPlaytime(9L, (short) 9, 13.5),
        new AnnualPlaytime(10L, (short) 10, 15.0),
        new AnnualPlaytime(11L, (short) 11, 16.5),
        new AnnualPlaytime(12L, (short) 12, 18.0),
        new AnnualPlaytime(13L, (short) 13, 19.5),
        new AnnualPlaytime(14L, (short) 14, 21.0),
        new AnnualPlaytime(15L, (short) 15, 22.5),
        new AnnualPlaytime(16L, (short) 16, 24.0),
        new AnnualPlaytime(17L, (short) 17, 25.5),
        new AnnualPlaytime(18L, (short) 18, 27.0),
        new AnnualPlaytime(19L, (short) 19, 28.5),
        new AnnualPlaytime(20L, (short) 20, 30.0));

    when(annualPlaytimeRepository.findAll()).thenReturn(mockData);

    List<AnnualPlaytime> result = recommendationsServiceImpl.getAllAnnualPlaytimeData();

    assertEquals(20, result.size());
    assertEquals((short) 1, result.get(0).getEmployeeId());
    assertEquals(1.5, result.get(0).getPlaytime());
    assertEquals((short) 20, result.get(19).getEmployeeId());
    assertEquals(30.0, result.get(19).getPlaytime());
    verify(annualPlaytimeRepository, times(1)).findAll();
  }

  @Test
  void testGetAllEmployeesData() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"),
        new Employee((short) 2, "username2", "EN", "first_name2", "last_name2", "email2@example.com",
            LocalDate.of(2025, 2, 1), "manager"),
        new Employee((short) 3, "username3", "LT", "first_name3", "last_name3", "email3@example.com",
            LocalDate.of(2025, 3, 1), "helper"),
        new Employee((short) 4, "username4", "EN", "first_name4", "last_name4", "email4@example.com",
            LocalDate.of(2025, 4, 1), "support"),
        new Employee((short) 5, "username5", "LT", "first_name5", "last_name5", "email5@example.com",
            LocalDate.of(2025, 5, 1), "organizer"),
        new Employee((short) 6, "username6", "EN", "first_name6", "last_name6", "email6@example.com",
            LocalDate.of(2025, 6, 1), "chatmod"),
        new Employee((short) 7, "username7", "LT", "first_name7", "last_name7", "email7@example.com",
            LocalDate.of(2025, 7, 1), "support"),
        new Employee((short) 8, "username8", "EN", "first_name8", "last_name8", "email8@example.com",
            LocalDate.of(2025, 8, 1), "manager"),
        new Employee((short) 9, "username9", "LT", "first_name9", "last_name9", "email9@example.com",
            LocalDate.of(2025, 9, 1), "helper"),
        new Employee((short) 10, "username10", "EN", "first_name10", "last_name10", "email10@example.com",
            LocalDate.of(2025, 10, 1), "organizer"),
        new Employee((short) 11, "username11", "LT", "first_name11", "last_name11", "email11@example.com",
            LocalDate.of(2025, 11, 1), "support"),
        new Employee((short) 12, "username12", "EN", "first_name12", "last_name12", "email12@example.com",
            LocalDate.of(2025, 12, 1), "chatmod"),
        new Employee((short) 13, "username13", "LT", "first_name13", "last_name13", "email13@example.com",
            LocalDate.of(2026, 1, 1), "manager"),
        new Employee((short) 14, "username14", "EN", "first_name14", "last_name14", "email14@example.com",
            LocalDate.of(2026, 2, 1), "helper"),
        new Employee((short) 15, "username15", "LT", "first_name15", "last_name15", "email15@example.com",
            LocalDate.of(2026, 3, 1), "support"),
        new Employee((short) 16, "username16", "EN", "first_name16", "last_name16", "email16@example.com",
            LocalDate.of(2026, 4, 1), "organizer"),
        new Employee((short) 17, "username17", "LT", "first_name17", "last_name17", "email17@example.com",
            LocalDate.of(2026, 5, 1), "chatmod"),
        new Employee((short) 18, "username18", "EN", "first_name18", "last_name18", "email18@example.com",
            LocalDate.of(2026, 6, 1), "manager"),
        new Employee((short) 19, "username19", "LT", "first_name19", "last_name19", "email19@example.com",
            LocalDate.of(2026, 7, 1), "helper"),
        new Employee((short) 20, "username20", "EN", "first_name20", "last_name20", "email20@example.com",
            LocalDate.of(2026, 8, 1), "support"));

    when(employeeRepository.findAll()).thenReturn(mockData);

    List<Employee> result = recommendationsServiceImpl.getAllEmployeesData();

    assertEquals(20, result.size());
    assertEquals("username1", result.get(0).getUsername());
    assertEquals("LT", result.get(0).getLanguage());
    assertEquals(LocalDate.of(2025, 1, 1), result.get(0).getJoinDate());
    assertEquals("support", result.get(0).getLevel());
    assertEquals("username20", result.get(19).getUsername());
    assertEquals("EN", result.get(19).getLanguage());
    assertEquals(LocalDate.of(2026, 8, 1), result.get(19).getJoinDate());
    assertEquals("support", result.get(19).getLevel());

    verify(employeeRepository, times(1)).findAll();
  }

  @Test
  void testGetAllEmployeeIds() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"),
        new Employee((short) 2, "username2", "EN", "first_name2", "last_name2", "email2@example.com",
            LocalDate.of(2025, 2, 1), "manager"),
        new Employee((short) 1, "username3", "LT", "first_name3", "last_name3", "email3@example.com",
            LocalDate.of(2025, 3, 1), "helper"),
        new Employee((short) 3, "username4", "EN", "first_name4", "last_name4", "email4@example.com",
            LocalDate.of(2025, 4, 1), "support"),
        new Employee((short) 2, "username5", "LT", "first_name5", "last_name5", "email5@example.com",
            LocalDate.of(2025, 5, 1), "organizer"));

    List<Short> result = recommendationsServiceImpl.getAllEmployeeIds(mockData);

    assertEquals(3, result.size());
    assertEquals(List.of((short) 1, (short) 2, (short) 3), result);
    assertTrue(result.contains((short) 1));
    assertTrue(result.contains((short) 2));
    assertTrue(result.contains((short) 3));
  }

  @Test
  void testGetAllEmployeeIdsWithEmptyList() {
    List<Employee> mockData = List.of();
    List<Short> result = recommendationsServiceImpl.getAllEmployeeIds(mockData);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetAllEmployeeIdsWithSingleEntry() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"));
    List<Short> result = recommendationsServiceImpl.getAllEmployeeIds(mockData);
    assertEquals(List.of((short) 1), result);
  }

  @Test
  void testGetLevelForThisEmployee_ValidEmployee() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"),
        new Employee((short) 2, "username2", "EN", "first_name2", "last_name2", "email2@example.com",
            LocalDate.of(2025, 2, 1), "manager"));

    String result = recommendationsServiceImpl.getLevelForThisEmployee(mockData, (short) 1);

    assertEquals("support", result);
  }

  @Test
  void testGetLevelForThisEmployee_EmployeeNotFound() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"),
        new Employee((short) 2, "username2", "EN", "first_name2", "last_name2", "email2@example.com",
            LocalDate.of(2025, 2, 1), "manager"));

    String result = recommendationsServiceImpl.getLevelForThisEmployee(mockData, (short) 3);

    assertEquals("n/a", result);
  }

  @Test
  void testGetLevelForThisEmployee_NullList() {
    String result = recommendationsServiceImpl.getLevelForThisEmployee(null, (short) 1);

    assertEquals("n/a", result);
  }

  @Test
  void testGetLevelForThisEmployee_EmptyList() {
    List<Employee> mockData = List.of();

    String result = recommendationsServiceImpl.getLevelForThisEmployee(mockData, (short) 1);

    assertEquals("n/a", result);
  }

  @Test
  void testGetLevelForThisEmployee_MultipleMatchingEmployees() {
    List<Employee> mockData = List.of(
        new Employee((short) 1, "username1", "LT", "first_name1", "last_name1", "email1@example.com",
            LocalDate.of(2025, 1, 1), "support"),
        new Employee((short) 1, "username2", "EN", "first_name2", "last_name2", "email2@example.com",
            LocalDate.of(2025, 2, 1), "manager"));

    String result = recommendationsServiceImpl.getLevelForThisEmployee(mockData, (short) 1);

    assertEquals("support", result);
  }

}
