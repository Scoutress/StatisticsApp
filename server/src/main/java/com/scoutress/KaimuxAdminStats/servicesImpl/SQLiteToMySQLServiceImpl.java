package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.SQLiteToMySQLService;

@Service
public class SQLiteToMySQLServiceImpl implements SQLiteToMySQLService {

  private static final String SQLITE_DB_PATH = "server/src/main/java/com/scoutress/KaimuxAdminStats/utils/rawCoreProtectData/";

  private static final List<String> servers = List.of(
      "Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");

  private final JdbcTemplate mysqlJdbcTemplate;

  public SQLiteToMySQLServiceImpl(JdbcTemplate mysqlJdbcTemplate) {
    this.mysqlJdbcTemplate = mysqlJdbcTemplate;
  }

  @Override
  public void initializeUsersDatabase() {
    dropAndCreateTable(servers);
    transferData(servers);
  }

  private void dropAndCreateTable(List<String> servers) {
    for (String server : servers) {
      String dropQuery = "DROP TABLE IF EXISTS raw_user_data_" + server.toLowerCase();
      String createQuery = "CREATE TABLE raw_user_data_" + server.toLowerCase() + " (" +
          "user_id INT PRIMARY KEY, " +
          "username VARCHAR(255) NOT NULL" +
          ")";
      try {
        mysqlJdbcTemplate.execute(dropQuery);
        mysqlJdbcTemplate.execute(createQuery);
      } catch (DataAccessException e) {
      }
    }
  }

  public void transferData(List<String> servers) {
    for (String server : servers) {
      String sqliteUrl = "jdbc:sqlite:" + SQLITE_DB_PATH + server + ".db";

      try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl)) {
        String query = "SELECT id, user FROM co_user";

        try (Statement stmt = sqliteConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

          while (rs.next()) {
            int userId = rs.getInt("id");
            String username = rs.getString("user");

            saveToMySQL(userId, username, server);
          }
        }

      } catch (Exception e) {
      }
    }
  }

  private void saveToMySQL(int userId, String username, String server) {
    String insertQuery = "INSERT INTO raw_user_data_" + server.toLowerCase() + " (user_id, username) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE username = ?";
    mysqlJdbcTemplate.update(insertQuery, userId, username, username);
  }
}
