package com.scoutress.KaimuxAdminStats.Entity.old;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productivity_calc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductivityCalc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    @Column(name = "server_tickets_calc", nullable = true)
    private Double serverTicketsCalc;

    @Column(name = "server_tickets_taking_calc", nullable = true)
    private Double serverTicketsTakingCalc;

    @Column(name = "discord_tickets_calc", nullable = true)
    private Double discordTicketsCalc;

    @Column(name = "playtime_calc", nullable = true)
    private Double playtimeCalc;

    @Column(name = "afk_playtime_calc", nullable = true)
    private Double afkPlaytimeCalc;

    @Column(name = "complains_calc", nullable = true)
    private Double complainsCalc;

    public ProductivityCalc(Employee employee) {
        this.employee = employee;
    }
}
