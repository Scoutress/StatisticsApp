package com.scoutress.KaimuxAdminStats.entity;

import java.time.LocalDateTime;

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
@Table(name = "project_visitors_raw_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVisitorsRawData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ip", nullable = false)
  private String ip;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

  @Column(name = "is_premium", nullable = false)
  private boolean isPremium;

  // TODO: type - 2 teoreticly means login to page. Still need to confirm that
  @Column(name = "type", nullable = false)
  private int type;

  public boolean getPremiumStatusAsBoolean() {
    return isPremium;
  }
}
