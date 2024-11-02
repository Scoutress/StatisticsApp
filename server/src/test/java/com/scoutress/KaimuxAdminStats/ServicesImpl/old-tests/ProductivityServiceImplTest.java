// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import static org.mockito.ArgumentCaptor.forClass;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.ArgumentMatchers.argThat;
// import static org.mockito.ArgumentMatchers.eq;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.lenient;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
// import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
// import com.scoutress.KaimuxAdminStats.Entity.Productivity;
// import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
// import com.scoutress.KaimuxAdminStats.Repositories.AfkPlaytimeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.ProductivityCalcRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
// import com.scoutress.KaimuxAdminStats.Servicesimpl.ProductivityServiceImpl;

// @ExtendWith(MockitoExtension.class)
// public class ProductivityServiceImplTest {

// @Mock
// private AfkPlaytimeRepository afkPlaytimeRepository;

// @Mock
// private DailyPlaytimeRepository dailyPlaytimeRepository;

// @Mock
// private EmployeeRepository employeeRepository;

// @Mock
// private ProductivityRepository productivityRepository;

// @Mock
// private DcTicketRepository dcTicketRepository;

// @Mock
// private ProductivityCalcRepository productivityCalcRepository;

// @Mock
// private ComplainsRepository complainsRepository;

// @InjectMocks
// private ProductivityServiceImpl productivityServiceImpl;

// private Integer employeeId;
// private Employee employee;
// private ProductivityCalc productivityCalc;
// private Productivity productivity;

// @BeforeEach
// void setup() {
// employeeId = 1;
// employee = new Employee(1, "JohnDoe", "Support", "English", "John", "Doe",
// "johndoe@example.com", null);
// employee.setLevel("Support");

// productivityCalc = new ProductivityCalc();
// productivityCalc.setEmployee(employee);
// productivityCalc.setDiscordTicketsCalc(100.0);
// productivityCalc.setAfkPlaytimeCalc(20.0);
// productivityCalc.setPlaytimeCalc(50.0);
// productivityCalc.setServerTicketsCalc(70.0);
// productivityCalc.setServerTicketsTakingCalc(60.0);
// productivityCalc.setComplainsCalc(10.0);

// productivity = new Productivity();
// productivity.setEmployee(employee);
// }

// @Test
// void testFindAll() {
// setup();
// Productivity productivity1 = new Productivity();
// Productivity productivity2 = new Productivity();
// List<Productivity> productivityList = Arrays.asList(productivity1,
// productivity2);

// when(productivityRepository.findAllWithEmployeeDetails()).thenReturn(productivityList);

// List<Productivity> result = productivityServiceImpl.findAll();

// assertEquals(productivityList, result);
// verify(productivityRepository, times(1)).findAllWithEmployeeDetails();
// }

// @Test
// void testUpdateProductivityData_whenProductivityDoesNotExist() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(1)).thenReturn(null);

// productivityServiceImpl.updateProductivityData();

// verify(productivityRepository, times(1)).save(any(Productivity.class));
// }

// @Test
// void testUpdateProductivityData_whenProductivityExists() {
// Productivity existingProductivity = new Productivity();
// existingProductivity.setEmployee(employee);

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(1)).thenReturn(existingProductivity);

// productivityServiceImpl.updateProductivityData();

// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testUpdateAnnualPlaytimeForAllEmployees_whenProductivityRecordExists() {
// LocalDate endDate = LocalDate.now().minusDays(1);
// LocalDate startDate = endDate.minusDays(365);

// Double totalPlaytime = 100.0;
// Productivity existingProductivity = new Productivity(employee);

// when(dailyPlaytimeRepository.findAllDistinctEmployeeIds()).thenReturn(List.of(employeeId));
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId,
// startDate, endDate))
// .thenReturn(totalPlaytime);
// when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
// when(productivityRepository.findByEmployeeId(employeeId)).thenReturn(existingProductivity);

// productivityServiceImpl.updateAnnualPlaytimeForAllEmployees();

// verify(productivityRepository).save(existingProductivity);
// assertEquals(totalPlaytime, existingProductivity.getAnnualPlaytime());
// }

// @Test
// void
// testUpdateAnnualPlaytimeForAllEmployees_whenProductivityRecordDoesNotExist()
// {
// LocalDate endDate = LocalDate.now().minusDays(1);
// LocalDate startDate = endDate.minusDays(365);

// Double totalPlaytime = 200.0;

// when(dailyPlaytimeRepository.findAllDistinctEmployeeIds()).thenReturn(List.of(employeeId));
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId,
// startDate, endDate))
// .thenReturn(totalPlaytime);
// when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
// when(productivityRepository.findByEmployeeId(employeeId)).thenReturn(null);

