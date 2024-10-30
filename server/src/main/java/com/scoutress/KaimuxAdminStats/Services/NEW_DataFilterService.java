package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import com.scoutress.KaimuxAdminStats.Entity.DataItem;

public class NEW_DataFilterService {
  public static List<DataItem> sessionsFilterByAid(List<DataItem> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public static List<DataItem> filterByAction(List<DataItem> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.isAction() == action)
        .collect(Collectors.toList());
  }
}
