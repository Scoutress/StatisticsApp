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
@Table(name = "session_data_plugin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDataPlugin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "uuid", nullable = false)
  private String uuid;

  @Column(name = "time", nullable = false)
  private long time;

  @Column(name = "action", nullable = false)
  private boolean action;

  public boolean getActionAsBoolean() {
    return action;
  }
}