// productivityServiceImpl.updateAnnualPlaytimeForAllEmployees();

// verify(productivityRepository).save(any(Productivity.class));
// }

// @Test
// void testUpdateAnnualPlaytimeForAllEmployees_whenEmployeeNotFound() {
// when(dailyPlaytimeRepository.findAllDistinctEmployeeIds()).thenReturn(List.of(employeeId));
// when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

// Exception exception = assertThrows(RuntimeException.class, () -> {
// productivityServiceImpl.updateAnnualPlaytimeForAllEmployees();
// });

// assertEquals("Employee not found", exception.getMessage());
// }

// @Test
// void testUpdateAnnualPlaytimeForAllEmployees_withNoPlaytimeData() {
// LocalDate endDate = LocalDate.now().minusDays(1);
// LocalDate startDate = endDate.minusDays(365);

// when(dailyPlaytimeRepository.findAllDistinctEmployeeIds()).thenReturn(List.of(employeeId));
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId,
// startDate, endDate)).thenReturn(null);
// when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
// when(productivityRepository.findByEmployeeId(employeeId)).thenReturn(null);

// productivityServiceImpl.updateAnnualPlaytimeForAllEmployees();

// verify(productivityRepository).save(any(Productivity.class));
// }

// @Test
// void
// testUpdateAveragePlaytimeForAllEmployees_withExistingProductivityRecord() {
// setup();

// LocalDate startDate = LocalDate.of(2023, 8, 11);
// LocalDate endDate = LocalDate.of(2024, 8, 10);
// Double totalPlaytime = 100.0;

// Productivity existingProductivity = new Productivity(employee);

// when(employeeRepository.findAllEmployeeIds()).thenReturn(List.of(employeeId));
// when(dailyPlaytimeRepository.findEarliestPlaytimeDateByEmployeeId(employeeId)).thenReturn(startDate);
// when(dailyPlaytimeRepository.findLatestPlaytimeDateByEmployeeId(employeeId)).thenReturn(endDate);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId,
// startDate, endDate))
// .thenReturn(totalPlaytime);
// when(productivityRepository.findByEmployeeId(employeeId)).thenReturn(existingProductivity);
// when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

// productivityServiceImpl.updateAveragePlaytimeForAllEmployees();

// double expectedAveragePlaytime = totalPlaytime /
// (ChronoUnit.DAYS.between(startDate, endDate) + 1);

// verify(productivityRepository).save(existingProductivity);
// assertEquals(expectedAveragePlaytime, existingProductivity.getPlaytime());
// }

// @Test
// void testUpdateAveragePlaytimeForAllEmployees_whenNoPlaytimeRecords() {
// when(employeeRepository.findAllEmployeeIds()).thenReturn(List.of(employeeId));
// when(dailyPlaytimeRepository.findEarliestPlaytimeDateByEmployeeId(employeeId)).thenReturn(null);
// when(dailyPlaytimeRepository.findLatestPlaytimeDateByEmployeeId(employeeId)).thenReturn(null);

// productivityServiceImpl.updateAveragePlaytimeForAllEmployees();

// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testUpdateAveragePlaytimeForAllEmployees_whenTotalPlaytimeIsNull() {
// lenient()
// .when(
// dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(anyInt(),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(null);

// Productivity localProductivity = new Productivity(new Employee(1));
// lenient().when(productivityRepository.findByEmployeeId(anyInt())).thenReturn(localProductivity);

// productivityServiceImpl.updateAveragePlaytimeForAllEmployees();

// assertNull(localProductivity.getPlaytime());
// verify(productivityRepository, never()).save(localProductivity);
// }

// @Test
// void testUpdateAfkPlaytimeForAllEmployees_withExistingProductivityRecord() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dailyPlaytimeRepository.getTotalPlaytimeByEmployeeId(employee.getId())).thenReturn(100.0);
// when(afkPlaytimeRepository.getTotalAfkPlaytimeByEmployeeId(employee.getId())).thenReturn(20.0);

// Productivity existingProductivity = new Productivity(employee);

// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(existingProductivity);

// productivityServiceImpl.updateAfkPlaytimeForAllEmployees();

// verify(productivityRepository).save(existingProductivity);
// assertEquals(20.0, existingProductivity.getAfkPlaytime());
// }

// @Test
// void testUpdateAfkPlaytimeForAllEmployees_withZeroTotalPlaytime() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dailyPlaytimeRepository.getTotalPlaytimeByEmployeeId(employee.getId())).thenReturn(0.0);
// when(afkPlaytimeRepository.getTotalAfkPlaytimeByEmployeeId(employee.getId())).thenReturn(50.0);

