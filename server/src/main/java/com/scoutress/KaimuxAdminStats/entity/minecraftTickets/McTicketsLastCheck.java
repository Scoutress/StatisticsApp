package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

import java.time.LocalDate;

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
@Table(name = "mc_tickets_last_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McTicketsLastCheck {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  public McTicketsLastCheck(Short employeeId, LocalDate date) {
    this.employeeId = employeeId;
    this.date = date;
  }
}
