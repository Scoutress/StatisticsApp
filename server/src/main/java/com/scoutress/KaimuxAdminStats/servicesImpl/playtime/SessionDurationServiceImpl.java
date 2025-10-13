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
    log.info("=== Starting login/logout processing for {} servers ===", servers.size());

    Map<String, Short> usernameToEmployee = employeeRepository.findAll().stream()
        .collect(Collectors.toMap(Employee::getUsername, Employee::getId));

    for (String server : servers) {
      try {
        log.info("Processing server: {}", server);

        List<RawPlaytimeSession> sessions = fetchAllPlaytimeSessions(server);
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

          if (processedUsers % 100 == 0)
            log.info("Progress: processed {} users for {}", processedUsers, server);
        }
      } catch (Exception e) {
        log.error("❌ Error processing server {}: {}", server, e.getMessage(), e);
      }
    }

    long end = System.currentTimeMillis();
    log.info("✅ Login/logout processing completed in {} ms", end - start);
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
    } catch (Exception e) {
      log.error("❌ Failed to save login/logout for employee {} on {}: {}", employeeId, server, e.getMessage());
    }
  }

  // ===============================================================
  // SESSION DURATION HANDLING
  // ===============================================================
  @Override
  public void processSessions(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("=== Starting session duration processing ===");

    for (String server : servers) {
      try {
        List<LoginLogoutTimes> logs = loginLogoutTimesRepository.findAll()
            .stream()
            .filter(l -> l.getServerName().equals(server))
            .sorted(Comparator.comparing(LoginLogoutTimes::getLoginTime))
            .toList();

        List<SessionDuration> newSessions = new ArrayList<>();

        for (LoginLogoutTimes log : logs) {
          long duration = Duration.between(log.getLoginTime(), log.getLogoutTime()).getSeconds();
          if (duration <= 0)
            continue;

          SessionDuration sd = new SessionDuration();
          sd.setEmployeeId(log.getEmployeeId());
          sd.setServer(server);
          sd.setDate(log.getLoginTime().toLocalDate());
          sd.setSingleSessionDurationInSec((int) duration);
          newSessions.add(sd);
        }

        if (!newSessions.isEmpty()) {
          processedPlaytimeSessionsRepository.saveAll(newSessions);
          log.info("Saved {} new session durations for {}", newSessions.size(), server);
        }

      } catch (Exception e) {
        log.error("❌ Error processing sessions for {}: {}", server, e.getMessage(), e);
      }
    }

    long end = System.currentTimeMillis();
    log.info("✅ Session duration processing finished in {} ms", end - start);
  }

  // ===============================================================
  // DUPLICATE REMOVAL
  // ===============================================================
  @Override
  public void removeLoginLogoutsDupe() {
    log.info("🧹 Removing duplicate login/logout entries...");
    removeDuplicates(loginLogoutTimesRepository.findAll(), loginLogoutTimesRepository, "login/logout");
  }

  @Override
  public void removeDuplicateSessionData() {
    log.info("🧹 Removing duplicate session durations...");
    removeDuplicates(processedPlaytimeSessionsRepository.findAll(), processedPlaytimeSessionsRepository, "session");
  }

  private <T> void removeDuplicates(List<T> all, org.springframework.data.jpa.repository.JpaRepository<T, ?> repo,
      String type) {
    long start = System.currentTimeMillis();

    Map<Integer, List<T>> grouped = all.stream()
        .collect(Collectors.groupingBy(Object::hashCode)); // simple hash-based grouping
    List<T> toDelete = grouped.values().stream()
        .filter(list -> list.size() > 1)
        .flatMap(list -> list.subList(1, list.size()).stream())
        .toList();

    if (!toDelete.isEmpty()) {
      repo.deleteAllInBatch(toDelete);
      log.info("Removed {} duplicate {} entries", toDelete.size(), type);
    } else {
      log.info("No duplicate {} entries found", type);
    }

    log.debug("Duplicate cleanup took {} ms", System.currentTimeMillis() - start);
  }
}
