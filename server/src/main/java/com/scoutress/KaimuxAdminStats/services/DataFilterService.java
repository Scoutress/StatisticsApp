package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;

public interface DataFilterService {

  public List<SessionDataItem> filterSessionsByAid(
      List<SessionDataItem> data, short aid);

  public List<SessionDataItem> filterSessionsByServer(
      List<SessionDataItem> data, String server);

  public List<SessionDataItem> filterSessionsByAction(
      List<SessionDataItem> data, boolean action);

  public List<SanitizedSessionData> filterSanitizedSessionsByAid(
      List<SanitizedSessionData> data, short aid);

  public List<SanitizedSessionData> filterSanitizedSessionsByServer(
      List<SanitizedSessionData> data, String server);

  public List<SanitizedSessionData> filterSanitizedSessionsByAction(
      List<SanitizedSessionData> data, boolean action);
}
