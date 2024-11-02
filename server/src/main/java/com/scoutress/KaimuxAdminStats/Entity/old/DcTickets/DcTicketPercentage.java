package com.scoutress.KaimuxAdminStats.Entity.old.DcTickets;

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
@Table(name = "dc_ticket_percentage")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DcTicketPercentage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id")
  private Integer employeeId;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "percentage")
  private Double percentage;

  public DcTicketPercentage(Integer employeeId, LocalDate date, Double percentage) {
    this.employeeId = employeeId;
    this.date = date;
    this.percentage = percentage;
  }
}
