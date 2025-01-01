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
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualyPlaytimeService;

@Service
public class AnnualyPlaytimeServiceImpl implements AnnualyPlaytimeService {

  private final DataExtractingService dataExtractingService;
  private final AnnualPlaytimeRepository annualPlaytimeRepository;

  public AnnualyPlaytimeServiceImpl(
      DataExtractingService dataExtractingService,
      AnnualPlaytimeRepository annualPlaytimeRepository) {

    this.dataExtractingService = dataExtractingService;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
  }

  @Override
  public void handleAnnualPlaytime() {
    List<DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<AnnualPlaytime> annualPlaytime = calculateAnnualPlaytime(allPlaytime);
    saveAnnualPlaytime(annualPlaytime);
  }

  public List<AnnualPlaytime> calculateAnnualPlaytime(List<DailyPlaytime> allPlaytime) {
    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    Map<Short, Double> annualPlaytimeMap = allPlaytime
        .stream()
        .filter(playtime -> !playtime.getDate().isBefore(dateOneYearAgo))
        .collect(Collectors.groupingBy(
            DailyPlaytime::getAid,
            Collectors.summingDouble(DailyPlaytime::getTime)));

    List<AnnualPlaytime> handledAnnualPlaytimeData = annualPlaytimeMap
        .entrySet()
        .stream()
        .map(entry -> {
          AnnualPlaytime annualPlaytime = new AnnualPlaytime();
          annualPlaytime.setAid(entry.getKey());
          annualPlaytime.setPlaytime(entry.getValue() / 3600);
          return annualPlaytime;
        })
        .sorted(Comparator.comparing(AnnualPlaytime::getAid))
        .collect(Collectors.toList());

    return handledAnnualPlaytimeData;
  }

  public void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData) {
    annualPlaytimeData.forEach(annualPlaytime -> {
      AnnualPlaytime existingPlaytime = annualPlaytimeRepository.findByAid(annualPlaytime.getAid());

      if (existingPlaytime != null) {
        existingPlaytime.setPlaytime(annualPlaytime.getPlaytime());
        annualPlaytimeRepository.save(existingPlaytime);
      } else {
        annualPlaytimeRepository.save(annualPlaytime);
      }
    });
  }
}
