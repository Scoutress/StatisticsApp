package com.scoutress.KaimuxAdminStats.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_AveragePlaytime;
import com.scoutress.KaimuxAdminStats.Entity.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_AveragePlaytimeRepository;

@Service
public class NEW_AveragePlaytimeService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_AveragePlaytimeRepository averagePlaytimeRepository;

  public NEW_AveragePlaytimeService(
      NEW_DataExtractingService dataExtractingService,
      NEW_AveragePlaytimeRepository averagePlaytimeRepository) {
    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeRepository = averagePlaytimeRepository;
  }

  public void handleAveragePlaytime() {
    List<NEW_DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<NEW_AveragePlaytime> averagePlaytime = calculateAveragePlaytime(allPlaytime);
    saveAveragePlaytime(averagePlaytime);
  }

  public List<NEW_AveragePlaytime> calculateAveragePlaytime(
      List<NEW_DailyPlaytime> allPlaytime) {

    List<NEW_AveragePlaytime> handledAveragePlaytimeData = new ArrayList<>();

    // TODO nesukurta
    // Set<Short> uniqueAids = allPlaytime
    // .stream()
    // .map(NEW_DailyPlaytime::getAid)
    // .collect(Collectors.toSet());

    // LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    // for (Short aid : uniqueAids) {
    // int annualPlaytimeSumByPlayer = allPlaytime
    // .stream()
    // .filter(playtime -> playtime.getAid() == aid)
    // .filter(playtime -> playtime.getDate().isAfter(dateOneYearAgo))
    // .mapToInt(playtime -> playtime.getTime())
    // .sum();

    // NEW_AveragePlaytime annualPlaytimeData = new NEW_AnnualPlaytime();
    // annualPlaytimeData.setAid(aid);
    // annualPlaytimeData.setPlaytime(annualPlaytimeSumByPlayer);
    // handledAnnualPlaytimeData.add(annualPlaytimeData);
    // }
    // handledAveragePlaytimeData.sort(Comparator.comparing(NEW_AveragePlaytime::getAid));
    return handledAveragePlaytimeData;

  }

  public void saveAveragePlaytime(List<NEW_AveragePlaytime> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeRepository::save);
  }
}
