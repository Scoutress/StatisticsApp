// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
// import com.scoutress.KaimuxAdminStats.Entity.Productivity;
// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
// import com.scoutress.KaimuxAdminStats.Servicesimpl.EmployeeServiceImpl;

// public class EmployeeServiceImplTest {

// @Mock
// private EmployeeRepository employeeRepository;

// @Mock
// private ProductivityRepository productivityRepository;

// @InjectMocks
// private EmployeeServiceImpl employeeServiceImpl;

// @BeforeEach
// public void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// public void testFindAll_ReturnsEmployees() {
// Employee employee1 = new Employee(1, "user1", "Level 1", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// Employee employee2 = new Employee(2, "user2", "Level 2", "Spanish", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.of(2019, 5, 15));
// when(employeeRepository.findAllByOrderByLevel()).thenReturn(Arrays.asList(employee1,
// employee2));

// List<Employee> employees = employeeServiceImpl.findAll();

// assertNotNull(employees);
// assertEquals(2, employees.size());
// assertEquals(employee1, employees.get(0));
// assertEquals(employee2, employees.get(1));
// verify(employeeRepository, times(1)).findAllByOrderByLevel();
// }

// @Test
// public void testFindAll_ReturnsEmptyList() {
// when(employeeRepository.findAllByOrderByLevel()).thenReturn(Collections.emptyList());

// List<Employee> employees = employeeServiceImpl.findAll();

// assertNotNull(employees);
// assertTrue(employees.isEmpty());
// verify(employeeRepository, times(1)).findAllByOrderByLevel();
// }

// @Test
// public void testSave_Employee_Success() {
// Employee employee = new Employee(null, "user1", "Level 1", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// Employee savedEmployee = new Employee(1, "user1", "Level 1", "English",
// "John", "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// when(employeeRepository.save(employee)).thenReturn(savedEmployee);

// Employee result = employeeServiceImpl.save(employee);

// assertNotNull(result);
// assertEquals(savedEmployee, result);
// verify(employeeRepository, times(1)).save(employee);
// verify(productivityRepository, times(1)).save(any(Productivity.class));
// }

// @Test
// public void testSave_Employee_RepositoryThrowsException() {
// Employee employee = new Employee(null, "user1", "Level 1", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// when(employeeRepository.save(employee)).thenThrow(new
// RuntimeException("Database error"));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// employeeServiceImpl.save(employee);
// });

// assertEquals("Database error", exception.getMessage());
// verify(employeeRepository, times(1)).save(employee);
// verify(productivityRepository, times(0)).save(any(Productivity.class));
// }

// @Test
// public void testSave_ProductivityRepositoryThrowsException() {
// Employee employee = new Employee(null, "user1", "Level 1", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// Employee savedEmployee = new Employee(1, "user1", "Level 1", "English",
// "John", "Doe", "john.doe@example.com",
// LocalDate.of(2020, 1, 1));
// when(employeeRepository.save(employee)).thenReturn(savedEmployee);
// when(productivityRepository.save(any(Productivity.class))).thenThrow(new
// RuntimeException("Database error"));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// employeeServiceImpl.save(employee);
// });

// assertEquals("Database error", exception.getMessage());
// verify(employeeRepository, times(1)).save(employee);
// verify(productivityRepository, times(1)).save(any(Productivity.class));
// }
// }
