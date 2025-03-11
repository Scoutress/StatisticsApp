package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import jakarta.transaction.Transactional;

@Service
public class TimeOfDayPlaytimeServiceImpl implements TimeOfDayPlaytimeService {

  private final int BATCH_SIZE = 1000;
  private final int PAGE_SIZE = 5000;

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

    truncateAllTimeOfDaySegments();

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

  @Transactional
  public void truncateAllTimeOfDaySegments() {
    timeOfDaySegmentsRepository.deleteAll();
  }

  private void processSessions(List<LoginLogoutTimes> sessions) {
    if (sessions == null || sessions.isEmpty()) {
      System.err.println("No sessions to process!");
      return;
    }

    sessions.parallelStream().forEach(this::processSingleSession);
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

    truncateAllSegmentData();

    List<Object[]> results = timeOfDaySegmentsRepository.findAllSegmentCounts();

    Set<String> validServers = getValidServers();
    Set<Short> validEmployeeIds = getValidEmployeeIds();

    Map<Short, Map<String, Map<Integer, Integer>>> employeeServerData = new HashMap<>();

    for (Object[] row : results) {
      Short employeeId = (Short) row[0];
      String server = (String) row[1];
      int timeSegment = (int) row[2];
      int count = ((Number) row[3]).intValue();

      if (!validServers.contains(server.trim().toLowerCase())) {
        System.err.println("Invalid server name: " + server + " for Employee ID: " + employeeId);
        continue;
      }

      if (!validEmployeeIds.contains(employeeId)) {
        System.err.println("Invalid Employee ID: " + employeeId + " for Server: " + server);
        continue;
      }

      employeeServerData
          .computeIfAbsent(employeeId, k -> new HashMap<>())
          .computeIfAbsent(server, k -> new HashMap<>())
          .put(timeSegment, count);
    }

    System.out.println("Data aggregation completed. Starting to save segment counts...");

    employeeServerData.forEach((employeeId, serverData) -> {
      serverData.forEach((server, segmentCounts) -> {
        saveSegmentCounts(segmentCounts, employeeId, server);
      });

      Map<Integer, Integer> combinedCounts = new HashMap<>();
      serverData.forEach((server, segmentCounts) -> {
        segmentCounts.forEach((minute, count) -> {
          combinedCounts.merge(minute, count, Integer::sum);
        });
      });

      saveAllServerSegmentCounts(combinedCounts, employeeId);
    });

    System.out.println("Completed processing of time-of-day playtime.");
  }

  @Transactional
  public void truncateAllSegmentData() {
    segmentCountByServerRepository.deleteAll();
    segmentCountAllServersRepository.deleteAll();
  }

  private void saveSegmentCounts(Map<Integer, Integer> segmentCounts, Short employeeId, String server) {
    List<SegmentCountByServer> batch = new ArrayList<>();

    for (Map.Entry<Integer, Integer> entry : segmentCounts.entrySet()) {
      SegmentCountByServer segment = new SegmentCountByServer();
      segment.setEmployeeId(employeeId);
      segment.setServerName(server);
      segment.setTimeSegment(entry.getKey());
      segment.setCount(entry.getValue());
      batch.add(segment);

      if (batch.size() >= BATCH_SIZE) {
        segmentCountByServerRepository.saveAll(batch);
        batch.clear();
      }
    }

    if (!batch.isEmpty()) {
      segmentCountByServerRepository.saveAll(batch);
    }
  }

  private void saveAllServerSegmentCounts(Map<Integer, Integer> segmentCounts, Short employeeId) {
    List<SegmentCountAllServers> batch = new ArrayList<>();

    for (Map.Entry<Integer, Integer> entry : segmentCounts.entrySet()) {
      SegmentCountAllServers segment = new SegmentCountAllServers();
      segment.setEmployeeId(employeeId);
      segment.setTimeSegment(entry.getKey());
      segment.setCount(entry.getValue());
      batch.add(segment);

      if (batch.size() >= BATCH_SIZE) {
        segmentCountAllServersRepository.saveAll(batch);
        batch.clear();
      }
    }

    if (!batch.isEmpty()) {
      segmentCountAllServersRepository.saveAll(batch);
    }
  }

  private Set<String> getValidServers() {
    return new HashSet<>(Arrays.asList("survival", "skyblock", "creative", "boxpvp", "prison", "events", "lobby"));
  }

  private Set<Short> getValidEmployeeIds() {
    return employeeRepository
        .findAll()
        .stream()
        .map(Employee::getId)
        .collect(Collectors.toSet());
  }
}
