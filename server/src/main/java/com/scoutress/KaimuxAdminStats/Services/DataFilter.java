package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;

public class DataFilter {
  public static List<DataItem> sessionsFilterByAid(List<DataItem> dataList, short aid) {
    return dataList
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public static List<DataItem> filterByAction(List<DataItem> dataList, boolean action) {
    return dataList
        .stream()
        .filter(item -> item.isAction() == action)
        .collect(Collectors.toList());
  }

  public static List<DataItem> filterLogins(List<DataItem> data) {
    return data.stream().filter(item -> item.isAction()).collect(Collectors.toList());
  }

  public static List<DataItem> filterLogouts(List<DataItem> data) {
    return data.stream().filter(item -> !item.isAction()).collect(Collectors.toList());
  }

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
