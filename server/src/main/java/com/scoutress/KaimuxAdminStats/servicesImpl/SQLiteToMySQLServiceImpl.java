package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.SQLiteToMySQLService;

@Service
public class SQLiteToMySQLServiceImpl implements SQLiteToMySQLService {

  private static final Logger log = LoggerFactory.getLogger(SQLiteToMySQLServiceImpl.class);
  private static final String SQLITE_DB_PATH = "C:\\Users\\Asus\\Desktop\\KAIMUX databases\\uploadToDatabase\\";

  private final JdbcTemplate mysqlJdbcTemplate;

  public SQLiteToMySQLServiceImpl(JdbcTemplate mysqlJdbcTemplate) {
    this.mysqlJdbcTemplate = mysqlJdbcTemplate;
  }

  // ===========================================================
  // USERS DATABASE INITIALIZATION
  // ===========================================================
  @Override
  public void initializeUsersDatabase(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("🗂️ [START] Initializing users database for servers: {}", servers);

    dropAndCreateUsersTable(servers);
    transferUsersData(servers);

    log.info("✅ [DONE] Users database initialized in {} ms", System.currentTimeMillis() - start);
  }

  private void dropAndCreateUsersTable(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server)) {
        log.warn("⚠️ Invalid server name '{}', skipping.", server);
        continue;
      }
      String tableName = "raw_user_data_" + server.toLowerCase();
      try {
        mysqlJdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
        mysqlJdbcTemplate.execute("""
            CREATE TABLE %s (
              user_id INT PRIMARY KEY,
              username VARCHAR(255) NOT NULL
            )
            """.formatted(tableName));
        log.info("✅ Table '{}' created successfully.", tableName);
      } catch (DataAccessException e) {
        log.error("❌ Failed to create users table for '{}': {}", server, e.getMessage(), e);
      }
    }
  }

  private void transferUsersData(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;

      File sqliteFile = new File(SQLITE_DB_PATH + server + ".db");
      if (!sqliteFile.exists()) {
        log.warn("⚠️ SQLite DB file not found for server '{}': {}", server, sqliteFile.getAbsolutePath());
        continue;
      }

      String sqliteUrl = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
      log.info("📤 Transferring user data from {}", sqliteFile.getName());

      try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl);
          Statement stmt = sqliteConnection.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT id, user FROM co_user")) {

        List<Object[]> batchArgs = new ArrayList<>();
        while (rs.next()) {
          int userId = rs.getInt("id");
          String username = rs.getString("user");
          batchArgs.add(new Object[] { userId, username, username });

          if (log.isTraceEnabled())
            log.trace("User row → id={}, username={}", userId, username);
        }

        if (!batchArgs.isEmpty()) {
          String insertQuery = "INSERT INTO raw_user_data_" + server.toLowerCase()
              + " (user_id, username) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = ?";
          log.debug("Executing batch insert into {} ({} records)", server, batchArgs.size());
          mysqlJdbcTemplate.batchUpdate(insertQuery, batchArgs);
          log.info("✅ Inserted/updated {} user entries for server '{}'.", batchArgs.size(), server);
        } else {
          log.info("ℹ️ No user records found in '{}'", sqliteFile.getName());
        }

      } catch (SQLException e) {
        log.error("❌ SQL error transferring users for '{}': {}", server, e.getMessage(), e);
      } catch (Exception e) {
        log.error("❌ Unexpected error while transferring users for '{}': {}", server, e.getMessage(), e);
      }
    }
  }

  // ===========================================================
  // PLAYTIME SESSIONS DATABASE INITIALIZATION
  // ===========================================================
  @Override
  public void initializePlaytimeSessionsDatabase(List<String> servers) {
    long start = System.currentTimeMillis();
    log.info("🕒 [START] Initializing playtime sessions database for servers: {}", servers);

    dropAndCreatePlaytimeSessionsTable(servers);
    transferPlaytimeSessionsData(servers);

    log.info("✅ [DONE] Playtime sessions initialized in {} ms", System.currentTimeMillis() - start);
  }

  private void dropAndCreatePlaytimeSessionsTable(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server)) {
        log.warn("⚠️ Invalid server name '{}', skipping.", server);
        continue;
      }
      String tableName = "raw_playtime_sessions_data_" + server.toLowerCase();
      try {
        mysqlJdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
        mysqlJdbcTemplate.execute("""
            CREATE TABLE %s (
              id INT AUTO_INCREMENT PRIMARY KEY,
              user_id INT NOT NULL,
              time INT NOT NULL,
              action TINYINT(1) NOT NULL
            )
            """.formatted(tableName));
        log.info("✅ Table '{}' created successfully.", tableName);
      } catch (DataAccessException e) {
        log.error("❌ Failed to create playtime table for '{}': {}", server, e.getMessage(), e);
      }
    }
  }

  private void transferPlaytimeSessionsData(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;

      File sqliteFile = new File(SQLITE_DB_PATH + server + ".db");
      if (!sqliteFile.exists()) {
        log.warn("⚠️ SQLite DB file not found for '{}': {}", server, sqliteFile.getAbsolutePath());
        continue;
      }

      String sqliteUrl = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
      log.info("📤 Transferring playtime sessions from {}", sqliteFile.getName());

      try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl);
          Statement stmt = sqliteConnection.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT user, time, action FROM co_session")) {

        List<Object[]> batchArgs = new ArrayList<>();
        while (rs.next()) {
          int userId = rs.getInt("user");
          int time = rs.getInt("time");
          int action = rs.getInt("action");

          batchArgs.add(new Object[] { userId, time, action });

          if (log.isTraceEnabled())
            log.trace("Session row → userId={}, time={}, action={}", userId, time, action);
        }

        if (!batchArgs.isEmpty()) {
          String insertQuery = "INSERT INTO raw_playtime_sessions_data_" + server.toLowerCase()
              + " (user_id, time, action) VALUES (?, ?, ?)";
          log.debug("Executing batch insert into {} ({} records)", server, batchArgs.size());
          mysqlJdbcTemplate.batchUpdate(insertQuery, batchArgs);
          log.info("✅ Inserted {} playtime session entries for '{}'.", batchArgs.size(), server);
        } else {
          log.info("ℹ️ No playtime sessions found in '{}'", sqliteFile.getName());
        }

      } catch (SQLException e) {
        log.error("❌ SQL error transferring playtime sessions for '{}': {}", server, e.getMessage(), e);
      } catch (Exception e) {
        log.error("❌ Unexpected error transferring playtime sessions for '{}': {}", server, e.getMessage(), e);
      }
    }
  }

  // ===========================================================
  // VALIDATION
  // ===========================================================
  private boolean isValidServerName(String name) {
    boolean valid = name != null && name.matches("^[a-zA-Z0-9_]+$");
    if (!valid)
      log.warn("⚠️ Invalid server name '{}'. Must be alphanumeric.", name);
    return valid;
  }
}
