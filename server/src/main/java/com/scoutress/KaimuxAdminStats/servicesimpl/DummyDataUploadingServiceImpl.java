package com.scoutress.KaimuxAdminStats.servicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.DummyDataUploadingService;

@Service
public class DummyDataUploadingServiceImpl implements DummyDataUploadingService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void uploadDailyPlaytimeDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.session_data (id, action, aid, server, time) VALUES (?, ?, ?, ?, ?)";

    Object[][] data = {
        { null, true, 1, "survival", 1730441000L },
        { null, false, 1, "survival", 1730441060L },
        { null, true, 1, "survival", 1730441120L },
        { null, false, 1, "survival", 1730441180L },
        { null, true, 1, "survival", 1730441240L },
        { null, false, 1, "survival", 1730441300L },
        { null, true, 1, "survival", 1730441360L },
        { null, false, 1, "survival", 1730441420L },
        { null, true, 1, "survival", 1730441480L },
        { null, false, 1, "survival", 1730441540L },
        { null, true, 2, "survival", 1730441600L },
        { null, false, 2, "survival", 1730441660L },
        { null, true, 2, "survival", 1730441720L },
        { null, false, 2, "survival", 1730441780L },
        { null, true, 2, "survival", 1730441840L },
        { null, false, 2, "survival", 1730441900L },
        { null, true, 2, "survival", 1730441960L },
        { null, false, 2, "survival", 1730442020L },
        { null, true, 3, "survival", 1730442080L },
        { null, false, 3, "survival", 1730442140L },
        { null, true, 3, "survival", 1730442200L },
        { null, false, 3, "survival", 1730442260L },
        { null, true, 3, "survival", 1730442320L },
        { null, false, 3, "survival", 1730442380L },
        { null, true, 3, "survival", 1730442440L },
        { null, false, 3, "survival", 1730442500L },
        { null, true, 3, "survival", 1730442560L },
        { null, false, 3, "survival", 1730442620L },
        { null, true, 1, "survival", 1730442680L },
        { null, false, 1, "survival", 1730442740L }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (playtime) uploaded successfully.");
  }

  @Override
  public void uploadDailyAfkPlaytimeDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.afk_playtime_raw_data (id, aid, time, action, server) VALUES (?, ?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 1730441050L, true, "survival" },
        { null, 1, 1730441070L, false, "survival" },
        { null, 2, 1730441250L, true, "survival" },
        { null, 2, 1730441270L, false, "survival" },
        { null, 3, 1730441450L, true, "survival" },
        { null, 3, 1730441470L, false, "survival" },
        { null, 1, 1730441500L, true, "survival" },
        { null, 1, 1730441520L, false, "survival" },
        { null, 2, 1730441700L, true, "survival" },
        { null, 2, 1730441720L, false, "survival" },
        { null, 3, 1730441900L, true, "survival" },
        { null, 3, 1730441920L, false, "survival" },
        { null, 1, 1730442100L, true, "survival" },
        { null, 1, 1730442120L, false, "survival" },
        { null, 2, 1730442300L, true, "survival" },
        { null, 2, 1730442320L, false, "survival" },
        { null, 3, 1730442500L, true, "survival" },
        { null, 3, 1730442520L, false, "survival" },
        { null, 1, 1730442700L, true, "survival" },
        { null, 1, 1730442720L, false, "survival" },
        { null, 2, 1730442900L, true, "survival" },
        { null, 2, 1730442920L, false, "survival" },
        { null, 3, 1730443100L, true, "survival" },
        { null, 3, 1730443120L, false, "survival" },
        { null, 1, 1730443300L, true, "survival" },
        { null, 1, 1730443320L, false, "survival" },
        { null, 2, 1730443500L, true, "survival" },
        { null, 2, 1730443520L, false, "survival" },
        { null, 3, 1730443700L, true, "survival" },
        { null, 3, 1730443720L, false, "survival" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (afk playtime) uploaded successfully.");
  }

  @Override
  public void uploadDailyDiscordTicketsDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_discord_tickets (id, aid, ticket_count, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 10, "2024-01-01" },
        { null, 1, 12, "2024-01-02" },
        { null, 2, 8, "2024-01-01" },
        { null, 2, 9, "2024-01-02" },
        { null, 3, 15, "2024-01-01" },
        { null, 3, 17, "2024-01-02" },
        { null, 1, 20, "2024-01-03" },
        { null, 1, 18, "2024-01-04" },
        { null, 2, 14, "2024-01-03" },
        { null, 2, 13, "2024-01-04" },
        { null, 3, 22, "2024-01-03" },
        { null, 3, 25, "2024-01-04" },
        { null, 1, 30, "2024-01-05" },
        { null, 1, 28, "2024-01-06" },
        { null, 2, 18, "2024-01-05" },
        { null, 2, 16, "2024-01-06" },
        { null, 3, 35, "2024-01-05" },
        { null, 3, 38, "2024-01-06" },
        { null, 1, 40, "2024-01-07" },
        { null, 1, 42, "2024-01-08" },
        { null, 2, 30, "2024-01-07" },
        { null, 2, 32, "2024-01-08" },
        { null, 3, 50, "2024-01-07" },
        { null, 3, 52, "2024-01-08" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (dc tickets) uploaded successfully.");
  }

  @Override
  public void uploadDailyDiscordTicketsComparedDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_discord_tickets_compared (id, aid, value, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 10, "2024-01-01" },
        { null, 1, 12, "2024-01-02" },
        { null, 2, 8, "2024-01-01" },
        { null, 2, 9, "2024-01-02" },
        { null, 3, 15, "2024-01-01" },
        { null, 3, 17, "2024-01-02" },
        { null, 1, 20, "2024-01-03" },
        { null, 1, 18, "2024-01-04" },
        { null, 2, 14, "2024-01-03" },
        { null, 2, 13, "2024-01-04" },
        { null, 3, 22, "2024-01-03" },
        { null, 3, 25, "2024-01-04" },
        { null, 1, 30, "2024-01-05" },
        { null, 1, 28, "2024-01-06" },
        { null, 2, 18, "2024-01-05" },
        { null, 2, 16, "2024-01-06" },
        { null, 3, 35, "2024-01-05" },
        { null, 3, 38, "2024-01-06" },
        { null, 1, 40, "2024-01-07" },
        { null, 1, 42, "2024-01-08" },
        { null, 2, 30, "2024-01-07" },
        { null, 2, 32, "2024-01-08" },
        { null, 3, 50, "2024-01-07" },
        { null, 3, 52, "2024-01-08" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (dc tickets compared) uploaded successfully.");
  }

  @Override
  public void uploadDailyDiscordMessagesDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_discord_messages (id, aid, msg_count, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 50, "2024-01-01" },
        { null, 1, 60, "2024-01-02" },
        { null, 2, 30, "2024-01-01" },
        { null, 2, 25, "2024-01-02" },
        { null, 3, 45, "2024-01-01" },
        { null, 3, 55, "2024-01-02" },
        { null, 1, 70, "2024-01-03" },
        { null, 1, 80, "2024-01-04" },
        { null, 2, 40, "2024-01-03" },
        { null, 2, 35, "2024-01-04" },
        { null, 3, 90, "2024-01-03" },
        { null, 3, 85, "2024-01-04" },
        { null, 1, 95, "2024-01-05" },
        { null, 1, 100, "2024-01-06" },
        { null, 2, 50, "2024-01-05" },
        { null, 2, 45, "2024-01-06" },
        { null, 3, 120, "2024-01-05" },
        { null, 3, 130, "2024-01-06" },
        { null, 1, 150, "2024-01-07" },
        { null, 1, 160, "2024-01-08" },
        { null, 2, 80, "2024-01-07" },
        { null, 2, 90, "2024-01-08" },
        { null, 3, 170, "2024-01-07" },
        { null, 3, 180, "2024-01-08" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (dc messages) uploaded successfully.");
  }

  @Override
  public void uploadDailyDiscordMessagesComparedDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_discord_messages_comp (id, aid, value, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 1730441050.0, "2024-01-01" },
        { null, 1, 1730441070.0, "2024-01-02" },
        { null, 2, 1730441250.0, "2024-01-01" },
        { null, 2, 1730441270.0, "2024-01-02" },
        { null, 3, 1730441450.0, "2024-01-01" },
        { null, 3, 1730441470.0, "2024-01-02" },
        { null, 1, 1730441500.0, "2024-01-03" },
        { null, 1, 1730441520.0, "2024-01-04" },
        { null, 2, 1730441700.0, "2024-01-03" },
        { null, 2, 1730441720.0, "2024-01-04" },
        { null, 3, 1730441900.0, "2024-01-03" },
        { null, 3, 1730441920.0, "2024-01-04" },
        { null, 1, 1730442100.0, "2024-01-05" },
        { null, 1, 1730442120.0, "2024-01-06" },
        { null, 2, 1730442300.0, "2024-01-05" },
        { null, 2, 1730442320.0, "2024-01-06" },
        { null, 3, 1730442500.0, "2024-01-05" },
        { null, 3, 1730442520.0, "2024-01-06" },
        { null, 1, 1730442700.0, "2024-01-07" },
        { null, 1, 1730442720.0, "2024-01-08" },
        { null, 2, 1730442900.0, "2024-01-07" },
        { null, 2, 1730442920.0, "2024-01-08" },
        { null, 3, 1730443100.0, "2024-01-07" },
        { null, 3, 1730443120.0, "2024-01-08" },
        { null, 1, 1730443300.0, "2024-01-09" },
        { null, 1, 1730443320.0, "2024-01-10" },
        { null, 2, 1730443500.0, "2024-01-09" },
        { null, 2, 1730443520.0, "2024-01-10" },
        { null, 3, 1730443700.0, "2024-01-09" },
        { null, 3, 1730443720.0, "2024-01-10" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (dc messages compared) uploaded successfully.");
  }

  @Override
  public void uploadDailyMinecraftTicketsDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_minecraft_tickets (id, aid, ticket_count, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 1, "2024-01-01" },
        { null, 1, 2, "2024-01-02" },
        { null, 2, 3, "2024-01-01" },
        { null, 2, 4, "2024-01-02" },
        { null, 3, 5, "2024-01-01" },
        { null, 3, 6, "2024-01-02" },
        { null, 1, 7, "2024-01-03" },
        { null, 1, 8, "2024-01-04" },
        { null, 2, 9, "2024-01-03" },
        { null, 2, 10, "2024-01-04" },
        { null, 3, 1, "2024-01-03" },
        { null, 3, 2, "2024-01-04" },
        { null, 1, 3, "2024-01-05" },
        { null, 1, 4, "2024-01-06" },
        { null, 2, 5, "2024-01-05" },
        { null, 2, 6, "2024-01-06" },
        { null, 3, 7, "2024-01-05" },
        { null, 3, 8, "2024-01-06" },
        { null, 1, 9, "2024-01-07" },
        { null, 1, 10, "2024-01-08" },
        { null, 2, 1, "2024-01-07" },
        { null, 2, 2, "2024-01-08" },
        { null, 3, 3, "2024-01-07" },
        { null, 3, 4, "2024-01-08" },
        { null, 1, 5, "2024-01-09" },
        { null, 1, 6, "2024-01-10" },
        { null, 2, 7, "2024-01-09" },
        { null, 2, 8, "2024-01-10" },
        { null, 3, 9, "2024-01-09" },
        { null, 3, 10, "2024-01-10" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (mc tickets) uploaded successfully.");
  }

  @Override
  public void uploadDailyMinecraftTicketsComparedDummyData() {
    String dataSql = "INSERT INTO kaimuxstatistics.daily_minecraft_tickets_comp (id, aid, value, date) VALUES (?, ?, ?, ?)";

    Object[][] data = {
        { null, 1, 1, "2024-01-01" },
        { null, 1, 2, "2024-01-02" },
        { null, 2, 3, "2024-01-01" },
        { null, 2, 4, "2024-01-02" },
        { null, 3, 5, "2024-01-01" },
        { null, 3, 6, "2024-01-02" },
        { null, 1, 7, "2024-01-03" },
        { null, 1, 8, "2024-01-04" },
        { null, 2, 9, "2024-01-03" },
        { null, 2, 10, "2024-01-04" },
        { null, 3, 1, "2024-01-03" },
        { null, 3, 2, "2024-01-04" },
        { null, 1, 3, "2024-01-05" },
        { null, 1, 4, "2024-01-06" },
        { null, 2, 5, "2024-01-05" },
        { null, 2, 6, "2024-01-06" },
        { null, 3, 7, "2024-01-05" },
        { null, 3, 8, "2024-01-06" },
        { null, 1, 9, "2024-01-07" },
        { null, 1, 10, "2024-01-08" },
        { null, 2, 1, "2024-01-07" },
        { null, 2, 2, "2024-01-08" },
        { null, 3, 3, "2024-01-07" },
        { null, 3, 4, "2024-01-08" },
        { null, 1, 5, "2024-01-09" },
        { null, 1, 6, "2024-01-10" },
        { null, 2, 7, "2024-01-09" },
        { null, 2, 8, "2024-01-10" },
        { null, 3, 9, "2024-01-09" },
        { null, 3, 10, "2024-01-10" }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(dataSql, row);
    }

    System.out.println("Dummy data (mc tickets compared) uploaded successfully.");
  }
}
