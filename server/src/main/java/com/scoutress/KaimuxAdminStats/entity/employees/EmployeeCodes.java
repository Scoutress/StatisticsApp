package com.scoutress.KaimuxAdminStats.entity.employees;

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
@Table(name = "employee_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCodes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = true)
  private Short employeeId;

  @Column(name = "survival_id", nullable = true)
  private Short survivalId;

  @Column(name = "skyblock_id", nullable = true)
  private Short skyblockId;

  @Column(name = "creative_id", nullable = true)
  private Short creativeId;

  @Column(name = "boxpvp_id", nullable = true)
  private Short boxpvpId;

  @Column(name = "prison_id", nullable = true)
  private Short prisonId;

  @Column(name = "events_id", nullable = true)
  private Short eventsId;

  @Column(name = "lobby_id", nullable = true)
  private Short lobbyId;

  @Column(name = "discord_id", nullable = true)
  private Short discordId;

  @Column(name = "minecraft_id", nullable = true)
  private Short minecraftId;
}
