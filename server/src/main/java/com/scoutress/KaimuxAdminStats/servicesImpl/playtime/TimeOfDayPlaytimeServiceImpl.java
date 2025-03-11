package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountAllServers;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountByServer;
import com.scoutress.KaimuxAdminStats.entity.playtime.TimeOfDaySegments;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountAllServersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountByServerRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.TimeOfDaySegmentsRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.TimeOfDayPlaytimeService;
import com.scoutress.KaimuxAdminStats.utils.TimeUtils;

@Service
public class TimeOfDayPlaytimeServiceImpl implements TimeOfDayPlaytimeService {

  private final int BATCH_SIZE = 10000;
  private final int PAGE_SIZE = 500;

  private final TimeOfDaySegmentsRepository timeOfDaySegmentsRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;
  private final EmployeeRepository employeeRepository;
  private final SegmentCountByServerRepository segmentCountByServerRepository;
  private final SegmentCountAllServersRepository segmentCountAllServersRepository;

  public TimeOfDayPlaytimeServiceImpl(
      TimeOfDaySegmentsRepository timeOfDaySegmentsRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository,
      EmployeeRepository employeeRepository,
      SegmentCountByServerRepository segmentCountByServerRepository,
      SegmentCountAllServersRepository segmentCountAllServersRepository) {
    this.timeOfDaySegmentsRepository = timeOfDaySegmentsRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
    this.employeeRepository = employeeRepository;
    this.segmentCountByServerRepository = segmentCountByServerRepository;
    this.segmentCountAllServersRepository = segmentCountAllServersRepository;
  }

  @Override
  public void handleTimeOfDayPlaytime() {
    System.out.println("Starting batch processing of time-of-day playtime.");

    int totalRecords = (int) loginLogoutTimesRepository.count();
    int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
    int processedRecords = 0;

    for (int page = 0; page < totalPages; page++) {
      Page<LoginLogoutTimes> pageData = loginLogoutTimesRepository.findAll(PageRequest.of(page, PAGE_SIZE));
      processSessions(pageData.getContent());
      processedRecords += pageData.getNumberOfElements();

      int progress = (int) (((double) processedRecords / totalRecords) * 100);
      System.out.println("Progress: " + progress + "% (Processed " + processedRecords + "/" + totalRecords + ")");
    }

    System.out.println("Time-of-day playtime processing completed.");
  }

  private void processSessions(List<LoginLogoutTimes> sessions) {
    if (sessions == null || sessions.isEmpty()) {
      System.err.println("No sessions to process!");
      return;
    }

    for (LoginLogoutTimes session : sessions) {
      processSingleSession(session);
    }
  }

  private void processSingleSession(LoginLogoutTimes session) {
    if (session == null) {
      System.err.println("Skipping NULL session.");
      return;
    }

    if (!isSessionValid(session)) {
      System.err.println("Skipping invalid session. Employee ID: " + session.getEmployeeId());
      return;
    }

    saveSessionSegments(session);
  }

  private boolean isSessionValid(LoginLogoutTimes session) {
    return session.getServerName() != null &&
        !session.getServerName().isEmpty() &&
        session.getLoginTime() != null &&
        session.getLogoutTime() != null &&
        isServerNameValid(session.getServerName());
  }

  private boolean isServerNameValid(String serverName) {
    List<String> validServerNames = List.of("survival", "skyblock", "creative", "boxpvp", "prison", "events", "lobby");
    return validServerNames.contains(serverName.toLowerCase());
  }

  private void saveSessionSegments(LoginLogoutTimes session) {
    List<TimeOfDaySegments> segmentsToSave = new ArrayList<>();

    short employeeId = session.getEmployeeId();
    String serverName = session.getServerName();
    LocalDate sessionDate = session.getLoginTime().toLocalDate();
    int sessionStartMinute = TimeUtils.toMinutesOfDay(session.getLoginTime());
    int sessionEndMinute = TimeUtils.toMinutesOfDay(session.getLogoutTime());

    for (int minute = sessionStartMinute; minute <= sessionEndMinute; minute++) {
      segmentsToSave.add(new TimeOfDaySegments(employeeId, serverName, sessionDate, minute));

      if (segmentsToSave.size() >= BATCH_SIZE) {
        saveSegments(segmentsToSave);
        segmentsToSave.clear();
      }
    }

    if (!segmentsToSave.isEmpty()) {
      saveSegments(segmentsToSave);
    }
  }

