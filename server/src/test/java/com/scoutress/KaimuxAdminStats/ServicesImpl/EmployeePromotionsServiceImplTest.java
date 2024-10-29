// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import
// com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotionsPlus;
// import
// com.scoutress.KaimuxAdminStats.Repositories.EmployeePromotionsRepository;
// import
// com.scoutress.KaimuxAdminStats.Servicesimpl.EmployeePromotionsServiceImpl;

// public class EmployeePromotionsServiceImplTest {

// @Mock
// private EmployeePromotionsRepository employeePromotionsRepository;

// @InjectMocks
// private EmployeePromotionsServiceImpl employeePromotionsServiceImpl;

// @BeforeEach
// public void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// public void testGetAllEmployeePromotionsWithEmployeeData_ReturnsData() {
// EmployeePromotionsPlus employeePromotion1 = new EmployeePromotionsPlus(
// 1, "user1", "Level 1", LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1),
// LocalDate.of(2022, 1, 1),
// LocalDate.of(2023, 1, 1));
// EmployeePromotionsPlus employeePromotion2 = new EmployeePromotionsPlus(
// 2, "user2", "Level 2", LocalDate.of(2019, 5, 15), LocalDate.of(2020, 6, 15),
// LocalDate.of(2021, 7, 15),
// LocalDate.of(2022, 8, 15));
// List<EmployeePromotionsPlus> expectedPromotions =
// Arrays.asList(employeePromotion1, employeePromotion2);

// when(employeePromotionsRepository.findAllEmployeePromotionsWithEmployeeData()).thenReturn(expectedPromotions);

// List<EmployeePromotionsPlus> actualPromotions = employeePromotionsServiceImpl
// .getAllEmployeePromotionsWithEmployeeData();

// assertNotNull(actualPromotions);
// assertEquals(2, actualPromotions.size());
// assertEquals(expectedPromotions, actualPromotions);

// assertEquals(1, actualPromotions.get(0).getEmployeeId());
// assertEquals("user1", actualPromotions.get(0).getUsername());
// assertEquals(LocalDate.of(2020, 1, 1),
// actualPromotions.get(0).getToSupport());

// verify(employeePromotionsRepository,
// times(1)).findAllEmployeePromotionsWithEmployeeData();
// }

// @Test
// public void testGetAllEmployeePromotionsWithEmployeeData_ReturnsEmptyList() {
// when(employeePromotionsRepository.findAllEmployeePromotionsWithEmployeeData()).thenReturn(Arrays.asList());

// List<EmployeePromotionsPlus> actualPromotions = employeePromotionsServiceImpl
// .getAllEmployeePromotionsWithEmployeeData();

// assertNotNull(actualPromotions);
// assertTrue(actualPromotions.isEmpty());
// verify(employeePromotionsRepository,
// times(1)).findAllEmployeePromotionsWithEmployeeData();
// }

// @Test
// public void testGetAllEmployeePromotionsWithEmployeeData_ExceptionThrown() {
// when(employeePromotionsRepository.findAllEmployeePromotionsWithEmployeeData())
// .thenThrow(new RuntimeException("Database error"));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// employeePromotionsServiceImpl.getAllEmployeePromotionsWithEmployeeData();
// });
// assertEquals("Database error", exception.getMessage());
// verify(employeePromotionsRepository,
// times(1)).findAllEmployeePromotionsWithEmployeeData();
// }
// }
