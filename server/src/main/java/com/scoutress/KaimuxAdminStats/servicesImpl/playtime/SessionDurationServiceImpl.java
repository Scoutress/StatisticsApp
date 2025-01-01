package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;

@Service
public class SessionDurationServiceImpl implements SessionDurationService {

  private final JdbcTemplate jdbcTemplate;

  public SessionDurationServiceImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
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

  private List<String> getServers() {
    return Arrays.asList("Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");
  }

  private List<Map<String, Object>> getEmployeesForServer(String server) {
    String query = "SELECT employee_id, " + server.toLowerCase() + "_id AS server_user_id FROM employee_codes WHERE "
        + server.toLowerCase() + "_id IS NOT NULL";
    return jdbcTemplate.queryForList(query);
  }

  private List<Map<String, Object>> getSessionData(String server, Integer userId) {
    String query = "SELECT action, time FROM raw_playtime_sessions_data_" + server.toLowerCase()
        + " WHERE user_id = ? ORDER BY time";
    return jdbcTemplate.queryForList(query, userId);
  }

  private void processEmployeeSessions(Integer employeeId, String server, List<Map<String, Object>> sessionData) {
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

  private void calculateAndSaveSessionDurations(Integer employeeId, String server, List<Integer> logins,
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

  private Integer findValidLogoutTime(List<Integer> logouts, Integer loginTime, Integer nextLoginTime) {
    for (Iterator<Integer> iterator = logouts.iterator(); iterator.hasNext();) {
      Integer logoutTime = iterator.next();
      if (logoutTime >= loginTime && logoutTime < nextLoginTime) {
        iterator.remove();
        return logoutTime;
      }
    }
    return null;
  }

  private void saveSessionDuration(Integer employeeId, String server, Integer sessionDuration, Integer loginTime) {
    LocalDate sessionDate = Instant.ofEpochSecond(loginTime).atZone(ZoneId.systemDefault()).toLocalDate();

    String query = "INSERT INTO session_duration (aid, single_session_duration, date, server) VALUES (?, ?, ?, ?)";

    int rowsInserted = jdbcTemplate.update(query, employeeId, sessionDuration, sessionDate, server);
    System.out.println("Rows inserted: " + rowsInserted);
  }
}
