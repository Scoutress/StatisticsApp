package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountAllServers;
import com.scoutress.KaimuxAdminStats.entity.playtime.SegmentCountByServer;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountAllServersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountByServerRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.TimeOfDaySegmentsRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.TimeOfDayPlaytimeService;
import com.scoutress.KaimuxAdminStats.utils.TimeUtils;

@Service
public class TimeOfDayPlaytimeServiceImpl implements TimeOfDayPlaytimeService {

  private static final Logger log = LoggerFactory.getLogger(TimeOfDayPlaytimeServiceImpl.class);

  private static final int BATCH_SIZE = 1_000;
  private static final int FETCH_SIZE = 5_000;
  private static final int MAX_RETRIES = 3;
  private static final int THREAD_COUNT = Math.max(4, Runtime.getRuntime().availableProcessors() - 1);

  private static final Set<String> VALID_SERVERS = Set.of(
      "survival", "skyblock", "creative", "boxpvp", "prison", "events", "lobby");

  private static final String INSERT_SEGMENT_SQL = "INSERT INTO time_of_day_segments (employee_id, server, date, time_segment) VALUES (?, ?, ?, ?)";

  private final TimeOfDaySegmentsRepository timeOfDaySegmentsRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;
  private final EmployeeRepository employeeRepository;
  private final SegmentCountByServerRepository segmentCountByServerRepository;
  private final SegmentCountAllServersRepository segmentCountAllServersRepository;
  private final JdbcTemplate jdbcTemplate;

  public TimeOfDayPlaytimeServiceImpl(
      TimeOfDaySegmentsRepository timeOfDaySegmentsRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository,
      EmployeeRepository employeeRepository,
      SegmentCountByServerRepository segmentCountByServerRepository,
      SegmentCountAllServersRepository segmentCountAllServersRepository,
      JdbcTemplate jdbcTemplate) {
    this.timeOfDaySegmentsRepository = timeOfDaySegmentsRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
    this.employeeRepository = employeeRepository;
    this.segmentCountByServerRepository = segmentCountByServerRepository;
    this.segmentCountAllServersRepository = segmentCountAllServersRepository;
    this.jdbcTemplate = jdbcTemplate;
  }

  // ============================================================
  // STAGE 1: Raw sessions → minute-level segments
  // ============================================================
  @Override
  public void handleTimeOfDayPlaytime() {
    final long totalStart = System.currentTimeMillis();
    log.info("=== [START] Time-of-day segment generation (multi-threaded streaming) ===");

    truncateAllTimeOfDaySegments();

    final long totalRecords = loginLogoutTimesRepository.count();
    if (totalRecords == 0) {
      log.warn("⚠ No login/logout records found — skipping.");
      return;
    }

    log.info("Total records: {} | Using {} threads | Fetch size = {}", totalRecords, THREAD_COUNT, FETCH_SIZE);

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    List<Future<Integer>> futures = new ArrayList<>();

    long lastId = 0;
    int batchIndex = 0;

    while (true) {
      List<LoginLogoutTimes> batch = loginLogoutTimesRepository.findTop5000ByIdGreaterThanOrderByIdAsc(lastId);
      if (batch.isEmpty())
        break;

      long minId = batch.get(0).getId();
      long maxId = batch.get(batch.size() - 1).getId();
      batchIndex++;

      final int currentBatchIndex = batchIndex;
      final List<LoginLogoutTimes> safeBatch = new ArrayList<>(batch);

      futures.add(executor.submit(() -> processBatch(currentBatchIndex, minId, maxId, safeBatch)));

      lastId = maxId;
    }

    int totalInserted = 0;
    for (Future<Integer> f : futures) {
      try {
        totalInserted += f.get();
      } catch (InterruptedException | ExecutionException e) {
        log.error("❌ Thread task failed: {}", e.getMessage(), e);
      }
    }

    executor.shutdown();

    long elapsed = System.currentTimeMillis() - totalStart;
    log.info("✅ Finished processing {} sessions, total inserted rows: {} ({} s)",
        totalRecords, totalInserted, elapsed / 1000);
  }

