package com.scoutress.KaimuxAdminStats.entity.playtime;

import java.time.LocalDateTime;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "login_logout_times")
public class LoginLogoutTimes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "server_name", nullable = false, length = 255)
  private String serverName;

  @Column(name = "login_time", nullable = false)
  private LocalDateTime loginTime;

  @Column(name = "logout_time", nullable = false)
  private LocalDateTime logoutTime;

}