// Productivity existingProductivity = new Productivity(employee);

// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(existingProductivity);

// productivityServiceImpl.updateAfkPlaytimeForAllEmployees();

// verify(productivityRepository).save(existingProductivity);
// assertEquals(0.0, existingProductivity.getAfkPlaytime());
// }

// @Test
// void testUpdateAfkPlaytimeForAllEmployees_withNullTotalPlaytime() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dailyPlaytimeRepository.getTotalPlaytimeByEmployeeId(employee.getId())).thenReturn(null);
// when(afkPlaytimeRepository.getTotalAfkPlaytimeByEmployeeId(employee.getId())).thenReturn(50.0);

// Productivity existingProductivity = new Productivity(employee);

// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(existingProductivity);

// productivityServiceImpl.updateAfkPlaytimeForAllEmployees();

// verify(productivityRepository).save(existingProductivity);
// assertEquals(0.0, existingProductivity.getAfkPlaytime());
// }

// @Test
// void testCalculateServerTicketsForAllEmployeesWithCoefs_supportLevel() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findServerTicketsByEmployeeId(employee.getId())).thenReturn(1.0);

// productivityServiceImpl.calculateServerTicketsForAllEmployeesWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor = forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(0.5 * CalculationConstants.SERVER_TICKETS_SUPPORT,
// captured.getServerTicketsCalc());
// }

// @Test
// void testCalculateServerTicketsForAllEmployeesWithCoefs_chatmodLevel() {
// employee.setLevel("Chatmod");

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findServerTicketsByEmployeeId(employee.getId())).thenReturn(1.0);

// productivityServiceImpl.calculateServerTicketsForAllEmployeesWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor = forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(1.0 * CalculationConstants.SERVER_TICKETS_CHATMOD,
// captured.getServerTicketsCalc());
// }

// @Test
// void
// testCalculateServerTicketsForAllEmployeesWithCoefs_managerLevelWithHighTickets()
// {
// employee.setLevel("Manager");

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findServerTicketsByEmployeeId(employee.getId())).thenReturn(5.0);

// productivityServiceImpl.calculateServerTicketsForAllEmployeesWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertNotNull(captured, "Captured ProductivityCalc should not be null");
// assertEquals(4.0 * CalculationConstants.SERVER_TICKETS_MANAGER,
// captured.getServerTicketsCalc());
// }

// @Test
// void testCalculateServerTicketsTakenForAllEmployeesWithCoefs_supportLevel() {
// double serverTicketsTaken = 25.0;
// double expectedValue = 20.0 *
// CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findServerTicketsTakenByEmployeeId(employee.getId())).thenReturn(serverTicketsTaken);

// productivityServiceImpl.calculateServerTicketsTakenForAllEmployeesWithCoefs();

// verify(productivityCalcRepository, times(1)).save(argThat(savedCalc -> {
// assertEquals(expectedValue, savedCalc.getServerTicketsTakingCalc());
// assertEquals(employee, savedCalc.getEmployee());
// return true;
// }));
// }

// @Test
// void testCalculateServerTicketsTakenForAllEmployeesWithCoefs_chatmodLevel() {
// employee.setLevel("Chatmod");
// double serverTicketsTaken = 45.0;
// double expectedValue = 40.0 *
// CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findServerTicketsTakenByEmployeeId(employee.getId())).thenReturn(serverTicketsTaken);

// productivityServiceImpl.calculateServerTicketsTakenForAllEmployeesWithCoefs();

// verify(productivityCalcRepository, times(1)).save(argThat(savedCalc -> {
// assertEquals(expectedValue, savedCalc.getServerTicketsTakingCalc());
// assertEquals(employee, savedCalc.getEmployee());
// return true;
// }));
// }

// @Test
// void testCalculateAfkPlaytimeForAllEmployeesWithCoefs_supportLevel() {
// employee.setLevel("Support");

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findAfkPlaytimeByEmployeeId(employee.getId())).thenReturn(250.0);

// productivityServiceImpl.calculateAfkPlaytimeForAllEmployeesWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(100.0, captured.getAfkPlaytimeCalc());
// }

// @Test
// void testCalculateAfkPlaytimeForAllEmployeesWithCoefs_chatmodLevel() {
// employee.setLevel("Chatmod");

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);
// when(productivityRepository.findAfkPlaytimeByEmployeeId(employee.getId())).thenReturn(1000.0);

