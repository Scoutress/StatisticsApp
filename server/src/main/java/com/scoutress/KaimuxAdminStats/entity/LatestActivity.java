package com.scoutress.KaimuxAdminStats.entity;

import com.scoutress.KaimuxAdminStats.servicesImpl.EmployeeDataServiceImpl.HasEmployeeId;

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
@Table(name = "latest_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatestActivity implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "days_since_last_playtime")
  private short daysSinceLastPlaytime;

  @Column(name = "days_since_last_discord_ticket")
  private short daysSinceLastDiscordTicket;

  @Column(name = "days_since_last_minecraft_ticket")
  private short daysSinceLastMinecraftTicket;

  @Column(name = "days_since_last_discord_chat")
  private short daysSinceLastDiscordChat;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
