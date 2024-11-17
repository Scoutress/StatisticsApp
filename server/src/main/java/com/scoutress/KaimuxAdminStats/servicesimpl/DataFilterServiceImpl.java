package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;

@Service
public class DataFilterServiceImpl implements DataFilterService {

  @Override
  public List<SessionDataItem> filterSessionsByAid(
      List<SessionDataItem> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  @Override
  public List<SessionDataItem> filterSessionsByServer(
      List<SessionDataItem> data, String server) {
    return data
        .stream()
        .filter(item -> item.getServer().equals(server))
        .collect(Collectors.toList());
  }

  @Override
  public List<SessionDataItem> filterSessionsByAction(
      List<SessionDataItem> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .collect(Collectors.toList());
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByAid(
      List<SanitizedSessionData> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByServer(
      List<SanitizedSessionData> data, String server) {
    return data
        .stream()
        .filter(item -> item.getServer().equals(server))
        .collect(Collectors.toList());
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByAction(
      List<SanitizedSessionData> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .collect(Collectors.toList());
  }
}
