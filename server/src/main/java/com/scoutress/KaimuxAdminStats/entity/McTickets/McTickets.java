package com.scoutress.KaimuxAdminStats.Entity.McTickets;

import java.time.LocalDate;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class McTickets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "mc_tickets_count")
    private Integer mcTicketsCount;

    public McTickets() {}

    public McTickets(Employee employee, LocalDate date, Integer mcTicketsCount) {
        this.employee = employee;
        this.date = date;
        this.mcTicketsCount = mcTicketsCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getMcTicketsCount() {
        return mcTicketsCount;
    }

    public void setMcTicketsCount(Integer mcTicketsCount) {
        this.mcTicketsCount = mcTicketsCount;
    }

    @Override
    public String toString() {
        return "McTickets [id=" + id + ", date=" + date + ", mcTicketsCount=" + mcTicketsCount + "]";
    }
}
