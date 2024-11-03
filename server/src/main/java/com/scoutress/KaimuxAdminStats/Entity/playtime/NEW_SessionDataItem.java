package com.scoutress.KaimuxAdminStats.Entity.playtime;

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
@Table(name = "session_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NEW_SessionDataItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private short aid;

  @Column(name = "time", nullable = false)
  private long time;

  @Column(name = "action", nullable = false)
  private boolean action;

  @Column(name = "server", nullable = false)
  private String server;

  public boolean getActionAsBoolean() {
    return action;
  }
}
