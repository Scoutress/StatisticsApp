package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.entity.playtime.RawPlaytimeSession;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SessionDurationRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;

import jakarta.transaction.Transactional;

@Service
public class SessionDurationServiceImpl implements SessionDurationService {

  private final JdbcTemplate jdbcTemplate;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  private final SessionDurationRepository sessionDurationRepository;
  private final EmployeeRepository employeeRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;

  public SessionDurationServiceImpl(
      JdbcTemplate jdbcTemplate,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      SessionDurationRepository sessionDurationRepository,
      EmployeeRepository employeeRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.sessionDurationRepository = sessionDurationRepository;
    this.employeeRepository = employeeRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
  }

  @Override
  @Transactional
  public void processLoginLogouts(List<String> servers) {
    Map<String, Short> usernameToEmployeeIdMap = getUsernameToEmployeeIdMap();

    for (String server : servers) {
      List<RawPlaytimeSession> allPlaytimeSessions = getAllPlaytimeSessionsForServer(server);

      for (String username : usernameToEmployeeIdMap.keySet()) {
        int rawUserId = getUserIdByUsername(server, username);
        if (rawUserId == -1) {
          continue;
        }

        List<RawPlaytimeSession> userSessions = filterSessionsByUser(allPlaytimeSessions, rawUserId);
        if (!userSessions.isEmpty()) {
          Short employeeId = usernameToEmployeeIdMap.get(username);
          processAndSaveUserSessions(userSessions, server, employeeId);
        }
      }
    }
  }