  private void saveSegments(List<TimeOfDaySegments> segments) {
    timeOfDaySegmentsRepository.saveAll(segments);
  }

  @Override
  public void handleProcessedTimeOfDayPlaytime(List<String> servers) {
    System.out.println("Starting processing of time-of-day playtime...");

    List<Short> employeeIds = getAllEmployeeIds();
    System.out.println("Fetched " + employeeIds.size() + " employee IDs.");

    for (Short employeeId : employeeIds) {
      System.out.println("Processing employee ID: " + employeeId);

      for (String server : servers) {
        System.out.println("Processing server: " + server + " for employee ID: " + employeeId);
        processEmployeeServerSegments(employeeId, server);
      }

      System.out.println("Processing combined segments for all servers for employee ID: " + employeeId);
      processEmployeeAllServersSegments(employeeId);
    }

    System.out.println("Completed processing of time-of-day playtime.");
  }

  private void processEmployeeServerSegments(Short employeeId, String server) {
    System.out.println("Starting segment processing for server: " + server + " and employee ID: " + employeeId);

    Map<Integer, Integer> segmentCounts = new HashMap<>();

    for (int minute = 1; minute <= 1440; minute++) {
      int count = getSegmentCount(employeeId, server, minute);

      if (count > 0) {
        segmentCounts.put(minute, count);
        System.out.println(
            "Employee ID: " + employeeId + ", Server: " + server + ", Minute: " + minute + " -> Count: " + count);
      }
    }

    System.out.println("Saving segment counts for server: " + server + " and employee ID: " + employeeId);
    saveSegmentCounts(segmentCounts, employeeId, server);
  }

  private void processEmployeeAllServersSegments(Short employeeId) {
    System.out.println("Starting combined segment processing for all servers for employee ID: " + employeeId);

    Map<Integer, Integer> segmentCounts = new HashMap<>();

    for (int minute = 1; minute <= 1440; minute++) {
      int totalCount = 0;

      for (String server : List.of("survival", "skyblock", "creative", "boxpvp", "prison", "events", "lobby")) {
        totalCount += getSegmentCount(employeeId, server, minute);
      }

      if (totalCount > 0) {
        segmentCounts.put(minute, totalCount);
        System.out.println(
            "Employee ID: " + employeeId + ", Minute: " + minute + " -> Total Count Across Servers: " + totalCount);
      }
    }

    System.out.println("Saving combined segment counts for all servers for employee ID: " + employeeId);
    saveAllServerSegmentCounts(segmentCounts, employeeId);
  }

  private int getSegmentCount(Short employeeId, String server, int minute) {
    int count = (int) timeOfDaySegmentsRepository.countByEmployeeIdAndServerAndTimeSegment(employeeId, server, minute);
    System.out.println("Count fetched for Employee ID: " + employeeId + ", Server: " + server + ", Minute: " + minute
        + " -> Count: " + count);
    return count;
  }

  private void saveSegmentCounts(Map<Integer, Integer> segmentCounts, Short employeeId, String server) {
    for (Map.Entry<Integer, Integer> entry : segmentCounts.entrySet()) {
      SegmentCountByServer segment = new SegmentCountByServer();
      segment.setEmployeeId(employeeId);
      segment.setServerName(server);
      segment.setTimeSegment(entry.getKey());
      segment.setCount(entry.getValue());

      segmentCountByServerRepository.save(segment);

      System.out.println("Saved segment for Employee ID: " + employeeId + ", Server: " + server + ", Minute: "
          + entry.getKey() + " -> Count: " + entry.getValue());
    }
  }

  private void saveAllServerSegmentCounts(Map<Integer, Integer> segmentCounts, Short employeeId) {
    for (Map.Entry<Integer, Integer> entry : segmentCounts.entrySet()) {
      SegmentCountAllServers segment = new SegmentCountAllServers();
      segment.setEmployeeId(employeeId);
      segment.setTimeSegment(entry.getKey());
      segment.setCount(entry.getValue());

      segmentCountAllServersRepository.save(segment);

      System.out.println("Saved combined segment for Employee ID: " + employeeId + ", Minute: " + entry.getKey()
          + " -> Total Count: " + entry.getValue());
    }
  }

  private List<Short> getAllEmployeeIds() {
    System.out.println("Fetching all employee IDs...");
    return employeeRepository.findAll()
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }
}
