package com.scoutress.KaimuxAdminStats.Entity;

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
@Table(name = "productivity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Productivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

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

    public Productivity(Employee employee) {
        this.employee = employee;
    }

    public Productivity(Employee employee, Double annualPlaytime, Double serverTickets, Double serverTicketsTaking, Double discordTickets, Double discordTicketsTaking, Double playtime, Double afkPlaytime, Double productivity, String recommendation) {
        this.employee = employee;
        this.annualPlaytime = annualPlaytime;
        this.serverTickets = serverTickets;
        this.serverTicketsTaking = serverTicketsTaking;
        this.discordTickets = discordTickets;
        this.discordTicketsTaking = discordTicketsTaking;
        this.playtime = playtime;
        this.afkPlaytime = afkPlaytime;
        this.productivity = productivity;
        this.recommendation = recommendation;
    }

    public Productivity(Employee employee, Double annualPlaytime) {
        this.employee = employee;
        this.annualPlaytime = annualPlaytime;
    }
}