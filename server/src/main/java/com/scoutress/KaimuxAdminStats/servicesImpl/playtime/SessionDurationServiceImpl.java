package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.entity.playtime.RawPlaytimeSession;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;

@Service
public class SessionDurationServiceImpl implements SessionDurationService {

  private static final Logger log = LoggerFactory.getLogger(SessionDurationServiceImpl.class);

  private final JdbcTemplate jdbcTemplate;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  private final EmployeeRepository employeeRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;

  public SessionDurationServiceImpl(
      JdbcTemplate jdbcTemplate,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      EmployeeRepository employeeRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.employeeRepository = employeeRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
  }

  // ===============================================================
  // LOGIN / LOGOUT HANDLING
  // ===============================================================
  @Override
  @Transactional
  public void processLoginLogouts(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("=== [START] Login/Logout processing for {} servers ===", servers.size());

    Map<String, Short> usernameToEmployee = employeeRepository.findAll().stream()
        .collect(Collectors.toMap(Employee::getUsername, Employee::getId));

    for (String server : servers) {
      long serverStart = System.currentTimeMillis();
      log.info("▶️ Processing login/logout for server '{}'", server);

      try {
        List<RawPlaytimeSession> sessions = fetchAllPlaytimeSessions(server);
        if (sessions.isEmpty()) {
          log.warn("⚠️ No raw playtime data found for {}", server);
          continue;
        }

        Map<Integer, List<RawPlaytimeSession>> sessionsByUser = sessions.stream()
            .collect(Collectors.groupingBy(RawPlaytimeSession::getUserId));

        int processedUsers = 0;
        for (Map.Entry<Integer, List<RawPlaytimeSession>> entry : sessionsByUser.entrySet()) {
          int rawUserId = entry.getKey();
          String username = getUsernameByUserId(server, rawUserId);

          if (username == null || !usernameToEmployee.containsKey(username))
            continue;

          Short employeeId = usernameToEmployee.get(username);
          processUserSessions(entry.getValue(), server, employeeId);
          processedUsers++;

          if (processedUsers % 100 == 0) {
            log.debug("Server {} — processed {} users so far", server, processedUsers);
          }
        }

        log.info("✅ Server '{}' — processed {} users", server, processedUsers);

      } catch (Exception e) {
        log.error("❌ Error processing server '{}': {}", server, e.getMessage(), e);
      }

      log.info("⏱ Completed server '{}' in {} ms", server, System.currentTimeMillis() - serverStart);
    }

    log.info("✅ [END] Login/logout processing finished in {} ms", System.currentTimeMillis() - start);
  }

  private List<RawPlaytimeSession> fetchAllPlaytimeSessions(String server) {
    String table = "raw_playtime_sessions_data_" + server.toLowerCase();
    String sql = "SELECT id, user_id, time, action FROM " + table;
    return jdbcTemplate.query(sql, (rs, i) -> new RawPlaytimeSession(
        rs.getLong("id"),
        rs.getInt("user_id"),
        rs.getInt("time"),
        rs.getInt("action")));
  }

  private String getUsernameByUserId(String server, int userId) {
    String sql = "SELECT username FROM raw_user_data_" + server.toLowerCase() + " WHERE user_id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, String.class, userId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  private void processUserSessions(List<RawPlaytimeSession> sessions, String server, Short employeeId) {
    sessions.sort(Comparator.comparing(RawPlaytimeSession::getTime));

    for (int i = 0; i < sessions.size() - 1; i++) {
      RawPlaytimeSession current = sessions.get(i);
      RawPlaytimeSession next = sessions.get(i + 1);

      if (current.getAction() == 1 && next.getAction() == 0 && next.getTime() > current.getTime()) {
        LocalDateTime login = toDateTime(current.getTime());
        LocalDateTime logout = toDateTime(next.getTime());
        Duration duration = Duration.between(login, logout);

        // Skip abnormal sessions (>24h, negative or zero duration)
        if (duration.isNegative() || duration.isZero() || duration.toHours() > 24) {
          log.trace("⚠️ Abnormal session for Employee {} | {} | Duration: {}", employeeId, server, duration);
          continue;
        }

        saveLoginLogout(employeeId, server, login, logout);
      }
    }
  }

