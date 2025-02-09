package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.scoutress.KaimuxAdminStats.config.DcBotConfig;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.services.DiscordBotService;

@Service
public class DiscordBotServiceImpl implements DiscordBotService {

  EmployeeCodesRepository employeeCodesRepository;
  DailyDiscordMessagesRepository dailyDiscordMessagesRepository;

  public DiscordBotServiceImpl(
      EmployeeCodesRepository employeeCodesRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository) {
    this.employeeCodesRepository = employeeCodesRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    Runtime.getRuntime().addShutdownHook(new Thread(this::stopBot));
  }

  @Override
  public void collectMessagesCountsFromDiscord() {
    startBot();

    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      System.err.println(e);
    }

    List<EmployeeCodes> employeeCodesData = getAddEmployeeCodesData();
    List<Short> allEmployeeIds = getAllEmployeeIds(employeeCodesData);
    LocalDate latestDateFromDcMsgsData = getLatestDateFromDiscordMessagesData();
    LocalDate todaysDate = LocalDate.now();
    List<LocalDate> allDatesFromLatestTillTodays = getAllDatesBetween(
        latestDateFromDcMsgsData, todaysDate);

    System.out.println("Discord messages processing was started...");

    for (Short employeeId : allEmployeeIds) {
      Long dcUserId = getDiscordUserIdForThisEmployee(employeeCodesData, employeeId);

      processDiscordMessagesCount(dcUserId, allDatesFromLatestTillTodays);
    }

    System.out.println("Discord messages processing was completed.");

    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      System.err.println(e);
    }

    stopBot();
  }

  public List<EmployeeCodes> getAddEmployeeCodesData() {
    return employeeCodesRepository.findAll();
  }

  public List<Short> getAllEmployeeIds(List<EmployeeCodes> employeeCodesData) {
    return employeeCodesData
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  public LocalDate getLatestDateFromDiscordMessagesData() {
    return dailyDiscordMessagesRepository
        .findAll()
        .stream()
        .map(DailyDiscordMessages::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
  }

  public List<LocalDate> getAllDatesBetween(
      LocalDate latestDate, LocalDate todaysDate) {

    return Stream
        .iterate(
            latestDate.plusDays(1),
            date -> date.plusDays(1))
        .limit(java.time.temporal.ChronoUnit.DAYS.between(latestDate, todaysDate.minusDays(1)))
        .collect(Collectors.toList());
  }

  public Long getDiscordUserIdForThisEmployee(
      List<EmployeeCodes> employeeCodesData,
      Short employeeId) {

    return employeeCodesData
        .stream()
        .filter(employeeCodes -> employeeCodes.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getDiscordUserId)
        .findFirst()
        .orElse(null);
  }

  public void processDiscordMessagesCount(Long dcUserId, List<LocalDate> allDates) {

    RestTemplate restTemplate = new RestTemplate();
    DcBotConfig dcBotConfig = new DcBotConfig();

    String botApiUrl = dcBotConfig.getDcBotApi();

    if (botApiUrl == null || !botApiUrl.startsWith("http")) {
      System.err.println("Invalid bot API URL: " + botApiUrl);
      return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    for (LocalDate date : allDates) {
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("user_id", dcUserId);
      requestBody.put("message_date", date.format(formatter));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

      try {
        ResponseEntity<String> response = restTemplate.postForEntity(botApiUrl, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
          System.out.println("Response: " + response.getBody());
        } else {
          System.err.println("Error: " + response.getBody());
        }
      } catch (RestClientException e) {
        System.err.println("Error making POST request: " + e.getMessage());
      }
    }
  }

  private void startBot() {
    try {
      new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", "python",
          Paths.get("discordBotPy", "bot.py").toString()).start();
      System.out.println("Bot started in a new terminal window.");
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  private void stopBot() {
    try {
      new ProcessBuilder("cmd.exe", "/c", "taskkill", "/F", "/IM", "python.exe", "/T").start();
      System.out.println("Bot stopped and terminal window closed.");
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
