package com.scoutress.KaimuxAdminStats.servicesimpl;

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
    String sql = "INSERT INTO kaimuxstatistics.session_data (id, action, aid, server, time) VALUES (?, ?, ?, ?, ?)";

    Object[][] data = {
        { 1, true, 1, "survival", 1730441000L },
        { 2, false, 1, "survival", 1730441001L },
        { 3, true, 1, "survival", 1730441002L },
        { 4, false, 1, "survival", 1730441004L },
        { 5, true, 1, "survival", 1730441005L },
        { 6, false, 1, "survival", 1730441095L },
        { 7, true, 1, "survival", 1730441100L },
        { 8, false, 1, "survival", 1730441500L },
        { 9, true, 1, "survival", 1730441750L },
        { 10, false, 1, "survival", 1730441999L },
        { 11, true, 1, "survival", 1730442000L },
        { 12, false, 1, "survival", 1730443000L },
        { 13, true, 1, "survival", 1730443001L },
        { 14, false, 1, "survival", 1730443002L },
        { 15, true, 1, "survival", 1730443003L },
        { 16, false, 1, "survival", 1730443004L },
        { 17, true, 1, "survival", 1730443005L },
        { 18, false, 1, "survival", 1730443006L },
        { 19, true, 1, "survival", 1730443007L },
        { 20, false, 1, "survival", 1730443008L }
    };

    for (Object[] row : data) {
      jdbcTemplate.update(sql, row);
    }

    System.out.println("Dummy data uploaded successfully.");
  }
}
