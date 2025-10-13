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

  @Override
  public void initializeUsersDatabase(List<String> servers) {
    log.info("Initializing users database for servers: {}", servers);
    dropAndCreateUsersTable(servers);
    transferUsersData(servers);
  }

  private void dropAndCreateUsersTable(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;
      String tableName = "raw_user_data_" + server.toLowerCase();
      try {
        mysqlJdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
        mysqlJdbcTemplate.execute("""
            CREATE TABLE %s (
              user_id INT PRIMARY KEY,
              username VARCHAR(255) NOT NULL
            )
            """.formatted(tableName));
        log.info("✅ Table {} created successfully.", tableName);
      } catch (DataAccessException e) {
        log.error("❌ Failed to create table for server {}: {}", server, e.getMessage());
      }
    }
  }

  private void transferUsersData(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;

      File sqliteFile = new File(SQLITE_DB_PATH + server + ".db");
      if (!sqliteFile.exists()) {
        log.warn("⚠️ SQLite DB not found for server: {}", server);
        continue;
      }

      String sqliteUrl = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
      log.info("Transferring users data from {}", sqliteFile.getName());

      try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl);
          Statement stmt = sqliteConnection.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT id, user FROM co_user")) {

        List<Object[]> batchArgs = new ArrayList<>();
        while (rs.next()) {
          int userId = rs.getInt("id");
          String username = rs.getString("user");
          batchArgs.add(new Object[] { userId, username, username });
        }

        if (!batchArgs.isEmpty()) {
          String insertQuery = "INSERT INTO raw_user_data_" + server.toLowerCase()
              + " (user_id, username) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = ?";
          mysqlJdbcTemplate.batchUpdate(insertQuery, batchArgs);
          log.info("✅ Inserted {} users for server {}", batchArgs.size(), server);
        } else {
          log.info("ℹ️ No users found for server {}", server);
        }

      } catch (SQLException e) {
        log.error("❌ Error transferring users for server {}: {}", server, e.getMessage());
      }
    }
  }

  @Override
  public void initializePlaytimeSessionsDatabase(List<String> servers) {
    log.info("Initializing playtime sessions database...");
    dropAndCreatePlaytimeSessionsTable(servers);
    transferPlaytimeSessionsData(servers);
  }

  private void dropAndCreatePlaytimeSessionsTable(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;
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
        log.info("✅ Table {} created successfully.", tableName);
      } catch (DataAccessException e) {
        log.error("❌ Failed to create playtime table for {}: {}", server, e.getMessage());
      }
    }
  }

  private void transferPlaytimeSessionsData(List<String> servers) {
    for (String server : servers) {
      if (!isValidServerName(server))
        continue;

      File sqliteFile = new File(SQLITE_DB_PATH + server + ".db");
      if (!sqliteFile.exists()) {
        log.warn("⚠️ SQLite DB not found for server: {}", server);
        continue;
      }

      String sqliteUrl = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
      log.info("Transferring playtime sessions from {}", sqliteFile.getName());

      try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl);
          Statement stmt = sqliteConnection.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT user, time, action FROM co_session")) {

        List<Object[]> batchArgs = new ArrayList<>();
        while (rs.next()) {
          batchArgs.add(new Object[] {
              rs.getInt("user"),
              rs.getInt("time"),
              rs.getInt("action")
          });
        }

        if (!batchArgs.isEmpty()) {
          String insertQuery = "INSERT INTO raw_playtime_sessions_data_" + server.toLowerCase()
              + " (user_id, time, action) VALUES (?, ?, ?)";
          mysqlJdbcTemplate.batchUpdate(insertQuery, batchArgs);
          log.info("✅ Inserted {} playtime sessions for {}", batchArgs.size(), server);
        } else {
          log.info("ℹ️ No playtime sessions found for {}", server);
        }

      } catch (SQLException e) {
        log.error("❌ Error transferring playtime sessions for {}: {}", server, e.getMessage());
      }
    }
  }

  private boolean isValidServerName(String name) {
    return name != null && name.matches("^[a-zA-Z0-9_]+$");
  }
}
