package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_PlaytimeSessionsRepository;

@Service
public class NEW_DataExtractingService {

  public final NEW_PlaytimeSessionsRepository playtimeSessionsRepository;

  public NEW_DataExtractingService(NEW_PlaytimeSessionsRepository playtimeSessionsRepository) {
    this.playtimeSessionsRepository = playtimeSessionsRepository;
  }

  public List<LoginLogoutTimes> getLoginLogoutTimes() {
    return playtimeSessionsRepository.findAll();
  }
}
