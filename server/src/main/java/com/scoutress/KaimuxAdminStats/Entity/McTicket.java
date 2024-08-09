package com.scoutress.KaimuxAdminStats.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mc_tickets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Data
@NoArgsConstructor
public class McTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    public McTicket(Integer employeeId, LocalDate date, Integer ticketCount) {
        this.employeeId = employeeId;
        this.date = date;
        this.ticketCount = ticketCount;
    }
}
