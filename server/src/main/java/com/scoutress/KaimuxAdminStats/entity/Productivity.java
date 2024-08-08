package com.scoutress.KaimuxAdminStats.entity;

import com.scoutress.KaimuxAdminStats.entity.Employees.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productivity")
@Data
@NoArgsConstructor
public class Productivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "annual_playtime")
    private Double annualPlaytime;

    @Column(name = "server_tickets")
    private Double serverTickets;

    @Column(name = "server_tickets_taking")
    private Double serverTicketsTaking;

    @Column(name = "discord_tickets")
    private Double discordTickets;

    @Column(name = "discord_tickets_taking")
    private Double discordTicketsTaking;

    @Column(name = "playtime")
    private Double playtime;

    @Column(name = "afk_playtime")
    private Double afkPlaytime;

    @Column(name = "productivity")
    private Double productivity;

    @Column(name = "recommendation")
    private String recommendation;    
}