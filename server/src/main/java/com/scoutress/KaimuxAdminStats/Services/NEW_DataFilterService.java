package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDataItem;

@Service
public class NEW_DataFilterService {

  public List<NEW_SessionDataItem> filterSessionsByAid(
      List<NEW_SessionDataItem> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public List<NEW_SessionDataItem> filterSessionsByServer(
      List<NEW_SessionDataItem> data, String server) {
    return data
        .stream()
        .filter(item -> item.getServer().equals(server))
        .collect(Collectors.toList());
  }

  public List<NEW_SessionDataItem> filterSessionsByAction(
      List<NEW_SessionDataItem> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .collect(Collectors.toList());
  }

  public List<NEW_SanitizedSessionData> filterSanitizedSessionsByAid(
      List<NEW_SanitizedSessionData> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public List<NEW_SanitizedSessionData> filterSanitizedSessionsByServer(
      List<NEW_SanitizedSessionData> data, String server) {
    return data
        .stream()
        .filter(item -> item.getServer().equals(server))
        .collect(Collectors.toList());
  }

  public List<NEW_SanitizedSessionData> filterSanitizedSessionsByAction(
      List<NEW_SanitizedSessionData> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .collect(Collectors.toList());
  }
}
