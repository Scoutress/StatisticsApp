package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;

@Service
public class DataFilterServiceImpl implements DataFilterService {

  @Override
  public List<SessionDataItem> filterSessionsByAid(List<SessionDataItem> data, short aid) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> item.getEmployeeId() == aid)
        .toList();
  }

  @Override
  public List<SessionDataItem> filterSessionsByServer(List<SessionDataItem> data, String server) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> Objects.equals(item.getServer(), server))
        .toList();
  }

  @Override
  public List<SessionDataItem> filterSessionsByAction(List<SessionDataItem> data, boolean action) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .toList();
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByAid(List<SanitizedSessionData> data, short aid) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> item.getEmployeeId() == aid)
        .toList();
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByServer(List<SanitizedSessionData> data, String server) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> Objects.equals(item.getServer(), server))
        .toList();
  }

  @Override
  public List<SanitizedSessionData> filterSanitizedSessionsByAction(List<SanitizedSessionData> data, boolean action) {
    if (data == null || data.isEmpty())
      return List.of();

    return data
        .stream()
        .filter(item -> item.getActionAsBoolean() == action)
        .toList();
  }
}
