package com.scoutress.KaimuxAdminStats.Services.Playtime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataTransferService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${sqlite.url.survival}")
    private String sqliteUrlSurvival;
    
    @Value("${sqlite.url.skyblock}")
    private String sqliteUrlSkyblock;
    
    @Value("${sqlite.url.creative}")
    private String sqliteUrlCreative;
    
    @Value("${sqlite.url.boxpvp}")
    private String sqliteUrlBoxpvp;
    
    @Value("${sqlite.url.prison}")
    private String sqliteUrlPrison;
    
    @Value("${sqlite.url.events}")
    private String sqliteUrlEvents;

    @Value("${mysql.table.survival}")
    private String survivalTable;
    
    @Value("${mysql.table.skyblock}")
    private String skyblockTable;
    
    @Value("${mysql.table.creative}")
    private String creativeTable;
    
    @Value("${mysql.table.boxpvp}")
    private String boxpvpTable;
    
    @Value("${mysql.table.prison}")
    private String prisonTable;
    
    @Value("${mysql.table.events}")
    private String eventsTable;

    public void transferDataFromSQLiteToMySQL() {
        transferDataFromSQLiteToMySQL(survivalTable, sqliteUrlSurvival);
        transferDataFromSQLiteToMySQL(skyblockTable, sqliteUrlSkyblock);
        transferDataFromSQLiteToMySQL(creativeTable, sqliteUrlCreative);
        transferDataFromSQLiteToMySQL(boxpvpTable, sqliteUrlBoxpvp);
        transferDataFromSQLiteToMySQL(prisonTable, sqliteUrlPrison);
        transferDataFromSQLiteToMySQL(eventsTable, sqliteUrlEvents);
    }

    private void transferDataFromSQLiteToMySQL(String mysqlTable, String sqliteUrl) {
        try (Connection sqliteConnection = DriverManager.getConnection(sqliteUrl)) {
            String querySelect = "SELECT * FROM co_session";

            int rowsTransferred = 0;

            try (Statement statement = sqliteConnection.createStatement();
                 ResultSet resultSet = statement.executeQuery(querySelect)) {

                while (resultSet.next()) {
                    int user = resultSet.getInt("user");
                    int time = resultSet.getInt("time");
                    int action = resultSet.getInt("action");

                    jdbcTemplate.update("INSERT INTO " + mysqlTable + " (user, time, action) VALUES (?, ?, ?)", user, time, action);
                    rowsTransferred++;
                }

                System.out.println("");
                System.out.println("Data transfer from SQLite to MySQL completed for server: " + mysqlTable);
                System.out.println("Rows transferred to " + mysqlTable + ": " + rowsTransferred);
            }
        } catch (SQLException e) {
        }
    }
}
