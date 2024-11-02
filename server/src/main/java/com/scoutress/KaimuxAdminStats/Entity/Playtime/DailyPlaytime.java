package com.scoutress.KaimuxAdminStats.Entity.Playtime;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "old_daily_playtime") // will be removed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyPlaytime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Integer employeeId;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "total_survival_playtime", nullable = false)
  private Double totalSurvivalPlaytime = 0.0;

  @Column(name = "total_skyblock_playtime", nullable = false)
  private Double totalSkyblockPlaytime = 0.0;

  @Column(name = "total_creative_playtime", nullable = false)
  private Double totalCreativePlaytime = 0.0;

  @Column(name = "total_boxpvp_playtime", nullable = false)
  private Double totalBoxpvpPlaytime = 0.0;

  @Column(name = "total_Prison_playtime", nullable = false)
  private Double totalPrisonPlaytime = 0.0;

  @Column(name = "total_events_playtime", nullable = false)
  private Double totalEventsPlaytime = 0.0;

  @Column(name = "total_playtime", nullable = false)
  private Double totalPlaytime = 0.0;
}
