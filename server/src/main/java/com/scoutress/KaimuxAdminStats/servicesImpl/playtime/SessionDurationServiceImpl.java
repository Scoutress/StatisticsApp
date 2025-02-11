package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SessionDurationRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;

@Service
public class SessionDurationServiceImpl implements SessionDurationService {

  private final JdbcTemplate jdbcTemplate;
  private final DataExtractingService dataExtractingService;
  private final SessionDurationRepository sessionDurationRepository;
  private final EmployeeRepository employeeRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;

  public SessionDurationServiceImpl(
      JdbcTemplate jdbcTemplate,
      DataExtractingService dataExtractingService,
      SessionDurationRepository sessionDurationRepository,
      EmployeeRepository employeeRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.dataExtractingService = dataExtractingService;
    this.sessionDurationRepository = sessionDurationRepository;
    this.employeeRepository = employeeRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
  }

  @Override
  public void processSessions() {
    List<String> servers = getServers();

    for (String server : servers) {
      List<Map<String, Object>> employees = getEmployeesForServer(server);

      for (Map<String, Object> employee : employees) {
        Integer employeeId = (Integer) employee.get("employee_id");
        Integer serverUserId = (Integer) employee.get("server_user_id");

        List<Map<String, Object>> sessionData = getSessionData(server, serverUserId);
        processEmployeeSessions(employeeId, server, sessionData);
      }
    }
  }

  public List<String> getServers() {
    return Arrays.asList("Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");
  }

  public List<Map<String, Object>> getEmployeesForServer(String server) {
    String query = "SELECT employee_id, " + server.toLowerCase() + "_id AS server_user_id FROM employee_codes WHERE "
        + server.toLowerCase() + "_id IS NOT NULL";
    return jdbcTemplate.queryForList(query);
  }

  public List<Map<String, Object>> getSessionData(String server, Integer userId) {
    String query = "SELECT action, time FROM raw_playtime_sessions_data_" + server.toLowerCase()
        + " WHERE user_id = ? ORDER BY time";
    return jdbcTemplate.queryForList(query, userId);
  }

  public void processEmployeeSessions(Integer employeeId, String server, List<Map<String, Object>> sessionData) {
    List<Integer> logins = new ArrayList<>();
    List<Integer> logouts = new ArrayList<>();

    for (Map<String, Object> record : sessionData) {
      Boolean action = (Boolean) record.get("action");
      Integer playtime = (Integer) record.get("time");

      if (action) {
        logins.add(playtime);
      } else {
        logouts.add(playtime);
      }
    }

    calculateAndSaveSessionDurations(employeeId, server, logins, logouts);
  }

  public void calculateAndSaveSessionDurations(Integer employeeId, String server, List<Integer> logins,
      List<Integer> logouts) {
    for (int i = 0; i < logins.size(); i++) {
      if (i + 1 >= logins.size() || logouts.isEmpty()) {
        break;
      }

      Integer loginTime = logins.get(i);
      Integer nextLoginTime = logins.get(i + 1);
      Integer logoutTime = findValidLogoutTime(logouts, loginTime, nextLoginTime);

      if (logoutTime != null) {
        int sessionDuration = logoutTime - loginTime;

        if (sessionDuration <= 24 * 60 * 60) {
          saveSessionDuration(employeeId, server, sessionDuration, loginTime);
        }
      }
    }
  }

  public Integer findValidLogoutTime(List<Integer> logouts, Integer loginTime, Integer nextLoginTime) {
    for (Iterator<Integer> iterator = logouts.iterator(); iterator.hasNext();) {
      Integer logoutTime = iterator.next();
      if (logoutTime >= loginTime && logoutTime < nextLoginTime) {
        iterator.remove();
        return logoutTime;
      }
    }
    return null;
  }

  public void saveSessionDuration(Integer employeeId, String server, Integer sessionDuration, Integer loginTime) {
    LocalDate sessionDate = Instant.ofEpochSecond(loginTime).atZone(ZoneId.systemDefault()).toLocalDate();

    String query = "INSERT INTO session_duration (aid, single_session_duration, date, server) VALUES (?, ?, ?, ?)";

    jdbcTemplate.update(query, employeeId, sessionDuration, sessionDate, server);
  }

  @Override
  public void removeDuplicateSessionData() {
    List<SessionDuration> allSessions = dataExtractingService.getSessionDurations();

    Map<String, List<SessionDuration>> groupedByUniqueFields = allSessions
        .stream()
        .collect(Collectors.groupingBy(session -> session.getEmployeeId() + "-" +
            session.getDate() + "-" +
            session.getServer() + "-" +
            session.getSingleSessionDurationInSec()));

    groupedByUniqueFields.values().forEach(group -> {
      if (group.size() > 1) {
        group.subList(1, group.size()).forEach(sessionDuration -> sessionDurationRepository.delete(sessionDuration));
      }
    });
  }

  public void delete(SessionDuration sessionDuration) {
    if (sessionDuration == null) {
      throw new IllegalArgumentException("SessionDuration must not be null.");
    }
    sessionDurationRepository.delete(sessionDuration);
  }

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
