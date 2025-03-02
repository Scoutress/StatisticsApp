package com.scoutress.KaimuxAdminStats.services;

public interface TaskService {

  void runScheduledTasks();

  void runBackupDataUploadingTasks();
}
