package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.DailyAfkPlaytime;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.afkPlaytime.DailyAfkPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.DailyObjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.servicesImpl.productivity.ProductivityServiceImpl;

class ProductivityServiceImplTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private DailyPlaytimeRepository dailyPlaytimeRepository;

  @Mock
  private DailyAfkPlaytimeRepository dailyAfkPlaytimeRepository;

  @Mock
  private DailyDiscordTicketsRepository dailyDiscordTicketsRepository;

  @Mock
  private DailyDiscordMessagesRepository dailyDiscordMessagesRepository;

  @Mock
  private DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;

  @Mock
  private DailyObjectiveProductivityRepository dailyObjectiveProductivityRepository;

  @Mock
  private DailyDiscordTicketsComparedRepository dailyDiscordTicketsComparedRepository;

  @Mock
  private DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;

  @Mock
  private DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;

  @InjectMocks
  private ProductivityServiceImpl productivityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testCalculateDailyObjectiveProductivity() {
    // Arrange: Setup test data for Employee
    // Employee employee = new Employee(
    // 1L, // id
    // "testUsername", // username
    // "testLanguage", // language
    // "testName", // firstName
    // "testLastName", // lastName
    // "testEmail", // email
    // LocalDate.of(2024, 1, 1), // joinDate
    // "support" // level
    // );
    // List<Employee> employees = List.of(employee);

    // Test data for DailyPlaytime
    DailyPlaytime playtime = new DailyPlaytime(
        1L, // id
        (short) 1, // aid
        10.0, // time
        LocalDate.of(2024, 5, 1), // date
        "survival" // server
    );

    // Test data for DailyAfkPlaytime
    DailyAfkPlaytime afkPlaytime = new DailyAfkPlaytime(
        1L, // id
        (short) 1, // aid
        2.0, // time
        LocalDate.of(2024, 5, 1), // date
        "survival" // server
    );

    // Test data for DailyDiscordTickets
    DailyDiscordTickets discordTickets = new DailyDiscordTickets(
        1L, // id
        (short) 1, // aid
        10, // ticketCount
        LocalDate.of(2024, 5, 1) // date
    );

    // Test data for DailyDiscordMessages
    DailyDiscordMessages discordMessages = new DailyDiscordMessages(
        1L, // id
        (short) 1, // aid
        50, // msgCount
        LocalDate.of(2024, 5, 1) // date
    );

    // Test data for DailyMinecraftTickets
    DailyMinecraftTickets minecraftTickets = new DailyMinecraftTickets(
        1L, // id
        (short) 1, // aid
        5, // ticketCount
        LocalDate.of(2024, 5, 1) // date
    );

    // Test data for EmployeeLevel
    // EmployeeLevel employeeLevel = new EmployeeLevel(
    // 1L,
    // (short) 1,
    // LocalDate.of(2024, 1, 1),
    // LocalDate.of(2024, 3, 1),
    // null,
    // null,
    // null,
    // null,
    // null,
    // null,
    // null);

    // Mock repository methods
    // when(employeeRepository.findAll()).thenReturn(employees);
    when(dailyPlaytimeRepository.findAll()).thenReturn(List.of(playtime));
    when(dailyAfkPlaytimeRepository.findAll()).thenReturn(List.of(afkPlaytime));
    when(dailyDiscordTicketsRepository.findAll()).thenReturn(List.of(discordTickets));
    when(dailyDiscordMessagesRepository.findAll()).thenReturn(List.of(discordMessages));
    when(dailyMinecraftTicketsRepository.findAll()).thenReturn(List.of(minecraftTickets));
    when(dailyDiscordTicketsComparedRepository.findAll()).thenReturn(List.of());
    when(dailyDiscordMessagesComparedRepository.findAll()).thenReturn(List.of());
    when(dailyMinecraftTicketsComparedRepository.findAll()).thenReturn(List.of());

    // Mock EmployeeLevel repository
    // when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));

    // Act: Call the method to test
    productivityService.calculateDailyObjectiveProductivity();

    // Assert: Verify that saveAll was called once
    verify(dailyObjectiveProductivityRepository, times(1)).saveAll(any());
  }
}
