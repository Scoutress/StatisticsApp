package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;

@Service
public class NEW_DataProcessingService {
  public static Long getFirstLoginTime(List<DataItem> logins) {
    return logins.isEmpty() ? null : logins.get(0).getTime();
  }

  public static Long getFirstLogoutTime(List<DataItem> logouts) {
    return logouts.isEmpty() ? null : logouts.get(0).getTime();
  }

  public static long calculateSessionDuration(Long loginTime, Long logoutTime) {
    if (loginTime == null || logoutTime == null || logoutTime < loginTime) {
      return 0;
    }
    return logoutTime - loginTime;
  }
}
