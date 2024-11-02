// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
// import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
// import com.scoutress.KaimuxAdminStats.Entity.Productivity;
// import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
// import com.scoutress.KaimuxAdminStats.Servicesimpl.RecommendationServiceImpl;

// class RecommendationServiceImplTest {

// @Mock
// private EmployeeRepository employeeRepository;

// @Mock
// private ProductivityRepository productivityRepository;

// @Mock
// private DailyPlaytimeRepository dailyPlaytimeRepository;

// @InjectMocks
// private RecommendationServiceImpl recommendationService;

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// void testSetUpInitialization() {
// setUp();
// }

// @Test
// void testDismissEmployeeWithLowPlaytime() {
// // 1. Per mažai pražaista (atleisti)
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusYears(2));

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(3.9); // Less than 4 hours playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Dismiss", savedProductivity.getRecommendation());
// }

// @Test
// void testIgnoreEmployeeWithSufficientPlaytime() {
// // Arrange
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusYears(2));

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.1); // Pakankamas žaidimo laikas (daugiau nei 4 valandos)

// // Act
// recommendationService.evaluateEmployees();

// // Assert
// verify(productivityRepository, never()).save(any(Productivity.class));
// }

// @Test
// void testEvaluateHelper_Promote() {
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusYears(2));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1);

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.1); // Pakankamas žaidimo laikas

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// System.out.println("Captured recommendation: " +
// savedProductivity.getRecommendation());
// assertEquals("Promote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateHelper_Stay_WithSufficientTimeAndLowProductivity() {
// // 4. Helper daugiau arba lygu WORK_TIME_HELPER ir mažiau PROMOTION_VALUE (-)
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusYears(2));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateHelper_Stay_ForShortWorkTime() {
// // Arrange
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_HELPER - 1)); //
// Darbo laikas mažesnis nei reikalingas
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Mažiau nei
// // Promotion Value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 valandos arba daugiau žaidimo laiko (viršija minimumą)

// // Act
// recommendationService.evaluateEmployees();

