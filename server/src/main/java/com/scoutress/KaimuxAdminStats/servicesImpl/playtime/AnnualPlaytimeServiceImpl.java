package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualPlaytimeService;

@Service
public class AnnualPlaytimeServiceImpl implements AnnualPlaytimeService {

  private final AnnualPlaytimeRepository annualPlaytimeRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;

  public AnnualPlaytimeServiceImpl(
      AnnualPlaytimeRepository annualPlaytimeRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository) {
    this.annualPlaytimeRepository = annualPlaytimeRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
  }

  @Override
  public void handleAnnualPlaytime() {
    List<DailyPlaytime> allPlaytime = getDailyPlaytimeData();
    List<AnnualPlaytime> annualPlaytime = calculateAnnualPlaytime(allPlaytime);
    saveAnnualPlaytime(annualPlaytime);
  }

  private List<DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  private List<AnnualPlaytime> calculateAnnualPlaytime(List<DailyPlaytime> allPlaytime) {
    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    Map<Short, Double> annualPlaytimeMap = allPlaytime
        .stream()
        .filter(playtime -> !playtime.getDate().isBefore(dateOneYearAgo))
        .collect(Collectors.groupingBy(
            DailyPlaytime::getEmployeeId,
            Collectors.summingDouble(DailyPlaytime::getTimeInHours)));

    List<AnnualPlaytime> handledAnnualPlaytimeData = annualPlaytimeMap
        .entrySet()
        .stream()
        .map(entry -> {
          AnnualPlaytime annualPlaytime = new AnnualPlaytime();
          annualPlaytime.setEmployeeId(entry.getKey());
          annualPlaytime.setPlaytimeInHours(entry.getValue());
          return annualPlaytime;
        })
        .sorted(Comparator.comparing(AnnualPlaytime::getEmployeeId))
        .collect(Collectors.toList());

    return handledAnnualPlaytimeData;
  }

  private void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData) {
    annualPlaytimeData.forEach(annualPlaytime -> {
      AnnualPlaytime existingPlaytime = annualPlaytimeRepository.findByEmployeeId(annualPlaytime.getEmployeeId());

      if (existingPlaytime != null) {
        existingPlaytime.setPlaytimeInHours(annualPlaytime.getPlaytimeInHours());
        annualPlaytimeRepository.save(existingPlaytime);
      } else {
        annualPlaytimeRepository.save(annualPlaytime);
      }
    });
  }
}
