package com.scoutress.KaimuxAdminStats.Services.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;

@Service
public class NEW_AnnualyPlaytimeService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_AnnualPlaytimeRepository annualPlaytimeRepository;

  public NEW_AnnualyPlaytimeService(
      NEW_DataExtractingService dataExtractingService,
      NEW_AnnualPlaytimeRepository annualPlaytimeRepository) {
    this.dataExtractingService = dataExtractingService;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
  }

  public void handleAnnualPlaytime() {
    List<NEW_DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<NEW_AnnualPlaytime> annualPlaytime = calculateAnnualPlaytime(allPlaytime);
    saveAnnualPlaytime(annualPlaytime);
  }

  public List<NEW_AnnualPlaytime> calculateAnnualPlaytime(
      List<NEW_DailyPlaytime> allPlaytime) {

    List<NEW_AnnualPlaytime> handledAnnualPlaytimeData = new ArrayList<>();

    Set<Short> uniqueAids = allPlaytime
        .stream()
        .map(NEW_DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    for (Short aid : uniqueAids) {
      int annualPlaytimeSumByPlayer = allPlaytime
          .stream()
          .filter(playtime -> playtime.getAid() == aid)
          .filter(playtime -> playtime.getDate().isAfter(dateOneYearAgo))
          .mapToInt(playtime -> playtime.getTime())
          .sum();

      NEW_AnnualPlaytime annualPlaytimeData = new NEW_AnnualPlaytime();
      annualPlaytimeData.setAid(aid);
      annualPlaytimeData.setPlaytime(annualPlaytimeSumByPlayer);
      handledAnnualPlaytimeData.add(annualPlaytimeData);
    }
    handledAnnualPlaytimeData.sort(Comparator.comparing(NEW_AnnualPlaytime::getAid));
    return handledAnnualPlaytimeData;
  }

  public void saveAnnualPlaytime(List<NEW_AnnualPlaytime> annualPlaytimeData) {
    annualPlaytimeData.forEach(annualPlaytimeRepository::save);
  }
}