  private int processBatch(int batchIndex, long minId, long maxId, List<LoginLogoutTimes> sessions) {
    long start = System.currentTimeMillis();
    log.info("▶️ [Batch {}] Processing {} sessions (IDs {}–{})", batchIndex, sessions.size(), minId, maxId);

    int inserted = processSessionsBatchJdbc(sessions);

    long elapsed = System.currentTimeMillis() - start;
    log.info("✅ [Batch {}] Done — {} rows inserted in {} ms ({} rows/s)",
        batchIndex, inserted, elapsed, (inserted * 1000L) / Math.max(1, elapsed));

    return inserted;
  }

  @Transactional
  public void truncateAllTimeOfDaySegments() {
    log.info("Truncating all TimeOfDaySegments...");
    try {
      jdbcTemplate.execute("TRUNCATE TABLE time_of_day_segments");
      log.info("✔ TimeOfDaySegments table truncated.");
    } catch (DataAccessException e) {
      log.warn("⚠ TRUNCATE failed, fallback to deleteAllInBatch: {}", e.getMessage());
      timeOfDaySegmentsRepository.deleteAllInBatch();
      log.info("✔ Table cleared using deleteAllInBatch.");
    }
  }

  private int processSessionsBatchJdbc(List<LoginLogoutTimes> sessions) {
    List<Object[]> batchArgs = new ArrayList<>(BATCH_SIZE * 2);
    int totalInserted = 0;

    for (LoginLogoutTimes s : sessions) {
      if (!isSessionValid(s))
        continue;

      try {
        short employeeId = s.getEmployeeId();
        String server = s.getServerName().toLowerCase(Locale.ROOT).trim();
        LocalDateTime login = s.getLoginTime();
        LocalDateTime logout = s.getLogoutTime();

        if (logout.isBefore(login))
          continue;

        LocalDateTime cursor = login;
        while (!cursor.toLocalDate().isAfter(logout.toLocalDate())) {
          LocalDate currentDate = cursor.toLocalDate();
          LocalDateTime dayStart = currentDate.atStartOfDay();
          LocalDateTime dayEnd = currentDate.atTime(23, 59, 59);

          LocalDateTime from = cursor.isBefore(dayStart) ? dayStart : cursor;
          LocalDateTime to = logout.isBefore(dayEnd) ? logout : dayEnd;

          int fromMin = TimeUtils.toMinutesOfDay(from);
          int toMin = TimeUtils.toMinutesOfDay(to);
          if (toMin < fromMin) {
            cursor = currentDate.plusDays(1).atStartOfDay();
            continue;
          }

          for (int m = fromMin; m <= toMin; m++) {
            batchArgs.add(new Object[] { employeeId, server, Date.valueOf(currentDate), m });
            if (batchArgs.size() >= BATCH_SIZE) {
              totalInserted += flushSegments(batchArgs);
            }
          }
          cursor = currentDate.plusDays(1).atStartOfDay();
        }

      } catch (Exception e) {
        log.error("❌ Failed to process session ID {}: {}", s.getId(), e.getMessage(), e);
      }
    }

    if (!batchArgs.isEmpty())
      totalInserted += flushSegments(batchArgs);
    return totalInserted;
  }

