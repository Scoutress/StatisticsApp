package com.scoutress.KaimuxAdminStats.servicesimpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

  @Override
  public List<AnnualPlaytime> calculateAnnualPlaytime(
      List<DailyPlaytime> allPlaytime) {

    List<AnnualPlaytime> handledAnnualPlaytimeData = new ArrayList<>();

    Set<Short> uniqueAids = allPlaytime
        .stream()
        .map(DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    for (Short aid : uniqueAids) {
      int annualPlaytimeSumByPlayer = allPlaytime
          .stream()
          .filter(playtime -> playtime.getAid() == aid)
          .filter(playtime -> playtime.getDate().isAfter(dateOneYearAgo))
          .mapToInt(playtime -> playtime.getTime())
          .sum();

      AnnualPlaytime annualPlaytimeData = new AnnualPlaytime();
      annualPlaytimeData.setAid(aid);
      annualPlaytimeData.setPlaytime(annualPlaytimeSumByPlayer);
      handledAnnualPlaytimeData.add(annualPlaytimeData);
    }
    handledAnnualPlaytimeData.sort(Comparator.comparing(AnnualPlaytime::getAid));
    return handledAnnualPlaytimeData;
  }

  @Override
  public void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData) {
    annualPlaytimeData.forEach(annualPlaytimeRepository::save);
  }
}
