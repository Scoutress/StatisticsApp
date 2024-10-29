package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;

public class DataFilter {
  public static List<DataItem> sessionsFilterByAid(List<DataItem> dataList, short aidValue) {
    return dataList
        .stream()
        .filter(item -> item.getAid() == aidValue)
        .collect(Collectors.toList());
  }
}