  private int flushSegments(List<Object[]> batchArgs) {
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        int[] counts = jdbcTemplate.batchUpdate(INSERT_SEGMENT_SQL, batchArgs);
        int total = Arrays.stream(counts).sum();
        log.debug("💾 Batch inserted {} rows successfully (attempt {})", total, attempt);
        batchArgs.clear();
        return total;
      } catch (DataAccessException e) {
        log.warn("⚠️ Batch insert failed (attempt {}/{}): {}", attempt, MAX_RETRIES, e.getMessage());
        if (attempt < MAX_RETRIES) {
          long delay = 500L * attempt;
          log.debug("⏳ Retrying in {} ms...", delay);
          LockSupport.parkNanos(delay * 1_000_000);
        }
      }
    }
    log.error("❌ Batch insert failed after {} retries. Skipping batch ({} rows)", MAX_RETRIES, batchArgs.size());
    batchArgs.clear();
    return 0;
  }

  private boolean isSessionValid(LoginLogoutTimes s) {
    if (s == null)
      return false;
    if (s.getLoginTime() == null || s.getLogoutTime() == null)
      return false;
    if (s.getServerName() == null || s.getServerName().isBlank())
      return false;
    return VALID_SERVERS.contains(s.getServerName().toLowerCase(Locale.ROOT).trim());
  }

  // ============================================================
  // STAGE 2: Aggregation → counts per segment per employee/server
  // ============================================================
  @Override
  public void handleProcessedTimeOfDayPlaytime(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("=== [START] Aggregating time-of-day playtime ===");

    truncateAllSegmentData();

    List<Object[]> counts = timeOfDaySegmentsRepository.findAllSegmentCounts();
    if (counts.isEmpty()) {
      log.warn("⚠ No segment data found — skipping aggregation.");
      return;
    }

    Set<Short> validEmployees = employeeRepository
        .findAll()
        .stream()
        .map(Employee::getId)
        .collect(Collectors.toSet());

    Map<Short, Map<String, Map<Integer, Integer>>> data = new HashMap<>();
    for (Object[] row : counts) {
      Short empId = (Short) row[0];
      String server = ((String) row[1]).toLowerCase(Locale.ROOT).trim();
      int segment = (int) row[2];
      int count = ((Number) row[3]).intValue();

      if (!VALID_SERVERS.contains(server) || !validEmployees.contains(empId))
        continue;

      data.computeIfAbsent(empId, e -> new HashMap<>())
          .computeIfAbsent(server, s -> new HashMap<>())
          .merge(segment, count, Integer::sum);
    }

    log.info("Data aggregated. Starting batch save ({} employees)...", data.size());
    int processed = 0;
    for (var entry : data.entrySet()) {
      Short empId = entry.getKey();
      Map<String, Map<Integer, Integer>> serverMap = entry.getValue();
      serverMap.forEach((server, countsMap) -> saveSegmentCounts(countsMap, empId, server));
      saveAllServerSegmentCounts(mergeServerCounts(serverMap), empId);
      if (++processed % 50 == 0)
        log.info("Progress: processed {}/{} employees...", processed, data.size());
    }

    log.info("✅ Aggregation completed in {} s", (System.currentTimeMillis() - start) / 1000);
  }

  @Transactional
  public void truncateAllSegmentData() {
    segmentCountByServerRepository.deleteAllInBatch();
    segmentCountAllServersRepository.deleteAllInBatch();
  }

  private void saveSegmentCounts(Map<Integer, Integer> counts, Short empId, String server) {
    List<SegmentCountByServer> buffer = new ArrayList<>(Math.min(counts.size(), BATCH_SIZE));
    for (var entry : counts.entrySet()) {
      buffer.add(new SegmentCountByServer(empId, server, entry.getKey(), entry.getValue()));
      if (buffer.size() >= BATCH_SIZE) {
        segmentCountByServerRepository.saveAll(buffer);
        buffer.clear();
      }
    }
    if (!buffer.isEmpty())
      segmentCountByServerRepository.saveAll(buffer);
  }

  private void saveAllServerSegmentCounts(Map<Integer, Integer> counts, Short empId) {
    List<SegmentCountAllServers> buffer = new ArrayList<>(Math.min(counts.size(), BATCH_SIZE));
    for (var entry : counts.entrySet()) {
      buffer.add(new SegmentCountAllServers(empId, entry.getKey(), entry.getValue()));
      if (buffer.size() >= BATCH_SIZE) {
        segmentCountAllServersRepository.saveAll(buffer);
        buffer.clear();
      }
    }
    if (!buffer.isEmpty())
      segmentCountAllServersRepository.saveAll(buffer);
  }

  private Map<Integer, Integer> mergeServerCounts(Map<String, Map<Integer, Integer>> serverMap) {
    Map<Integer, Integer> merged = new HashMap<>();
    serverMap.values().forEach(map -> map.forEach((minute, count) -> merged.merge(minute, count, Integer::sum)));
    return merged;
  }
}