  private Map<String, Short> getUsernameToEmployeeIdMap() {
    return employeeRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(Employee::getUsername, Employee::getId));
  }

  private int getUserIdByUsername(String server, String username) {
    String tableName = "raw_user_data_" + server.toLowerCase();
    String query = "SELECT user_id FROM " + tableName + " WHERE username = ?";

    try {
      Integer userId = jdbcTemplate.queryForObject(query, Integer.class, username);
      return userId != null ? userId : -1;
    } catch (EmptyResultDataAccessException e) {
      return -1;
    }
  }

  private List<RawPlaytimeSession> getAllPlaytimeSessionsForServer(String server) {
    String tableName = "raw_playtime_sessions_data_" + server.toLowerCase();
    String query = "SELECT id, user_id, time, action FROM " + tableName + " LIMIT ? OFFSET ?";

    int pageSize = 1000;
    int offset = 0;
    List<RawPlaytimeSession> allSessions = new ArrayList<>();

    while (true) {
      List<RawPlaytimeSession> batch = jdbcTemplate.query(query,
          (rs, rowNum) -> new RawPlaytimeSession(
              rs.getLong("id"),
              rs.getInt("user_id"),
              rs.getInt("time"),
              rs.getInt("action")),
          pageSize, offset);

      if (batch.isEmpty())
        break;

      allSessions.addAll(batch);
      offset += pageSize;
    }

    return allSessions;
  }

  private List<RawPlaytimeSession> filterSessionsByUser(List<RawPlaytimeSession> allSessions, int userId) {
    return allSessions.stream()
        .filter(session -> session.getUserId() == userId)
        .collect(Collectors.toList());
  }

  private void processAndSaveUserSessions(List<RawPlaytimeSession> sessions, String server, Short employeeId) {
    sessions.sort(Comparator.comparing(RawPlaytimeSession::getTime));

    int i = 0;
    while (i < sessions.size()) {
      RawPlaytimeSession loginSession = sessions.get(i);

      if (loginSession.getAction() != 1) {
        i++;
        continue;
      }

      int nextLoginIndex = findNextLogin(sessions, i + 1);

      if (nextLoginIndex == -1) {
        removeInvalidLogin(loginSession);
        break;
      }

      List<RawPlaytimeSession> logouts = findLogoutsBetween(sessions, i + 1, nextLoginIndex);

      if (!logouts.isEmpty()) {
        RawPlaytimeSession logoutSession = logouts.get(logouts.size() - 1);
        saveLoginLogout(loginSession, logoutSession, server, employeeId);
        i = nextLoginIndex + 1;
      } else {
        removeInvalidLogin(loginSession);
        i = nextLoginIndex;
      }
    }
  }

  private void saveLoginLogout(RawPlaytimeSession login, RawPlaytimeSession logout, String server, Short employeeId) {
    LocalDateTime loginTime = convertEpochToLocalDateTime(login.getTime());
    LocalDateTime logoutTime = convertEpochToLocalDateTime(logout.getTime());

    saveToDatabase(employeeId, server, loginTime, logoutTime);
  }

  private void saveToDatabase(Short employeeId, String server, LocalDateTime loginTime, LocalDateTime logoutTime) {
    LoginLogoutTimes record = new LoginLogoutTimes();
    record.setEmployeeId(employeeId);
    record.setServerName(server);
    record.setLoginTime(loginTime);
    record.setLogoutTime(logoutTime);

    loginLogoutTimesRepository.save(record);
  }

  private LocalDateTime convertEpochToLocalDateTime(long epochSeconds) {
    return Instant.ofEpochSecond(epochSeconds)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
  }

  private int findNextLogin(List<RawPlaytimeSession> sessions, int startIndex) {
    for (int i = startIndex; i < sessions.size(); i++) {
      if (sessions.get(i).getAction() == 1) {
        return i;
      }
    }
    return -1;
  }

  private List<RawPlaytimeSession> findLogoutsBetween(List<RawPlaytimeSession> sessions, int startIndex, int endIndex) {
    List<RawPlaytimeSession> logouts = new ArrayList<>();

    for (int i = startIndex; i < endIndex; i++) {
      if (sessions.get(i).getAction() == 0) {
        logouts.add(sessions.get(i));
      }
    }

    return logouts;
  }

  private void removeInvalidLogin(RawPlaytimeSession loginSession) {
    System.err.println("Invalid login session found and skipped: " + loginSession.getId());
  }

  @Override
  public void removeLoginLogoutsDupe() {
    List<LoginLogoutTimes> allLoginsLogouts = getAllLoginsLogouts();

    Map<String, List<LoginLogoutTimes>> groupedLoginsLogouts = groupLoginsLogoutsByUniqueFields(allLoginsLogouts);

    removeDuplicateLoginsLogouts(groupedLoginsLogouts);
  }

  private List<LoginLogoutTimes> getAllLoginsLogouts() {
    return loginLogoutTimesRepository.findAll();
  }

  private Map<String, List<LoginLogoutTimes>> groupLoginsLogoutsByUniqueFields(
      List<LoginLogoutTimes> allLoginsLogouts) {
    return allLoginsLogouts
        .stream()
        .collect(Collectors.groupingBy(logs -> logs.getEmployeeId() + "-" +
            logs.getServerName() + "-" +
            logs.getLoginTime() + "-" +
            logs.getLogoutTime()));
  }

  private void removeDuplicateLoginsLogouts(Map<String, List<LoginLogoutTimes>> groupedLoginsLogouts) {
    for (List<LoginLogoutTimes> group : groupedLoginsLogouts.values()) {
      if (group.size() > 1) {
        group.subList(1, group.size()).forEach(session -> {
          loginLogoutTimesRepository.delete(session);
        });
      }
    }
  }

  @Override
  public void processSessions(List<String> servers) {
    for (String server : servers) {
      LocalDate latestDate = sessionDurationRepository.findLatestDate();

      if (latestDate == null) {
        latestDate = LocalDate.of(2016, 1, 1);
      }

      LocalDateTime startOfDay = latestDate.atStartOfDay();

      List<LoginLogoutTimes> cleanedRawPlaytimes = loginLogoutTimesRepository
          .findByLoginTimeGreaterThanEqual(startOfDay);

      for (int i = 0; i < cleanedRawPlaytimes.size(); i++) {
        LoginLogoutTimes loginSession = cleanedRawPlaytimes.get(i);
        if (i + 1 < cleanedRawPlaytimes.size()) {
          LoginLogoutTimes logoutSession = cleanedRawPlaytimes.get(i + 1);

          if (logoutSession.getLoginTime().isAfter(loginSession.getLoginTime())) {
            long sessionDurationInSeconds = Duration.between(loginSession.getLoginTime(), logoutSession.getLogoutTime())
                .getSeconds();
            saveSessionDuration(loginSession.getEmployeeId(), sessionDurationInSeconds,
                loginSession.getLoginTime().toLocalDate(), server);
            i++;
          }
        }
      }
    }
  }

  private void saveSessionDuration(int userId, long sessionDurationInSeconds, LocalDate date, String server) {
    String query = "INSERT INTO session_duration (employee_id, single_session_duration, date, server) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(query, userId, sessionDurationInSeconds, date, server);
  }

  @Override
  public void removeDuplicateSessionData() {
    List<SessionDuration> allSessions = getSessionDurations();
    Map<String, List<SessionDuration>> groupedSessions = groupSessionsByUniqueFields(allSessions);

    removeDuplicateSessions(groupedSessions);
  }

  private List<SessionDuration> getSessionDurations() {
    return processedPlaytimeSessionsRepository.findAll();
  }

  private Map<String, List<SessionDuration>> groupSessionsByUniqueFields(List<SessionDuration> allSessions) {
    return allSessions
        .stream()
        .collect(Collectors.groupingBy(session -> session.getEmployeeId() + "-" +
            session.getDate() + "-" +
            session.getServer() + "-" +
            session.getSingleSessionDurationInSec()));
  }

  private void removeDuplicateSessions(Map<String, List<SessionDuration>> groupedSessions) {
    for (List<SessionDuration> group : groupedSessions.values()) {
      if (group.size() > 1) {
        group.subList(1, group.size()).forEach(session -> {
          sessionDurationRepository.delete(session);
        });
      }
    }
  }

  // Temp. methods below
  @Override
  public void processSessionsFromBackup() {
    List<Short> allEmployeeIds = getEmployeeIds();
    List<LoginLogoutTimes> allRawData = getRawDataFromLoginLogoutTimes();
    List<String> allServersFromRawData = getServersFromLoginLogoutTimes(allRawData);

    for (Short employeeId : allEmployeeIds) {
      List<LoginLogoutTimes> allDataForThisEmployee = getLoginLogoutDataForThisEmployee(allRawData, employeeId);

      for (String server : allServersFromRawData) {
        List<LoginLogoutTimes> allDataForThisEmployeeThisServer = getLoginLogoutDataForThisEmployeeThisServer(
            allDataForThisEmployee, server);

        int sessionsCountForThisEmployeeThisServer = getSessionCountForThisEmployeeThisServer(
            allDataForThisEmployeeThisServer);

        for (int i = 0; i < sessionsCountForThisEmployeeThisServer; i++) {
          int sessionDuration = getSessionDurationForThisSession(allDataForThisEmployeeThisServer, i);
          LocalDate sessionLoginDate = getLoginDateForThisSession(allDataForThisEmployeeThisServer, i);
          saveSessionDurationWithLoginDate(employeeId, server, sessionDuration, sessionLoginDate);
        }
      }
    }
  }

  public List<Short> getEmployeeIds() {
    return employeeRepository
        .findAll()
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  public List<LoginLogoutTimes> getRawDataFromLoginLogoutTimes() {
    return loginLogoutTimesRepository.findAll();
  }

  public List<String> getServersFromLoginLogoutTimes(List<LoginLogoutTimes> allRawData) {
    return allRawData
        .stream()
        .map(LoginLogoutTimes::getServerName)
        .distinct()
        .sorted()
        .toList();
  }

  public List<LoginLogoutTimes> getLoginLogoutDataForThisEmployee(List<LoginLogoutTimes> allRawData, Short employeeId) {
    return allRawData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .collect(Collectors.toList());
  }

  public List<LoginLogoutTimes> getLoginLogoutDataForThisEmployeeThisServer(
      List<LoginLogoutTimes> allDataForThisEmployee, String server) {
    return allDataForThisEmployee
        .stream()
        .filter(data -> data.getServerName().equals(server))
        .collect(Collectors.toList());
  }

  public int getSessionCountForThisEmployeeThisServer(List<LoginLogoutTimes> allDataForThisEmployeeThisServer) {
    return (int) allDataForThisEmployeeThisServer
        .stream()
        .count();
  }

  public int getSessionDurationForThisSession(List<LoginLogoutTimes> allDataForThisEmployeeThisServer, int i) {
    LoginLogoutTimes entry = allDataForThisEmployeeThisServer.get(i);

    LocalDateTime loginTime = entry.getLoginTime();
    LocalDateTime logoutTime = entry.getLogoutTime();

    Duration duration = Duration.between(loginTime, logoutTime);

    return (int) duration.toSeconds();
  }

  public LocalDate getLoginDateForThisSession(List<LoginLogoutTimes> allDataForThisEmployeeThisServer, int i) {
    LoginLogoutTimes entry = allDataForThisEmployeeThisServer.get(i);

    LocalDateTime loginDateTime = entry.getLoginTime();

    return loginDateTime.toLocalDate();
  }

  public void saveSessionDurationWithLoginDate(
      Short employeeId, String server, int sessionDuration, LocalDate sessionLoginDate) {

    Optional<SessionDuration> existingSession = sessionDurationRepository
        .findByEmployeeIdAndServerAndDateAndSingleSessionDurationInSec(
            employeeId, server, sessionLoginDate, sessionDuration);

    if (!existingSession.isPresent()) {
      SessionDuration newSessionDuration = new SessionDuration();
      newSessionDuration.setEmployeeId(employeeId);
      newSessionDuration.setServer(server);
      newSessionDuration.setDate(sessionLoginDate);
      newSessionDuration.setSingleSessionDurationInSec(sessionDuration);

      sessionDurationRepository.save(newSessionDuration);
    }
  }
}