// productivityServiceImpl.calculateAfkPlaytimeForAllEmployeesWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(100.0, captured.getAfkPlaytimeCalc());
// }

// @Test
// void testCalculateAnsweredDiscordTicketsWithCoefs_supportLevel() {
// employee.setLevel("Support");

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dcTicketRepository.findAllDates()).thenReturn(List.of(LocalDate.now()));
// when(dcTicketRepository.sumByDate(any(LocalDate.class))).thenReturn(100.0);
// when(dcTicketRepository.findAnsweredDiscordTicketsByEmployeeIdAndDate(eq(employee.getId()),
// any(LocalDate.class)))
// .thenReturn(50.0);

// productivityServiceImpl.calculateAnsweredDiscordTicketsWithCoefs();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// double expectedValue = (50.0 / (100.0 *
// CalculationConstants.DISCORD_TICKETS_SUPPORT)) * 100;
// assertEquals(expectedValue, captured.getDiscordTicketsCalc());
// }

// @Test
// void testCalculateAnsweredDiscordTicketsWithCoefs_whenNoTickets() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dcTicketRepository.findAllDates()).thenReturn(List.of(LocalDate.now()));
// when(dcTicketRepository.sumByDate(any(LocalDate.class))).thenReturn(0.0);

// productivityServiceImpl.calculateAnsweredDiscordTicketsWithCoefs();

// verify(productivityCalcRepository,
// never()).save(any(ProductivityCalc.class));
// }

// @Test
// void testCalculateAndSaveComplainsCalc_whenProductivityCalcExists() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(complainsRepository.sumComplaintsByEmployeeId(employee.getId())).thenReturn(5.0);
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(productivityCalc);

// productivityServiceImpl.calculateAndSaveComplainsCalc();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(5.0, captured.getComplainsCalc());
// }

// @Test
// void testCalculateAndSaveComplainsCalc_whenProductivityCalcDoesNotExist() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(complainsRepository.sumComplaintsByEmployeeId(employee.getId())).thenReturn(3.0);
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);

// productivityServiceImpl.calculateAndSaveComplainsCalc();

// ArgumentCaptor<ProductivityCalc> captor =
// ArgumentCaptor.forClass(ProductivityCalc.class);
// verify(productivityCalcRepository).save(captor.capture());

// ProductivityCalc captured = captor.getValue();
// assertEquals(3.0, captured.getComplainsCalc());
// }

// @Test
// void testCalculateAndSaveProductivity_successfulCalculationAndSave() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(productivityCalc);
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);

// productivityServiceImpl.calculateAndSaveProductivity();

// verify(productivityRepository).save(productivity);
// assertEquals(42.0, productivity.getProductivity());
// }

// @Test
// void testCalculateAndSaveProductivity_whenProductivityCalcIsNull() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(null);

// productivityServiceImpl.calculateAndSaveProductivity();

// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testCalculateAndSaveProductivity_whenExceptionFetchingProductivityCalc()
// {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId()))
// .thenThrow(new RuntimeException("Test Exception"));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// productivityServiceImpl.calculateAndSaveProductivity();
// });

// assertEquals("Test Exception", exception.getMessage());
// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testCalculateAndSaveProductivity_whenExceptionFetchingValues() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));

// ProductivityCalc mockProductivityCalc = mock(ProductivityCalc.class);
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(mockProductivityCalc);

// doThrow(new RuntimeException("Test
// Exception")).when(mockProductivityCalc).getPlaytimeCalc();

// RuntimeException thrown = assertThrows(RuntimeException.class,
// () -> productivityServiceImpl.calculateAndSaveProductivity());

// assertEquals("Test Exception", thrown.getMessage());

// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testCalculateAndSaveProductivity_whenExceptionDuringCalculation() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));

// ProductivityCalc mockProductivityCalc = mock(ProductivityCalc.class);
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(mockProductivityCalc);

// doThrow(new RuntimeException("Test
// Exception")).when(mockProductivityCalc).getPlaytimeCalc();

// RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
// productivityServiceImpl.calculateAndSaveProductivity();
// });

// assertEquals("Test Exception", thrown.getMessage());

// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testCalculateAndSaveProductivity_whenExceptionDuringSave() {
// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityCalcRepository.findByEmployeeId(employee.getId())).thenReturn(productivityCalc);
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);

// doThrow(new RuntimeException("Test
// Exception")).when(productivityRepository).save(any(Productivity.class));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// productivityServiceImpl.calculateAndSaveProductivity();
// });

// assertEquals("Test Exception", exception.getMessage());
// verify(productivityRepository).save(any(Productivity.class));
// }
// }
