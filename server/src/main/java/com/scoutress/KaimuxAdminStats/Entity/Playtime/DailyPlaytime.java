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
@Table(name = "daily_playtime")
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
  private Double totalSurvivalPlaytime;

  @Column(name = "total_skyblock_playtime", nullable = false)
  private Double totalSkyblockPlaytime;

  @Column(name = "total_creative_playtime", nullable = false)
  private Double totalCreativePlaytime;

  @Column(name = "total_boxpvp_playtime", nullable = false)
  private Double totalBoxpvpPlaytime;

  @Column(name = "total_Prison_playtime", nullable = false)
  private Double totalPrisonPlaytime;

  @Column(name = "total_events_playtime", nullable = false)
  private Double totalEventsPlaytime;

  @Column(name = "total_playtime", nullable = false)
  private Double totalPlaytime;
}
