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
  public void uploadPlaytimeSessionsDummyData() {
    String sessionDataSql = "INSERT INTO kaimuxstatistics.session_data (id, action, aid, server, time) VALUES (?, ?, ?, ?, ?)";
    String afkDataSql = "INSERT INTO kaimuxstatistics.afk_playtime_raw_data (id, aid, time, action, server) VALUES (?, ?, ?, ?, ?)";

    Object[][] sessionData = {
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

    Object[][] afkData = {
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

    for (Object[] row : sessionData) {
      jdbcTemplate.update(sessionDataSql, row);
    }

    for (Object[] row : afkData) {
      jdbcTemplate.update(afkDataSql, row);
    }

    System.out.println("Dummy data uploaded successfully.");
  }
}
