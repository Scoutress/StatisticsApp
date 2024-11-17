package com.scoutress.KaimuxAdminStats.entity.afkPlaytime;

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
@Table(name = "afk_playtime_raw_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfkPlaytimeRawData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "time", nullable = false)
  private Long time;

  @Column(name = "action", nullable = false)
  private Boolean action;

  @Column(name = "server", nullable = false)
  private String server;

  public Boolean getActionAsBoolean() {
    return action;
  }
}
