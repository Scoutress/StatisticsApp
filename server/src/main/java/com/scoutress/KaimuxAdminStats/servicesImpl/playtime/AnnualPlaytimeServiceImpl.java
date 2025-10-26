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
    log.info("=== [START] Annual playtime calculation process ===");
    long startTime = System.currentTimeMillis();

    try {
      List<DailyPlaytime> allPlaytime = dailyPlaytimeRepository.findAll();
      log.debug("Fetched {} daily playtime records from database.", allPlaytime.size());

      if (allPlaytime.isEmpty()) {
        log.warn("⚠️ No daily playtime records found — skipping process.");
        return;
      }

      List<AnnualPlaytime> annualPlaytime = calculateAnnualPlaytime(allPlaytime);
      log.debug("Calculated {} annual playtime records.", annualPlaytime.size());

      saveAnnualPlaytime(annualPlaytime);

      long elapsed = System.currentTimeMillis() - startTime;
      log.info("✅ [END] Annual playtime processing completed successfully in {} ms ({} s).",
          elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Critical error during annual playtime calculation: {}", e.getMessage(), e);
    }
  }

  private List<AnnualPlaytime> calculateAnnualPlaytime(List<DailyPlaytime> allPlaytime) {
    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);
    log.info("Calculating total playtime since {}", dateOneYearAgo);

    Map<Short, Double> annualPlaytimeMap = allPlaytime
        .stream()
        .filter(playtime -> !playtime.getDate().isBefore(dateOneYearAgo))
        .collect(Collectors.groupingBy(
            DailyPlaytime::getEmployeeId,
            Collectors.summingDouble(DailyPlaytime::getTimeInHours)));

    log.debug("Aggregated playtime for {} employees.", annualPlaytimeMap.size());

    List<AnnualPlaytime> handledAnnualPlaytimeData = annualPlaytimeMap.entrySet()
        .stream()
        .map(entry -> {
          AnnualPlaytime ap = new AnnualPlaytime();
          ap.setEmployeeId(entry.getKey());
          ap.setPlaytimeInHours(entry.getValue());
          log.trace("Employee {} — total annual playtime: {} hours", entry.getKey(), entry.getValue());
          return ap;
        })
        .sorted(Comparator.comparing(AnnualPlaytime::getEmployeeId))
        .collect(Collectors.toList());

    log.info("✅ Aggregated annual playtime data prepared for {} employees.", handledAnnualPlaytimeData.size());
    return handledAnnualPlaytimeData;
  }

  private void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData) {
    log.info("Saving {} annual playtime records to database...", annualPlaytimeData.size());

    int processed = 0;
    for (AnnualPlaytime annualPlaytime : annualPlaytimeData) {
      try {
        Short empId = annualPlaytime.getEmployeeId();
        double hours = annualPlaytime.getPlaytimeInHours();

        AnnualPlaytime existingPlaytime = annualPlaytimeRepository.findByEmployeeId(empId);

        if (existingPlaytime != null) {
          double oldValue = existingPlaytime.getPlaytimeInHours();
          existingPlaytime.setPlaytimeInHours(hours);
          annualPlaytimeRepository.save(existingPlaytime);
          log.trace("Updated employee {} — old={}h → new={}h", empId, oldValue, hours);
        } else {
          annualPlaytimeRepository.save(annualPlaytime);
          log.trace("Inserted new record for employee {} — {} hours", empId, hours);
        }

      } catch (Exception e) {
        log.error("❌ Failed to save annual playtime for employee {}: {}", annualPlaytime.getEmployeeId(),
            e.getMessage(), e);
      }

      processed++;
      if (processed % 25 == 0 || processed == annualPlaytimeData.size()) {
        int percent = (int) ((processed / (double) annualPlaytimeData.size()) * 100);
        log.info("Progress: {}/{} employees processed ({}%)", processed, annualPlaytimeData.size(), percent);
      }
    }

    log.info("✅ All annual playtime records saved successfully ({} total).", annualPlaytimeData.size());
  }
}
