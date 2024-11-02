// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;
// import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketPercentage;
// import com.scoutress.KaimuxAdminStats.Entity.Productivity;
// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketPercentageRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
// import com.scoutress.KaimuxAdminStats.Servicesimpl.McTicketServiceImpl;

// public class McTicketServiceImplTest {

// @Mock
// private McTicketRepository mcTicketRepository;

// @Mock
// private ProductivityRepository productivityRepository;

// @Mock
// private EmployeeRepository employeeRepository;

// @Mock
// private McTicketPercentageRepository mcTicketPercentageRepository;

// @InjectMocks
// private McTicketServiceImpl mcTicketServiceImpl;

// @BeforeEach
// public void setUp() {
// MockitoAnnotations.openMocks(this);
// }

// @Test
// public void testSaveAll_McTicketsSavedSuccessfully() {
// // Arrange
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 5),
// new McTicket(2, LocalDate.of(2024, 1, 2), 10));

// mcTicketServiceImpl.saveAll(mcTickets);

// verify(mcTicketRepository, times(1)).saveAll(mcTickets);
// }

// @Test
// public void testSaveAll_EmptyList() {
// List<McTicket> mcTickets = Collections.emptyList();

// mcTicketServiceImpl.saveAll(mcTickets);

// verify(mcTicketRepository, times(1)).saveAll(mcTickets);
// }

// @Test
// public void testUpdateMinecraftTicketsAverage_Success() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 5),
// new McTicket(1, LocalDate.of(2024, 1, 2), 10));
// Productivity productivity = new Productivity();
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(productivity);

// mcTicketServiceImpl.updateMinecraftTicketsAverage();

// assertEquals(7.5, productivity.getServerTickets());
// verify(productivityRepository, times(1)).save(productivity);
// }

// @Test
// public void testUpdateMinecraftTicketsAverage_NoTickets() {
// when(mcTicketRepository.findAll()).thenReturn(Collections.emptyList());

// mcTicketServiceImpl.updateMinecraftTicketsAverage();

// verify(productivityRepository, times(0)).save(any(Productivity.class));
// }

// @Test
// public void testUpdateMinecraftTicketsAverage_SingleDayTickets() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 10));
// Productivity productivity = new Productivity();
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(productivity);

// mcTicketServiceImpl.updateMinecraftTicketsAverage();

// assertEquals(10.0, productivity.getServerTickets());
// verify(productivityRepository, times(1)).save(productivity);
// }

// @Test
// public void testUpdateMinecraftTicketsAverage_EmployeeNotFound() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 5));
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(null);
// when(employeeRepository.findById(1)).thenReturn(Optional.empty());

// mcTicketServiceImpl.updateMinecraftTicketsAverage();

// verify(productivityRepository, times(0)).save(any(Productivity.class));
// }

// @Test
// public void testCalculateMcTicketsPercentage_Success() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 5),
// new McTicket(2, LocalDate.of(2024, 1, 1), 10));
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);

// mcTicketServiceImpl.calculateMcTicketsPercentage();

// verify(mcTicketPercentageRepository,
// times(2)).save(any(McTicketPercentage.class));
// }

// @Test
// public void testCalculateMcTicketsPercentage_NoTickets() {
// when(mcTicketRepository.findAll()).thenReturn(Collections.emptyList());

// mcTicketServiceImpl.calculateMcTicketsPercentage();

// verify(mcTicketPercentageRepository,
// times(0)).save(any(McTicketPercentage.class));
// }

// @Test
// public void testCalculateMcTicketsPercentage_ZeroTicketsOnDay() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 0),
// new McTicket(2, LocalDate.of(2024, 1, 1), 0));
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);

// mcTicketServiceImpl.calculateMcTicketsPercentage();

// verify(mcTicketPercentageRepository,
// times(2)).save(any(McTicketPercentage.class));
// }

// @Test
// public void testCalculateMcTicketsPercentage_RepositorySaveFails() {
// List<McTicket> mcTickets = Arrays.asList(
// new McTicket(1, LocalDate.of(2024, 1, 1), 5),
// new McTicket(2, LocalDate.of(2024, 1, 1), 10));
// when(mcTicketRepository.findAll()).thenReturn(mcTickets);
// doThrow(new RuntimeException("Save
// failed")).when(mcTicketPercentageRepository).save(any(McTicketPercentage.class));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// mcTicketServiceImpl.calculateMcTicketsPercentage();
// });
// assertEquals("Save failed", exception.getMessage());
// }

// @Test
// public void testUpdateAverageMcTicketsPercentages_Success() {
// List<McTicketPercentage> percentages = Arrays.asList(
// new McTicketPercentage(1, LocalDate.of(2024, 1, 1), 50.0),
// new McTicketPercentage(1, LocalDate.of(2024, 1, 2), 100.0));
// Productivity productivity = new Productivity();
// when(mcTicketPercentageRepository.findAll()).thenReturn(percentages);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(productivity);

// mcTicketServiceImpl.updateAverageMcTicketsPercentages();

// assertEquals(75.0, productivity.getServerTicketsTaking());
// verify(productivityRepository, times(1)).save(productivity);
// }

// @Test
// public void testUpdateAverageMcTicketsPercentages_NoPercentages() {
// when(mcTicketPercentageRepository.findAll()).thenReturn(Collections.emptyList());

// mcTicketServiceImpl.updateAverageMcTicketsPercentages();

// verify(productivityRepository, times(0)).save(any(Productivity.class));
// }

// @Test
// public void testUpdateAverageMcTicketsPercentages_EmployeeNotFound() {
// List<McTicketPercentage> percentages = Arrays.asList(
// new McTicketPercentage(1, LocalDate.of(2024, 1, 1), 50.0));
// when(mcTicketPercentageRepository.findAll()).thenReturn(percentages);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(null);

// mcTicketServiceImpl.updateAverageMcTicketsPercentages();

// verify(productivityRepository, times(0)).save(any(Productivity.class));
// }

// @Test
// public void testUpdateAverageMcTicketsPercentages_RepositorySaveFails() {
// List<McTicketPercentage> percentages = Arrays.asList(
// new McTicketPercentage(1, LocalDate.of(2024, 1, 1), 50.0));
// Productivity productivity = new Productivity();
// when(mcTicketPercentageRepository.findAll()).thenReturn(percentages);
// when(productivityRepository.findByEmployeeId(1)).thenReturn(productivity);
// doThrow(new RuntimeException("Save
// failed")).when(productivityRepository).save(any(Productivity.class));

// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// mcTicketServiceImpl.updateAverageMcTicketsPercentages();
// });
// assertEquals("Save failed", exception.getMessage());
// }
// }
