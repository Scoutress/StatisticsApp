package com.scoutress.KaimuxAdminStats.entity.playtime;

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
@Table(name = "average_playtime_last_year")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AveragePlaytimeLastYear {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "playtime", nullable = false)
  private Double playtime;
}
