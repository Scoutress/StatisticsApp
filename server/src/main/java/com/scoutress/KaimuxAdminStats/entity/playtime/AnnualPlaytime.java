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
@Table(name = "annual_playtime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualPlaytime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private short aid;

  @Column(name = "playtime", nullable = false)
  private int playtime;
}
