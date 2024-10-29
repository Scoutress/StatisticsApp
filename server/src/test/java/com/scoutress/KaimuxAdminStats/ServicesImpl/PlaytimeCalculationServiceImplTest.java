// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Collections;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
// import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
// import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;
// import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.LoginLogoutTimesRepository;
// import
// com.scoutress.KaimuxAdminStats.Servicesimpl.PlaytimeCalculationServiceImpl;

// public class PlaytimeCalculationServiceImplTest {

// @Mock
// private EmployeeRepository employeeRepository;

// @Mock
// private LoginLogoutTimesRepository loginLogoutTimesRepository;

// @Mock
// private DailyPlaytimeRepository dailyPlaytimeRepository;

// @InjectMocks
// private PlaytimeCalculationServiceImpl playtimeCalculationService;

// @BeforeEach
// public void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// public void calculateDailyPlaytime_ShouldCalculatePlaytimeForEachEmployee() {
// // Given
// Employee employee = new Employee(1, "John", "Doe", "john.doe@example.com",
// "123456789", "IT", "Support",
// LocalDate.now());
// when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));

// LoginLogoutTimes loginLogoutTimes = new LoginLogoutTimes(1L, 1, "survival",
// LocalDateTime.now().minusHours(2),
// LocalDateTime.now());
// when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(Collections.singletonList(loginLogoutTimes));

// DailyPlaytime dailyPlaytime = new DailyPlaytime();
// when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1,
// loginLogoutTimes.getLoginTime().toLocalDate()))
// .thenReturn(dailyPlaytime);

// // When
// playtimeCalculationService.calculateDailyPlaytime();

// // Then
// verify(dailyPlaytimeRepository, times(1)).save(dailyPlaytime);
// assertEquals(2.0, dailyPlaytime.getTotalSurvivalPlaytime(), 0.01);
// }

// @Test
// public void calculateDailyPlaytime_ShouldHandleNullPlaytime() {
// // Given
// Employee employee = new Employee(1, "John", "Doe", "john.doe@example.com",
// "123456789", "IT", "Support",
// LocalDate.now());
// when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));

// LoginLogoutTimes loginLogoutTimes = new LoginLogoutTimes(1L, 1, "survival",
// LocalDateTime.now().minusHours(2),
// LocalDateTime.now());
// when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(Collections.singletonList(loginLogoutTimes));

// when(dailyPlaytimeRepository.findByEmployeeIdAndDate(1,
// loginLogoutTimes.getLoginTime().toLocalDate()))
// .thenReturn(null);

// // When
// playtimeCalculationService.calculateDailyPlaytime();

// // Then
// verify(dailyPlaytimeRepository, times(1)).save(any(DailyPlaytime.class));
// }

// @Test
// public void calculateDailyPlaytime_ShouldSkipEmployeeWithNoLoginTimes() {
// // Given
// Employee employee = new Employee(1, "John", "Doe", "john.doe@example.com",
// "123456789", "IT", "Support",
// LocalDate.now());
// when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
// when(loginLogoutTimesRepository.findByEmployeeId(1)).thenReturn(Collections.emptyList());

// // When
// playtimeCalculationService.calculateDailyPlaytime();

// // Then
// verify(dailyPlaytimeRepository, never()).save(any(DailyPlaytime.class));
// }
// }
