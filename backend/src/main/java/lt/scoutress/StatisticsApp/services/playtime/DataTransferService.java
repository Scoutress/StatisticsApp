package lt.scoutress.StatisticsApp.services.playtime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataTransferService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQLITE_URL_SURVIVAL = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Survival.db";
    private static final String SQLITE_URL_SKYBLOCK = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Skyblock.db";
    private static final String SQLITE_URL_CREATIVE = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Creative.db";
    private static final String SQLITE_URL_BOXPVP = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Boxpvp.db";
    private static final String SQLITE_URL_PRISON = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Prison.db";
    private static final String SQLITE_URL_EVENTS = "jdbc:sqlite:C:\\Users\\Asus\\Documents\\Kaimux Statistics Database\\Events.db";
    private static final String PLAYTIME_DATA_ALL_SURVIVAL = "playtime_data_all_survival";
    private static final String PLAYTIME_DATA_ALL_SKYBLOCK = "playtime_data_all_skyblock";
    private static final String PLAYTIME_DATA_ALL_CREATIVE = "playtime_data_all_creative";
    private static final String PLAYTIME_DATA_ALL_BOXPVP = "playtime_data_all_boxpvp";
    private static final String PLAYTIME_DATA_ALL_PRISON = "playtime_data_all_prison";
    private static final String PLAYTIME_DATA_ALL_EVENTS = "playtime_data_all_events";

    public void transferDataFromSQLiteToMySQL() {
        List<Map<String, Object>> userRecords = jdbcTemplate.queryForList("SELECT * FROM playtime_db_codes");
        for (Map<String, Object> userRecord : userRecords) {
            String username = ((String) userRecord.get("username")).toLowerCase();
            if (username != null) {    
                if (userRecord.containsValue(null)) {
                    System.out.println("In " + username + " row there is no number for at least one server.");
                    printMissingValues(username, userRecord);
                } else {
                    int survivalValue = getIntValue(userRecord, "survival");
                    int skyblockValue = getIntValue(userRecord, "skyblock");
                    int creativeValue = getIntValue(userRecord, "creative");
                    int boxpvpValue = getIntValue(userRecord, "boxpvp");
                    int prisonValue = getIntValue(userRecord, "prison");
                    int eventsValue = getIntValue(userRecord, "events");

                    transferDataFromSQLite(username, survivalValue, SQLITE_URL_SURVIVAL, PLAYTIME_DATA_ALL_SURVIVAL);
                    transferDataFromSQLite(username, skyblockValue, SQLITE_URL_SKYBLOCK, PLAYTIME_DATA_ALL_SKYBLOCK);
                    transferDataFromSQLite(username, creativeValue, SQLITE_URL_CREATIVE, PLAYTIME_DATA_ALL_CREATIVE);
                    transferDataFromSQLite(username, boxpvpValue, SQLITE_URL_BOXPVP, PLAYTIME_DATA_ALL_BOXPVP);
                    transferDataFromSQLite(username, prisonValue, SQLITE_URL_PRISON, PLAYTIME_DATA_ALL_PRISON);
                    transferDataFromSQLite(username, eventsValue, SQLITE_URL_EVENTS, PLAYTIME_DATA_ALL_EVENTS);
                }
            }
        }
    }

    private int getIntValue(Map<String, Object> userRecord, String columnName) {
        String value = (String) userRecord.get(columnName);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println(columnName + " value is not a valid integer for user: " + userRecord.get("username"));
            return 0;
        }
    }

    private int getLastId(String tableName) {
        String selectLastIdQuery = "SELECT MAX(id) FROM " + tableName;
        try {
            Integer lastId = jdbcTemplate.queryForObject(selectLastIdQuery, Integer.class);
            return lastId != null ? lastId : 0;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Table " + tableName + " is empty or no records match the criteria.");
            return 0;
        }
    }
    
    private void transferDataFromSQLite(String username, int userValue, String sqliteUrl, String tableName) {
        try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl)) {
            String queryOn = "SELECT time FROM co_session WHERE action = 1 AND user = " + userValue;
            String queryOff = "SELECT time FROM co_session WHERE action = 0 AND user = " + userValue;
    
            int rowsInsertedOn = 0;
            int rowsInsertedOff = 0;
    
            try (Statement statement = sqliteConnection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
                resultSet.next();
                int count = resultSet.getInt(1);
    
                int lastId = 0;
                if (count > 0) {
                    resultSet = statement.executeQuery("SELECT MAX(id) FROM " + tableName);
                    resultSet.next();
                    lastId = resultSet.getInt(1);
                }
    
                try (ResultSet resultSetOn = statement.executeQuery(queryOn)) {
                    while (resultSetOn.next()) {
                        int time = resultSetOn.getInt("time");
                        String insertQueryOn = "INSERT INTO " + tableName + " (id, " + username + "_on) VALUES (?, ?)";
                        jdbcTemplate.update(insertQueryOn, lastId + 1, time); // Insert with the new ID
                        rowsInsertedOn++;
                        lastId++;
                    }
                }
    
                try (ResultSet resultSetOff = statement.executeQuery(queryOff)) {
                    while (resultSetOff.next()) {
                        int time = resultSetOff.getInt("time");
                        String insertQueryOff = "INSERT INTO " + tableName + " (id, " + username + "_off) VALUES (?, ?)";
                        jdbcTemplate.update(insertQueryOff, lastId + 1, time); // Insert with the new ID
                        rowsInsertedOff++;
                        lastId++;
                    }
                }
            }
    
            System.out.println("");
            System.out.println("Data transfer from SQLite to MySQL completed for user: " + username);
            System.out.println("Rows inserted into " + tableName + " - ON: " + rowsInsertedOn + ", OFF: " + rowsInsertedOff);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getLastTime(String columnName, String tableName) {
    String selectLastQuery = "SELECT " + columnName + " FROM " + tableName + " ORDER BY " + columnName + " DESC LIMIT 1";
        try {
            Integer lastValue = jdbcTemplate.queryForObject(selectLastQuery, Integer.class);
            return lastValue != null ? lastValue : 0;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Table " + tableName + " is empty or no records match the criteria.");
            return 0;
        }
    }
    
    private void printMissingValues(String username, Map<String, Object> userRecord) {
        String[] serverNames = {"survival", "skyblock", "creative", "boxpvp", "prison", "events"};
        for (String serverName : serverNames) {
            if (userRecord.get(serverName) == null) {
                System.out.println(serverName.substring(0, 1).toUpperCase() + serverName.substring(1) + " value is missing for user: " + username);
            }
        }
    }
}