// // Assert
// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// // Tikriname, kad rekomendacija yra "-"
// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateHelper_Stay_ForShortWorkTimeAndLowProductivity() {
// // 7. Helper mažiau WORK_TIME_HELPER ir mažiau PROMOTION_VALUE (-)
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_HELPER - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateHelper_Stay_ForShortWorkTimeAndVeryLowProductivity() {
// // 8. Helper mažiau WORK_TIME_HELPER ir mažiau DEMOTION_VALUE (-)
// Employee employee = new Employee(1, "JohnDoe", "Helper", "English", "John",
// "Doe", "john.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_HELPER - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// // Similar tests for Support, ChatMod, Overseer, and Manager levels
// @Test
// void testEvaluateSupport_Promote() {
// // 9. Support daugiau arba lygu WORK_TIME_SUPPORT ir daugiau arba lygu
// // PROMOTION_VALUE (Paaukštinti)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Promote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateSupport_Stay_WithSufficientTimeAndLowProductivity() {
// // 10. Support daugiau arba lygu WORK_TIME_SUPPORT ir mažiau PROMOTION_VALUE
// (-)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateSupport_Demote_WithSufficientTimeAndVeryLowProductivity() {
// // 11. Support daugiau arba lygu WORK_TIME_SUPPORT ir mažiau DEMOTION_VALUE
// // (Pažeminti)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Demote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateSupport_Stay_ForShortWorkTime() {
// // 12. Support mažiau WORK_TIME_SUPPORT ir daugiau arba lygu PROMOTION_VALUE
// (-)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_SUPPORT - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateSupport_Stay_ForShortWorkTimeAndLowProductivity() {
// // 13. Support mažiau WORK_TIME_SUPPORT ir mažiau PROMOTION_VALUE (-)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_SUPPORT - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateSupport_Stay_ForShortWorkTimeAndVeryLowProductivity() {
// // 14. Support mažiau WORK_TIME_SUPPORT ir mažiau DEMOTION_VALUE (-)
// Employee employee = new Employee(1, "JaneDoe", "Support", "English", "Jane",
// "Doe", "jane.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_SUPPORT - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// // Repeat similar tests for ChatMod, Overseer, and Manager levels

// @Test
// void testEvaluateChatMod_Promote() {
// // 15. ChatMod daugiau arba lygu WORK_TIME_CHATMOD ir daugiau arba lygu
// // PROMOTION_VALUE (Paaukštinti)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Promote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateChatMod_Stay_WithSufficientTimeAndLowProductivity() {
// // 16. ChatMod daugiau arba lygu WORK_TIME_CHATMOD ir mažiau PROMOTION_VALUE
// (-)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateChatMod_Demote_WithSufficientTimeAndVeryLowProductivity() {
// // 17. ChatMod daugiau arba lygu WORK_TIME_CHATMOD ir mažiau DEMOTION_VALUE
// // (Pažeminti)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusYears(3));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Demote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateChatMod_Stay_ForShortWorkTime() {
// // 18. ChatMod mažiau WORK_TIME_CHATMOD ir daugiau arba lygu PROMOTION_VALUE
// (-)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_CHATMOD - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateChatMod_Stay_ForShortWorkTimeAndLowProductivity() {
// // 19. ChatMod mažiau WORK_TIME_CHATMOD ir mažiau PROMOTION_VALUE (-)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_CHATMOD - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateChatMod_Stay_ForShortWorkTimeAndVeryLowProductivity() {
// // 20. ChatMod mažiau WORK_TIME_CHATMOD ir mažiau DEMOTION_VALUE (-)
// Employee employee = new Employee(1, "ChatModDoe", "ChatMod", "English",
// "Chat", "Mod", "chatmod.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_CHATMOD - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Promote() {
// // 21. Overseer daugiau arba lygu WORK_TIME_OVERSEER ir daugiau arba lygu
// // PROMOTION_VALUE (Paaukštinti)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusYears(4));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Promote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Stay_WithSufficientTimeAndLowProductivity() {
// // 22. Overseer daugiau arba lygu WORK_TIME_OVERSEER ir mažiau
// PROMOTION_VALUE
// // (-)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusYears(4));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Demote_WithSufficientTimeAndVeryLowProductivity() {
// // 23. Overseer daugiau arba lygu WORK_TIME_OVERSEER ir mažiau DEMOTION_VALUE
// // (Pažeminti)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusYears(4));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Demote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Stay_ForShortWorkTime() {
// // 24. Overseer mažiau WORK_TIME_OVERSEER ir daugiau arba lygu
// PROMOTION_VALUE
// // (-)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_OVERSEER - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Stay_ForShortWorkTimeAndLowProductivity() {
// // 25. Overseer mažiau WORK_TIME_OVERSEER ir mažiau PROMOTION_VALUE (-)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_OVERSEER - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateOverseer_Stay_ForShortWorkTimeAndVeryLowProductivity() {
// // 26. Overseer mažiau WORK_TIME_OVERSEER ir mažiau DEMOTION_VALUE (-)
// Employee employee = new Employee(1, "OverseerDoe", "Overseer", "English",
// "Over", "Seer",
// "overseer.doe@example.com",
// LocalDate.now().minusDays(CalculationConstants.WORK_TIME_OVERSEER - 1));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateManager_Promote() {
// // 27. Manager daugiau arba lygu WORK_TIME_MANAGER ir daugiau arba lygu
// // PROMOTION_VALUE (Paaukštinti)
// Employee employee = new Employee(1, "ManagerDoe", "Manager", "English",
// "Manager", "Doe", "manager.doe@example.com",
// LocalDate.now().minusYears(5));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateManager_Stay_WithSufficientTimeAndLowProductivity() {
// // 28. Manager daugiau arba lygu WORK_TIME_MANAGER ir mažiau PROMOTION_VALUE
// (-)
// Employee employee = new Employee(1, "ManagerDoe", "Manager", "English",
// "Manager", "Doe", "manager.doe@example.com",
// LocalDate.now().minusYears(5));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE - 1); // Lower than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateManager_Demote_WithSufficientTimeAndVeryLowProductivity() {
// // 29. Manager daugiau arba lygu WORK_TIME_MANAGER ir mažiau DEMOTION_VALUE
// // (Pažeminti)
// Employee employee = new Employee(1, "ManagerDoe", "Manager", "English",
// "Manager", "Doe", "manager.doe@example.com",
// LocalDate.now().minusYears(5));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.DEMOTION_VALUE - 1); // Lower than
// // demotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("Demote", savedProductivity.getRecommendation());
// }

// @Test
// void testEvaluateManager_Stay_WithDifferentLevel() {
// // 33. Darbuotojo lygis nesutampa su Helper, Support, ChatMod, Overseer ir
// // Manager (-)
// Employee employee = new Employee(1, "OtherLevelDoe", "OtherLevel", "English",
// "Other", "Level",
// "otherlevel.doe@example.com",
// LocalDate.now().minusYears(5));
// Productivity productivity = new Productivity(employee,
// CalculationConstants.PROMOTION_VALUE + 1); // Higher than
// // promotion value

// when(employeeRepository.findAll()).thenReturn(List.of(employee));
// when(productivityRepository.findByEmployeeId(employee.getId())).thenReturn(productivity);
// when(dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(eq(employee.getId()),
// any(LocalDate.class),
// any(LocalDate.class)))
// .thenReturn(4.0); // 4 hours or more playtime

// recommendationService.evaluateEmployees();

// ArgumentCaptor<Productivity> captor =
// ArgumentCaptor.forClass(Productivity.class);
// verify(productivityRepository).save(captor.capture());
// Productivity savedProductivity = captor.getValue();

// assertEquals("-", savedProductivity.getRecommendation());
// }
// }