  private LocalDateTime toDateTime(long epoch) {
    return Instant.ofEpochSecond(epoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  private void saveLoginLogout(Short employeeId, String server, LocalDateTime login, LocalDateTime logout) {
    try {
      LoginLogoutTimes record = new LoginLogoutTimes();
      record.setEmployeeId(employeeId);
      record.setServerName(server);
      record.setLoginTime(login);
      record.setLogoutTime(logout);
      loginLogoutTimesRepository.save(record);
      log.trace("Employee {} | {} — login={}, logout={}", employeeId, server, login, logout);
    } catch (Exception e) {
      log.error("❌ Failed to save login/logout for emp {} on {}: {}", employeeId, server, e.getMessage());
    }
  }

  // ===============================================================
  // SESSION DURATION HANDLING
  // ===============================================================
  @Override
  public void processSessions(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("=== [START] Session duration calculation ===");

    for (String server : servers) {
      long serverStart = System.currentTimeMillis();
      log.info("▶️ Processing session durations for server '{}'", server);

      try {
        List<LoginLogoutTimes> logs = loginLogoutTimesRepository.findAll().stream()
            .filter(l -> l.getServerName().equals(server))
            .sorted(Comparator.comparing(LoginLogoutTimes::getLoginTime))
            .toList();

        if (logs.isEmpty()) {
          log.warn("⚠️ No login/logout entries found for {}", server);
          continue;
        }

        List<SessionDuration> sessions = new ArrayList<>();

        for (LoginLogoutTimes logEntity : logs) {
          long duration = Duration.between(logEntity.getLoginTime(), logEntity.getLogoutTime()).getSeconds();
          if (duration <= 0 || duration > 86400) // >24h invalid
            continue;

          SessionDuration sd = new SessionDuration();
          sd.setEmployeeId(logEntity.getEmployeeId());
          sd.setServer(server);
          sd.setDate(logEntity.getLoginTime().toLocalDate());
          sd.setSingleSessionDurationInSec((int) duration);
          sessions.add(sd);
        }

        if (!sessions.isEmpty()) {
          processedPlaytimeSessionsRepository.saveAll(sessions);
          log.info("💾 Saved {} valid sessions for {}", sessions.size(), server);
        } else {
          log.info("ℹ️ No valid sessions to save for {}", server);
        }

      } catch (Exception e) {
        log.error("❌ Error processing session durations for '{}': {}", server, e.getMessage(), e);
      }

      log.info("⏱ Completed server '{}' in {} ms", server, System.currentTimeMillis() - serverStart);
    }

    log.info("✅ [END] Session duration processing finished in {} ms", System.currentTimeMillis() - start);
  }

  // ===============================================================
  // DUPLICATE REMOVAL
  // ===============================================================
  @Override
  public void removeLoginLogoutsDupe() {
    log.info("🧹 Removing duplicate login/logout entries...");
    removeDuplicates(loginLogoutTimesRepository.findAll(), loginLogoutTimesRepository, "login/logout",
        e -> ((LoginLogoutTimes) e).getEmployeeId() + "|" + ((LoginLogoutTimes) e).getServerName() + "|"
            + ((LoginLogoutTimes) e).getLoginTime() + "|" + ((LoginLogoutTimes) e).getLogoutTime());
  }

  @Override
  public void removeDuplicateSessionData() {
    log.info("🧹 Removing duplicate session duration entries...");
    removeDuplicates(processedPlaytimeSessionsRepository.findAll(), processedPlaytimeSessionsRepository, "session",
        e -> ((SessionDuration) e).getEmployeeId() + "|" + ((SessionDuration) e).getServer() + "|"
            + ((SessionDuration) e).getDate() + "|" + ((SessionDuration) e).getSingleSessionDurationInSec());
  }

  private <T> void removeDuplicates(List<T> all, org.springframework.data.jpa.repository.JpaRepository<T, ?> repo,
      String type, java.util.function.Function<T, String> keyExtractor) {
    long start = System.currentTimeMillis();
    log.debug("Starting duplicate cleanup for {} entries ({})...", all.size(), type);

    Map<String, List<T>> grouped = all.stream().collect(Collectors.groupingBy(keyExtractor));
    List<T> toDelete = grouped.values().stream()
        .filter(list -> list.size() > 1)
        .flatMap(list -> list.subList(1, list.size()).stream())
        .toList();

    if (!toDelete.isEmpty()) {
      repo.deleteAllInBatch(toDelete);
      log.info("🗑 Removed {} duplicate {} entries.", toDelete.size(), type);
    } else {
      log.info("✅ No duplicate {} entries found.", type);
    }

    log.debug("Duplicate cleanup for '{}' took {} ms", type, System.currentTimeMillis() - start);
  }
}
