package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordBotService;

@Service
public class DiscordBotServiceImpl implements DiscordBotService {

  private final DcBotConfig dcBotConfig;
  private final RestTemplate restTemplate;
  private static final String PID_FILE_PATH = "bot.pid";
  private static final String BOT_SCRIPT_PATH = "discordBotPy/bot.py";
  private Process botProcess;
  private final EmployeeRepository employeeRepository;

  public DiscordBotServiceImpl(
      DcBotConfig dcBotConfig,
      EmployeeRepository employeeRepository) {
    this.dcBotConfig = dcBotConfig;
    this.restTemplate = new RestTemplate();
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void handleDcBotRequests(
      List<EmployeeCodes> employeeCodesData,
      LocalDate latestDateFromDcMsgsData,
      LocalDate todaysDate,
      List<Short> employeeIdsWithoutData) {

    List<Short> allEmployeeIds = getAllEmployeeIds(employeeCodesData);

    for (Short employeeId : allEmployeeIds) {
      Long dcUserId = getDiscordUserIdForThisEmployee(employeeCodesData, employeeId);

      if (employeeIdsWithoutData.contains(employeeId)) {

        LocalDate joinDate = checkEmployeeWhenStartedWorking(employeeId);
        List<LocalDate> allDatesFromJoinTillTodays = getAllDatesBetween(
            joinDate, todaysDate);
        processDiscordMessagesCount(dcUserId, allDatesFromJoinTillTodays);

      } else {

        List<LocalDate> allDatesFromLatestTillTodays = getAllDatesBetween(
            latestDateFromDcMsgsData, todaysDate);
        processDiscordMessagesCount(dcUserId, allDatesFromLatestTillTodays);
      }
    }
  }

  private List<Short> getAllEmployeeIds(List<EmployeeCodes> employeeCodesData) {
    return employeeCodesData
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  private List<LocalDate> getAllDatesBetween(
      LocalDate latestDate, LocalDate todaysDate) {

    return Stream
        .iterate(
            latestDate.plusDays(1),
            date -> date.plusDays(1))
        .limit(java.time.temporal.ChronoUnit.DAYS.between(latestDate, todaysDate.minusDays(1)))
        .collect(Collectors.toList());
  }

  private LocalDate checkEmployeeWhenStartedWorking(Short employeeId) {
    return employeeRepository
        .findAll()
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(null);
  }

  private Long getDiscordUserIdForThisEmployee(
      List<EmployeeCodes> employeeCodesData,
      Short employeeId) {

    return employeeCodesData
        .stream()
        .filter(employeeCodes -> employeeCodes.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getDiscordUserId)
        .findFirst()
        .orElse(null);
  }

  private void processDiscordMessagesCount(Long dcUserId, List<LocalDate> allDates) {
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

  @Override
  public void checkOrStartDiscordBot() {
    Path pidPath = Path.of(PID_FILE_PATH);

    try {
      if (Files.exists(pidPath)) {
        String pidStr = Files.readString(pidPath).trim();

        if (!pidStr.isEmpty()) {
          long pid = Long.parseLong(pidStr);
          boolean isRunning = isProcessRunning(pid);

          if (isRunning) {
            System.out.println("Bot is already running with PID: " + pid);
            return;
          } else {
            System.out.println("Found stale PID. Bot not running. Restarting...");
            Files.delete(pidPath);
          }
        }
      }

      startBot();

    } catch (IOException | NumberFormatException e) {
      System.err.println("Failed to verify or start bot: " + e.getMessage());
    }
  }

  private boolean isProcessRunning(long pid) {
    try {
      ProcessHandle handle = ProcessHandle.of(pid).orElse(null);
      return handle != null && handle.isAlive();
    } catch (Exception e) {
      return false;
    }
  }

  public void startBot() {
    String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    ProcessBuilder processBuilder;

    try {
      if (os.contains("win")) {
        processBuilder = new ProcessBuilder("python", BOT_SCRIPT_PATH);
      } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
        processBuilder = new ProcessBuilder("python3", BOT_SCRIPT_PATH);
      } else {
        throw new UnsupportedOperationException("Unsupported OS: " + os);
      }

      processBuilder.redirectErrorStream(true);
      botProcess = processBuilder.start();

      long pid = botProcess.pid();
      Files.writeString(Path.of(PID_FILE_PATH), Long.toString(pid));

      System.out.println("Bot started with PID: " + pid);
    } catch (IOException | UnsupportedOperationException e) {
      System.err.println("Failed to start bot: " + e.getMessage());
    }
  }

  @Override
  public void sleepForHalfMin() {
    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      System.err.println(e);
    }
  }

  public void stopBot() {
    try {
      Path pidPath = Path.of(PID_FILE_PATH);
      if (Files.exists(pidPath)) {
        String pidStr = Files.readString(pidPath).trim();
        long pid = Long.parseLong(pidStr);

        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        ProcessBuilder killBuilder;

        if (os.contains("win")) {
          killBuilder = new ProcessBuilder("taskkill", "/PID", String.valueOf(pid), "/F");
        } else {
          killBuilder = new ProcessBuilder("kill", "-9", String.valueOf(pid));
        }

        killBuilder.start();
        Files.deleteIfExists(pidPath);
        System.out.println("Bot stopped (PID: " + pid + ")");
      } else {
        System.out.println("PID file not found. Bot may not have been started.");
      }
    } catch (IOException | NumberFormatException e) {
      System.err.println("Failed to stop bot: " + e.getMessage());
    }
  }
}
