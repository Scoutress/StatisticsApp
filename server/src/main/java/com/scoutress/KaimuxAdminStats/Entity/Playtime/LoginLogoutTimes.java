package com.scoutress.KaimuxAdminStats.Entity.Playtime;

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
@Table(name = "login_logout_times")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogoutTimes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Integer employeeId;

  @Column(name = "server_name", nullable = false)
  private String serverName;

  @Column(name = "login_time", nullable = false)
  private LocalDateTime loginTime;

  @Column(name = "logout_time", nullable = false)
  private LocalDateTime logoutTime;
}
