package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

public interface SQLiteToMySQLService {

  void initializeUsersDatabase(List<String> servers);

  void initializePlaytimeSessionsDatabase(List<String> servers);
}
