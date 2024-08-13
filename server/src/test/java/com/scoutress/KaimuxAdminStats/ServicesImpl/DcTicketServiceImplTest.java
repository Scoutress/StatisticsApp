package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicketPercentage;
import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotions;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeePromotionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Servicesimpl.DcTicketServiceImpl;

public class DcTicketServiceImplTest {

  @Mock
  private DcTicketRepository dcTicketRepository;

  @Mock
  private ProductivityRepository productivityRepository;

  @Mock
  private DcTicketPercentageRepository dcTicketPercentageRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeePromotionsRepository employeePromotionsRepository;

  @InjectMocks
  private DcTicketServiceImpl dcTicketServiceImpl;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void saveAll_ShouldCallRepositorySaveAll() {
    List<DcTicket> dcTickets = Arrays.asList(new DcTicket(), new DcTicket());

    dcTicketServiceImpl.saveAll(dcTickets);

    verify(dcTicketRepository, times(1)).saveAll(dcTickets);
  }

  @Test
  public void updateDiscordTicketsAverage_ShouldSkipIfNoJoinDateOrSupportDate() {
    DcTicket dcTicket = new DcTicket(1, LocalDate.now().minusDays(10), 5);
    Employee employee = new Employee(1, LocalDate.now().minusDays(20));
    EmployeePromotions promotions = new EmployeePromotions(1, 1, null, null, null, null);

    when(dcTicketRepository.findAll()).thenReturn(Collections.singletonList(dcTicket));
    when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
    when(employeePromotionsRepository.findByEmployeeId(1)).thenReturn(promotions);

    dcTicketServiceImpl.updateDiscordTicketsAverage();

    verify(productivityRepository, never()).save(any(Productivity.class));
  }

  @Test
  public void updateDiscordTicketsAverage_ShouldSkipIfNoValidTicketsAfterFiltering() {
    DcTicket dcTicket = new DcTicket(1, LocalDate.now().minusDays(10), 5);
    Employee employee = new Employee();
    employee.setId(1);
    employee.setJoinDate(LocalDate.now().minusDays(20));

    EmployeePromotions promotions = new EmployeePromotions(1, LocalDate.now().minusDays(5), null, null, null);

    when(dcTicketRepository.findAll()).thenReturn(Collections.singletonList(dcTicket));
    when(employeeRepository.findById(1)).thenReturn(java.util.Optional.of(employee));
    when(employeePromotionsRepository.findByEmployeeId(1)).thenReturn(promotions);

    dcTicketServiceImpl.updateDiscordTicketsAverage();

    verify(productivityRepository, never()).save(any(Productivity.class));
  }

  @Test
  public void updateDiscordTicketsAverage_ShouldSaveProductivityWhenValidTicketsExist() {
    DcTicket dcTicket1 = new DcTicket(1, LocalDate.now().minusDays(10), 5);
    DcTicket dcTicket2 = new DcTicket(1, LocalDate.now().minusDays(8), 3);

    Employee employee = new Employee(1, LocalDate.now().minusDays(20));
    EmployeePromotions promotions = new EmployeePromotions(1, 1, LocalDate.now().minusDays(15), null, null, null);

    when(dcTicketRepository.findAll()).thenReturn(Arrays.asList(dcTicket1, dcTicket2));
    when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
    when(employeePromotionsRepository.findByEmployeeId(1)).thenReturn(promotions);

    dcTicketServiceImpl.updateDiscordTicketsAverage();

    verify(productivityRepository).save(any(Productivity.class));
  }

  @Test
  public void calculateDcTicketsPercentage_ShouldSkipIfNoValidTicketsAfterFiltering() {
    DcTicket dcTicket = new DcTicket(1, LocalDate.now().minusDays(10), 5);

    Employee employee = new Employee(1, "John", "Doe", "john.doe@example.com", "123456789", "IT", "Support",
        LocalDate.now().minusDays(20));

    EmployeePromotions promotions = new EmployeePromotions(1, 1, LocalDate.now().minusDays(5), null, null, null);

    when(dcTicketRepository.findAll()).thenReturn(Collections.singletonList(dcTicket));
    when(employeeRepository.findById(1)).thenReturn(java.util.Optional.of(employee));
    when(employeePromotionsRepository.findByEmployeeId(1)).thenReturn(promotions);

    dcTicketServiceImpl.calculateDcTicketsPercentage();

    verify(dcTicketPercentageRepository, never()).save(any(DcTicketPercentage.class));
  }

  @Test
  public void calculateDcTicketsPercentage_ShouldSavePercentageWhenValidTicketsExist() {
    DcTicket dcTicket = new DcTicket(1, LocalDate.now(), 5);
    Employee employee = new Employee(1, LocalDate.now().minusDays(20));
    EmployeePromotions promotions = new EmployeePromotions(1, 1, LocalDate.now().minusDays(10), null, null, null);

    when(dcTicketRepository.findAll()).thenReturn(Collections.singletonList(dcTicket));
    when(employeeRepository.findById(1)).thenReturn(java.util.Optional.of(employee));
    when(employeePromotionsRepository.findByEmployeeId(1)).thenReturn(promotions);

    dcTicketServiceImpl.calculateDcTicketsPercentage();

    verify(dcTicketPercentageRepository, times(1)).save(any(DcTicketPercentage.class));
  }

  @Test
  public void updateAverageDcTicketsPercentages_ShouldNotUpdateIfProductivityIsNull() {
    DcTicketPercentage percentage = new DcTicketPercentage(1, LocalDate.now(), 50.0);

    when(dcTicketPercentageRepository.findAll()).thenReturn(Collections.singletonList(percentage));
    when(productivityRepository.findByEmployeeId(1)).thenReturn(null);

    dcTicketServiceImpl.updateAverageDcTicketsPercentages();

    verify(productivityRepository, never()).save(any(Productivity.class));
  }

  @Test
  public void updateAverageDcTicketsPercentages_ShouldUpdateProductivityWhenPercentagesExist() {
    DcTicketPercentage percentage = new DcTicketPercentage(1, LocalDate.now(), 50.0);
    Productivity productivity = new Productivity();

    when(dcTicketPercentageRepository.findAll()).thenReturn(Collections.singletonList(percentage));
    when(productivityRepository.findByEmployeeId(1)).thenReturn(productivity);

    dcTicketServiceImpl.updateAverageDcTicketsPercentages();

    verify(productivityRepository, times(1)).save(productivity);
    assertEquals(50.0, productivity.getDiscordTicketsTaking());
  }
}
