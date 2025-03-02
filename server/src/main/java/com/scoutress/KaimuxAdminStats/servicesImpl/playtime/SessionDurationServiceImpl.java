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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(SessionDurationServiceImpl.class);

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
    List<String> employeesUsernames = getAllEmployeesUsernames();
    logger.info("Starting to process sessions for servers: {}", servers);

    for (String server : servers) {
      logger.info("Processing server: {}", server);
      List<RawPlaytimeSession> allRawPlaytimes = getAllPlaytimeSessions(server);

      for (String username : employeesUsernames) {
        int userId = getUserIdByUsername(server, username);
        if (userId == -1) {
          continue;
        }

        List<RawPlaytimeSession> rawPlaytimes = getRawPlaytimesForUser(allRawPlaytimes, userId);
        if (rawPlaytimes.isEmpty()) {
          continue;
        }

        cleanAndSaveLoginLogout(rawPlaytimes, server);
      }
    }
    logger.info("Finished processing login logouts.");
  }

  private List<String> getAllEmployeesUsernames() {
    return employeeRepository
        .findAll()
        .stream()
        .map(Employee::getUsername)
        .collect(Collectors.toList());
  }

  private Integer getUserIdByUsername(String server, String username) {
    String tableName = "raw_user_data_" + server.toLowerCase();
    String query = "SELECT user_id FROM " + tableName + " WHERE username = ?";

    try {
      return jdbcTemplate.queryForObject(query, Integer.class, username);
    } catch (EmptyResultDataAccessException e) {
      return -1;
    }
  }

  private List<RawPlaytimeSession> getAllPlaytimeSessions(String server) {
    logger.debug("Fetching all playtime sessions for server: {}", server);
    String tableName = "raw_playtime_sessions_data_" + server.toLowerCase();
    String query = "SELECT id, user_id, time, action FROM " + tableName + " LIMIT ? OFFSET ?";

    int pageSize = 1000;
    int offset = 0;
    List<RawPlaytimeSession> allSessions = new ArrayList<>();

    while (true) {
      List<RawPlaytimeSession> sessionBatch = jdbcTemplate.query(query,
          (rs, rowNum) -> new RawPlaytimeSession(
              rs.getLong("id"),
              rs.getInt("user_id"),
              rs.getInt("time"),
              rs.getInt("action")),
          pageSize, offset);

      if (sessionBatch.isEmpty()) {
        break;
      }

      allSessions.addAll(sessionBatch);
      offset += pageSize;
    }
    logger.debug("Total sessions fetched: {}", allSessions.size());
    return allSessions;
  }

  private List<RawPlaytimeSession> getRawPlaytimesForUser(List<RawPlaytimeSession> allRawPlaytimes, int userId) {
    return allRawPlaytimes
        .stream()
        .filter(session -> session.getUserId() == userId)
        .collect(Collectors.toList());
  }

  private void cleanAndSaveLoginLogout(List<RawPlaytimeSession> rawPlaytimes, String server) {
    rawPlaytimes.sort(Comparator.comparing(RawPlaytimeSession::getTime));

    int i = 0;
    while (i < rawPlaytimes.size()) {
      RawPlaytimeSession firstLogin = rawPlaytimes.get(i);

      if (firstLogin.getAction() != 1) {
        i++;
        continue;
      }

      int secondLoginIndex = findNextLogin(rawPlaytimes, i + 1);

      if (secondLoginIndex == -1) {
        removeInvalidLogin(firstLogin);
        break;
      }

      List<RawPlaytimeSession> logouts = findLogoutsBetween(rawPlaytimes, i + 1, secondLoginIndex);

      if (!logouts.isEmpty()) {
        RawPlaytimeSession logoutSession = logouts.get(logouts.size() - 1);
        saveLoginLogoutPair(firstLogin, logoutSession, server);
        i = secondLoginIndex + 1;
      } else {
        removeInvalidLogin(firstLogin);
        i = secondLoginIndex + 1;
      }
    }
  }

  private void saveLoginLogoutPair(RawPlaytimeSession loginSession, RawPlaytimeSession logoutSession, String server) {
    logger.debug("Saving login/logout time for user: {}", loginSession.getUserId());
    LocalDateTime loginTime = Instant.ofEpochSecond(
        loginSession.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime logoutTime = logoutSession != null
        ? Instant.ofEpochSecond(
            logoutSession.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()
        : null;

    saveLoginLogoutToDb(loginSession.getUserId(), server, loginTime, logoutTime);
  }

  private void saveLoginLogoutToDb(int userId, String server, LocalDateTime loginTime, LocalDateTime logoutTime) {
    logger.debug("Saving to DB: User {}, Server {}, Login {}, Logout {}", userId, server, loginTime, logoutTime);
    LoginLogoutTimes loginLogoutTimes = new LoginLogoutTimes();
    loginLogoutTimes.setEmployeeId((short) userId);
    loginLogoutTimes.setServerName(server);
    loginLogoutTimes.setLoginTime(loginTime);
    loginLogoutTimes.setLogoutTime(logoutTime);
    loginLogoutTimesRepository.save(loginLogoutTimes);
  }

  private void removeInvalidLogin(RawPlaytimeSession loginSession) {
    logger.debug("Removing invalid login for user: {}", loginSession.getUserId());
  }

  private int findNextLogin(List<RawPlaytimeSession> rawPlaytimes, int startIndex) {
    for (int i = startIndex; i < rawPlaytimes.size(); i++) {
      if (rawPlaytimes.get(i).getAction() == 1) {
        return i;
      }
    }
    return -1;
  }

  private List<RawPlaytimeSession> findLogoutsBetween(List<RawPlaytimeSession> rawPlaytimes, int startIndex,
      int endIndex) {
    List<RawPlaytimeSession> logouts = new ArrayList<>();

    for (int i = startIndex; i < endIndex; i++) {
      if (rawPlaytimes.get(i).getAction() == 0) {
        logouts.add(rawPlaytimes.get(i));
      }
    }

    return logouts;
  }

  @Override
  public void removeLoginLogoutsDupe() {
    List<LoginLogoutTimes> allLoginsLogouts = getAllLoginsLogouts();
    logger.info("Fetched {} session records for duplicate removal.", allLoginsLogouts.size());

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
    int totalDuplicatesRemoved = 0;

    for (List<LoginLogoutTimes> group : groupedLoginsLogouts.values()) {
      if (group.size() > 1) {
        totalDuplicatesRemoved += group.size() - 1;

        logger.info("Removing {} duplicates from group: {}", group.size() - 1, group.get(0));

        group.subList(1, group.size()).forEach(session -> {
          logger.debug("Removing session: {}", session);
          loginLogoutTimesRepository.delete(session);
        });
      }
    }

    logger.info("Total duplicates removed: {}", totalDuplicatesRemoved);
  }

  @Override
  public void processSessions(List<String> servers) {
    for (String server : servers) {
      logger.debug("Counting and saving session durations for server: {}", server);
      LocalDate latestDate = sessionDurationRepository.findLatestDate();
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
    logger.debug("Saving session duration for user: {}, Duration: {}s, Date: {}", userId, sessionDurationInSeconds,
        date);
    String query = "INSERT INTO session_duration (employee_id, single_session_duration, date, server) VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(query, userId, sessionDurationInSeconds, date, server);
  }

  @Override
  public void removeDuplicateSessionData() {
    List<SessionDuration> allSessions = getSessionDurations();
    logger.info("Fetched {} session records for duplicate removal.", allSessions.size());

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
    int totalDuplicatesRemoved = 0;

    for (List<SessionDuration> group : groupedSessions.values()) {
      if (group.size() > 1) {
        totalDuplicatesRemoved += group.size() - 1;

        logger.info("Removing {} duplicates from group: {}", group.size() - 1, group.get(0));

        group.subList(1, group.size()).forEach(session -> {
          logger.debug("Removing session: {}", session);
          sessionDurationRepository.delete(session);
        });
      }
    }

    logger.info("Total duplicates removed: {}", totalDuplicatesRemoved);
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
