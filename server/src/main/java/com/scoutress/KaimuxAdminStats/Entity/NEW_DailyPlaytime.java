package com.scoutress.KaimuxAdminStats.Entity;

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
public class NEW_DailyPlaytime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private short aid;

  @Column(name = "time", nullable = false)
  private int time;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "server", nullable = false)
  private String server;
}
