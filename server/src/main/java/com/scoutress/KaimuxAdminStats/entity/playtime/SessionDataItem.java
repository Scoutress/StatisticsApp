package com.scoutress.KaimuxAdminStats.entity.playtime;

import com.scoutress.KaimuxAdminStats.services.HasEmployeeId;

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
public class SessionDataItem implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private short employeeId;

  @Column(name = "time", nullable = false)
  private long time;

  @Column(name = "action", nullable = false)
  private boolean action;

  @Column(name = "server", nullable = false)
  private String server;

  public boolean getActionAsBoolean() {
    return action;
  }

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
