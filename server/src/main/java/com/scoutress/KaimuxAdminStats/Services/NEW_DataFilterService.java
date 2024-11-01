package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SanitizedSessionData;

@Service
public class NEW_DataFilterService {

  public List<NEW_SanitizedSessionData> sessionsFilterByAid(List<NEW_SanitizedSessionData> data, short aid) {
    return data
        .stream()
        .filter(item -> item.getAid() == aid)
        .collect(Collectors.toList());
  }

  public List<NEW_SanitizedSessionData> sessionsFilterByServer(List<NEW_SanitizedSessionData> data, String server) {
    return data
        .stream()
        .filter(item -> item.getServer().equals(server))
        .collect(Collectors.toList());
  }

  public List<NEW_SanitizedSessionData> filterByAction(List<NEW_SanitizedSessionData> data, boolean action) {
    return data
        .stream()
        .filter(item -> item.isAction() == action)
        .collect(Collectors.toList());
  }
}
