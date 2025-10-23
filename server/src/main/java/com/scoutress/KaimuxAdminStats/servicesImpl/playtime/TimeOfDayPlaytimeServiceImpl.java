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
  private static final int FETCH_SIZE = 5_000; // turi atitikti repo metodą
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
    long scheduled = 0;

    while (true) {
      long batchFetchStart = System.currentTimeMillis();
      List<LoginLogoutTimes> batch = loginLogoutTimesRepository.findTop5000ByIdGreaterThanOrderByIdAsc(lastId);
      long fetchElapsed = System.currentTimeMillis() - batchFetchStart;

      if (batch.isEmpty()) {
        log.info("📦 No more sessions to stream (lastId={}).", lastId);
        break;
      }

      long minId = batch.get(0).getId();
      long maxId = batch.get(batch.size() - 1).getId();
      batchIndex++;

      if (log.isDebugEnabled()) {
        log.debug("📥 Loaded batch #{}, size={}, idRange=[{}..{}], fetchMs={}",
            batchIndex, batch.size(), minId, maxId, fetchElapsed);
      }
      if (log.isTraceEnabled()) {
        for (LoginLogoutTimes s : batch) {
          log.trace("  • [LOAD] id={}, emp={}, srv='{}', login={}, logout={}",
              s.getId(), s.getEmployeeId(), s.getServerName(), s.getLoginTime(), s.getLogoutTime());
        }
      }

      final int currentBatchIndex = batchIndex;
      final List<LoginLogoutTimes> safeBatch = new ArrayList<>(batch);

      futures.add(executor.submit(() -> processBatch(currentBatchIndex, minId, maxId, safeBatch)));
      scheduled++;

      lastId = maxId;
    }

    log.info("🧵 Scheduled {} batch tasks. Waiting for completion...", scheduled);

    int totalInserted = 0;
    int done = 0;
    for (Future<Integer> f : futures) {
      try {
        int inserted = f.get();
        totalInserted += inserted;
        done++;
        if (done % 5 == 0 || done == futures.size()) {
          log.info("Progress: finished {}/{} batches (inserted so far: {})", done, futures.size(), totalInserted);
        }
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

    if (log.isTraceEnabled()) {
      for (LoginLogoutTimes s : sessions) {
        log.trace("[Batch {}] SESSION id={} emp={} srv='{}' login={} logout={}",
            batchIndex, s.getId(), s.getEmployeeId(), s.getServerName(), s.getLoginTime(), s.getLogoutTime());
      }
    }

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

    int processedSessions = 0;
    for (LoginLogoutTimes s : sessions) {
      processedSessions++;

      if (!isSessionValid(s)) {
        if (log.isTraceEnabled()) {
          log.trace("⏭️ Skipping invalid session id={} emp={} srv='{}' login={} logout={}",
              s.getId(), s.getEmployeeId(), s.getServerName(), s.getLoginTime(), s.getLogoutTime());
        }
        continue;
      }

      try {
        short employeeId = s.getEmployeeId();
        String server = s.getServerName().toLowerCase(Locale.ROOT).trim();
        LocalDateTime login = s.getLoginTime();
        LocalDateTime logout = s.getLogoutTime();

        if (logout.isBefore(login)) {
          if (log.isTraceEnabled()) {
            log.trace("⏭️ Negative window: id={} emp={} login{} > logout{}",
                s.getId(), employeeId, login, logout);
          }
          continue;
        }

        if (log.isDebugEnabled()) {
          log.debug("↪️ Splitting session id={} emp={} srv='{}' window=[{}..{}]",
              s.getId(), employeeId, server, login, logout);
        }

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
            if (log.isTraceEnabled()) {
              log.trace("⏭️ toMin<fromMin ({}<{}) at date {} — jumping to next day start",
                  toMin, fromMin, currentDate);
            }
            cursor = currentDate.plusDays(1).atStartOfDay();
            continue;
          }

          if (log.isDebugEnabled()) {
            log.debug("  • Day slice {} -> {} (mins {}..{}) for emp={} srv='{}'",
                from, to, fromMin, toMin, employeeId, server);
          }

          for (int m = fromMin; m <= toMin; m++) {
            if (log.isTraceEnabled()) {
              log.trace("    + ROW employee={} server='{}' date={} minute={}", employeeId, server, currentDate, m);
            }
            batchArgs.add(new Object[] { employeeId, server, Date.valueOf(currentDate), m });

            if (batchArgs.size() >= BATCH_SIZE) {
              if (log.isDebugEnabled()) {
                Object[] first = batchArgs.get(0);
                Object[] last = batchArgs.get(batchArgs.size() - 1);
                log.debug("💾 Flushing batchArgs size={} (first={}, last={})",
                    batchArgs.size(), Arrays.toString(first), Arrays.toString(last));
              }
              totalInserted += flushSegments(batchArgs);
            }
          }
          cursor = currentDate.plusDays(1).atStartOfDay();
        }

      } catch (Exception e) {
        log.error("❌ Failed to process session ID {}: {}", s.getId(), e.getMessage(), e);
      }

      if (processedSessions % 1000 == 0) {
        log.info("… processed {} sessions in current batch so far", processedSessions);
      }
    }

    if (!batchArgs.isEmpty()) {
      if (log.isDebugEnabled()) {
        Object[] first = batchArgs.get(0);
        Object[] last = batchArgs.get(batchArgs.size() - 1);
        log.debug("💾 Final flush of remaining {} rows (first={}, last={})",
            batchArgs.size(), Arrays.toString(first), Arrays.toString(last));
      }
      totalInserted += flushSegments(batchArgs);
    }
    return totalInserted;
  }

  private int flushSegments(List<Object[]> batchArgs) {
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        if (log.isDebugEnabled()) {
          log.debug("➡️ JDBC batchUpdate start (rows={}, attempt={})", batchArgs.size(), attempt);
        }
        int[] counts = jdbcTemplate.batchUpdate(INSERT_SEGMENT_SQL, batchArgs);
        int total = Arrays.stream(counts).sum();

        if (log.isDebugEnabled()) {
          log.debug("⬅️ JDBC batchUpdate ok (inserted={}, attempt={})", total, attempt);
        }
        if (log.isTraceEnabled()) {
          for (int i = 0; i < Math.min(batchArgs.size(), 10); i++) {
            log.trace("    [SAMPLE {}] {}", i, Arrays.toString(batchArgs.get(i)));
          }
        }

        batchArgs.clear();
        return total;
      } catch (DataAccessException e) {
        log.warn("⚠️ Batch insert failed (attempt {}/{} | rows={}): {}",
            attempt, MAX_RETRIES, batchArgs.size(), e.getMessage(), e);
        if (attempt < MAX_RETRIES) {
          long delay = 500L * attempt;
          log.debug("⏳ Retrying in {} ms...", delay);
          LockSupport.parkNanos(delay * 1_000_000);
        }
      }
    }
    log.error("❌ Batch insert failed after {} retries. Skipping batch ({} rows)", MAX_RETRIES, batchArgs.size());
    if (log.isTraceEnabled()) {
      for (Object[] row : batchArgs) {
        log.trace("    [SKIPPED] {}", Arrays.toString(row));
      }
    }
    batchArgs.clear();
    return 0;
  }

  private boolean isSessionValid(LoginLogoutTimes s) {
    if (s == null) {
      if (log.isTraceEnabled())
        log.trace("Invalid session: null");
      return false;
    }
    if (s.getLoginTime() == null || s.getLogoutTime() == null) {
      if (log.isTraceEnabled())
        log.trace("Invalid session id={}: login/logout is null", s.getId());
      return false;
    }
    if (s.getServerName() == null || s.getServerName().isBlank()) {
      if (log.isTraceEnabled())
        log.trace("Invalid session id={}: server is blank", s.getId());
      return false;
    }
    boolean ok = VALID_SERVERS.contains(s.getServerName().toLowerCase(Locale.ROOT).trim());
    if (!ok && log.isTraceEnabled()) {
      log.trace("Invalid session id={}: server '{}' not in {}", s.getId(), s.getServerName(), VALID_SERVERS);
    }
    return ok;
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
    log.info("Loaded {} aggregated rows from time_of_day_segments", counts.size());
    if (counts.isEmpty()) {
      log.warn("⚠ No segment data found — skipping aggregation.");
      return;
    }

    Set<Short> validEmployees = employeeRepository
        .findAll()
        .stream()
        .map(Employee::getId)
        .collect(Collectors.toSet());

    if (log.isDebugEnabled()) {
      log.debug("Valid employees loaded: {}", validEmployees.size());
    }
    if (log.isTraceEnabled()) {
      log.trace("Valid employee IDs: {}", validEmployees);
    }

    Map<Short, Map<String, Map<Integer, Integer>>> data = new HashMap<>();
    int scanned = 0;
    for (Object[] row : counts) {
      Short empId = (Short) row[0];
      String server = ((String) row[1]).toLowerCase(Locale.ROOT).trim();
      int segment = (int) row[2];
      int count = ((Number) row[3]).intValue();

      scanned++;
      if (log.isTraceEnabled()) {
        log.trace("[SCAN {}] emp={} srv='{}' seg={} count={}", scanned, empId, server, segment, count);
      }

      if (!VALID_SERVERS.contains(server) || !validEmployees.contains(empId)) {
        if (log.isTraceEnabled()) {
          log.trace("  ⏭️ filtered out (validSrv={}, validEmp={})",
              VALID_SERVERS.contains(server), validEmployees.contains(empId));
        }
        continue;
      }

      data.computeIfAbsent(empId, e -> new HashMap<>())
          .computeIfAbsent(server, s -> new HashMap<>())
          .merge(segment, count, Integer::sum);
    }

    log.info("Data aggregated. Starting batch save ({} employees)...", data.size());
    int processed = 0;
    for (var entry : data.entrySet()) {
      Short empId = entry.getKey();
      Map<String, Map<Integer, Integer>> serverMap = entry.getValue();

      if (log.isDebugEnabled()) {
        log.debug("Saving per-server counts for emp={} (servers={})", empId, serverMap.keySet());
      }
      if (log.isTraceEnabled()) {
        for (var srvEntry : serverMap.entrySet()) {
          String srv = srvEntry.getKey();
          Map<Integer, Integer> cmap = srvEntry.getValue();
          log.trace("  • emp={} srv='{}' segments={}", empId, srv, cmap.size());
        }
      }

      serverMap.forEach((server, countsMap) -> saveSegmentCounts(countsMap, empId, server));

      Map<Integer, Integer> merged = mergeServerCounts(serverMap);
      if (log.isTraceEnabled()) {
        log.trace("  • emp={} [ALL_SERVERS] merged segments={}", empId, merged.size());
      }
      saveAllServerSegmentCounts(merged, empId);

      if (++processed % 50 == 0 || processed == data.size())
        log.info("Progress: processed {}/{} employees...", processed, data.size());
    }

    log.info("✅ Aggregation completed in {} s", (System.currentTimeMillis() - start) / 1000);
  }

  @Transactional
  public void truncateAllSegmentData() {
    log.info("🧹 Clearing segment aggregates (byServer & allServers)...");
    segmentCountByServerRepository.deleteAllInBatch();
    segmentCountAllServersRepository.deleteAllInBatch();
    log.info("✔ Cleared aggregates.");
  }

  private void saveSegmentCounts(Map<Integer, Integer> counts, Short empId, String server) {
    List<SegmentCountByServer> buffer = new ArrayList<>(Math.min(counts.size(), BATCH_SIZE));
    int added = 0;
    for (var entry : counts.entrySet()) {
      if (log.isTraceEnabled()) {
        log.trace("[SAVE byServer] emp={} srv='{}' seg={} cnt={}",
            empId, server, entry.getKey(), entry.getValue());
      }
      buffer.add(new SegmentCountByServer(empId, server, entry.getKey(), entry.getValue()));
      added++;
      if (buffer.size() >= BATCH_SIZE) {
        segmentCountByServerRepository.saveAll(buffer);
        if (log.isDebugEnabled()) {
          log.debug("💾 Flushed {} SegmentCountByServer rows (emp={}, srv='{}')", buffer.size(), empId, server);
        }
        buffer.clear();
      }
    }
    if (!buffer.isEmpty()) {
      segmentCountByServerRepository.saveAll(buffer);
      if (log.isDebugEnabled()) {
        log.debug("💾 Final flush {} SegmentCountByServer rows (emp={}, srv='{}', added={})",
            buffer.size(), empId, server, added);
      }
    }
  }

  private void saveAllServerSegmentCounts(Map<Integer, Integer> counts, Short empId) {
    List<SegmentCountAllServers> buffer = new ArrayList<>(Math.min(counts.size(), BATCH_SIZE));
    int added = 0;
    for (var entry : counts.entrySet()) {
      if (log.isTraceEnabled()) {
        log.trace("[SAVE allServers] emp={} seg={} cnt={}", empId, entry.getKey(), entry.getValue());
      }
      buffer.add(new SegmentCountAllServers(empId, entry.getKey(), entry.getValue()));
      added++;
      if (buffer.size() >= BATCH_SIZE) {
        segmentCountAllServersRepository.saveAll(buffer);
        if (log.isDebugEnabled()) {
          log.debug("💾 Flushed {} SegmentCountAllServers rows (emp={})", buffer.size(), empId);
        }
        buffer.clear();
      }
    }
    if (!buffer.isEmpty()) {
      segmentCountAllServersRepository.saveAll(buffer);
      if (log.isDebugEnabled()) {
        log.debug("💾 Final flush {} SegmentCountAllServers rows (emp={}, added={})", buffer.size(), empId, added);
      }
    }
  }

  private Map<Integer, Integer> mergeServerCounts(Map<String, Map<Integer, Integer>> serverMap) {
    Map<Integer, Integer> merged = new HashMap<>();
    serverMap.values().forEach(map -> map.forEach((minute, count) -> merged.merge(minute, count, Integer::sum)));
    return merged;
  }
}
