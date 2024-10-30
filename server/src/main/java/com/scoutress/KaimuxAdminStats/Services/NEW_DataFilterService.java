package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;

@Service
public class NEW_DataFilterService {

  public static List<NEW_SessionDataItem> sessionsFilterByAid(List<NEW_SessionDataItem> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public static List<NEW_SessionDataItem> filterByAction(List<NEW_SessionDataItem> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.isAction() == action)
        .collect(Collectors.toList());
  }
}
