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
  private Short employeeId; // Employee entity

  @Column(name = "survival_id", nullable = true)
  private Short survivalId; // Survival server CoreProtect

  @Column(name = "skyblock_id", nullable = true)
  private Short skyblockId; // Skyblock server CoreProtect

  @Column(name = "creative_id", nullable = true)
  private Short creativeId; // Creative server CoreProtect

  @Column(name = "boxpvp_id", nullable = true)
  private Short boxpvpId; // Boxpvp server CoreProtect

  @Column(name = "prison_id", nullable = true)
  private Short prisonId; // Prison server CoreProtect

  @Column(name = "events_id", nullable = true)
  private Short eventsId; // Events server CoreProtect

  @Column(name = "lobby_id", nullable = true)
  private Short lobbyId; // Lobby server CoreProtect

  @Column(name = "discord_id", nullable = true)
  private Short discordId; // Kaimux website API Discord user code

  @Column(name = "minecraft_id", nullable = true)
  private Short minecraftId; // Kaimux website API Minecraft user code

  @Column(name = "discord_user_id", nullable = true)
  private Long discordUserId; // Default Discord user ID
}
