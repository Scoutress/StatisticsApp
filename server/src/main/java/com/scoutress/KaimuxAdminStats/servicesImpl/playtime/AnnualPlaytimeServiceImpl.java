package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualPlaytimeService;

@Service
public class AnnualPlaytimeServiceImpl implements AnnualPlaytimeService {

  private static final Logger log = LoggerFactory.getLogger(AnnualPlaytimeServiceImpl.class);

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
    log.info("=== Starting annual playtime calculation ===");

    List<DailyPlaytime> allPlaytime = getDailyPlaytimeData();
    log.debug("Fetched {} daily playtime records from database.", allPlaytime.size());

    List<AnnualPlaytime> annualPlaytime = calculateAnnualPlaytime(allPlaytime);
    log.debug("Calculated {} annual playtime records.", annualPlaytime.size());

    saveAnnualPlaytime(annualPlaytime);

    log.info("✅ Annual playtime processing completed successfully.");
  }

  private List<DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  private List<AnnualPlaytime> calculateAnnualPlaytime(List<DailyPlaytime> allPlaytime) {
    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    log.debug("Filtering playtime data from {} until now.", dateOneYearAgo);

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

    log.debug("Aggregated annual playtime for {} employees.", handledAnnualPlaytimeData.size());

    return handledAnnualPlaytimeData;
  }

  private void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData) {

    log.info("Saving {} annual playtime records to database...", annualPlaytimeData.size());

    annualPlaytimeData.forEach(annualPlaytime -> {
      try {
        AnnualPlaytime existingPlaytime = annualPlaytimeRepository.findByEmployeeId(annualPlaytime.getEmployeeId());

        if (existingPlaytime != null) {
          existingPlaytime.setPlaytimeInHours(annualPlaytime.getPlaytimeInHours());
          annualPlaytimeRepository.save(existingPlaytime);

          log.debug("Updated playtime for employee ID {}", annualPlaytime.getEmployeeId());

        } else {
          annualPlaytimeRepository.save(annualPlaytime);

          log.debug("Inserted new playtime record for employee ID {}", annualPlaytime.getEmployeeId());
        }
      } catch (Exception e) {
        log.error("❌ Failed to save playtime for employee ID {}: {}", annualPlaytime.getEmployeeId(), e.getMessage(),
            e);
      }
    });

    log.info("✅ All annual playtime records saved successfully.");
  }
}
