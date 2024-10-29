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
}
