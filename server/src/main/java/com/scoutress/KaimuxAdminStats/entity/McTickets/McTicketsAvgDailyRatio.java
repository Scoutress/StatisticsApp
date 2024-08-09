package com.scoutress.KaimuxAdminStats.Entity.McTickets;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class McTicketsAvgDailyRatio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "average_daily_ratio")
    private Double averageDailyRatio;

    public McTicketsAvgDailyRatio() {}

    public McTicketsAvgDailyRatio(Employee employee, Double averageDailyRatio) {
        this.employee = employee;
        this.averageDailyRatio = averageDailyRatio;
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

    public Double getAverageDailyRatio() {
        return averageDailyRatio;
    }

    public void setAverageDailyRatio(Double averageDailyRatio) {
        this.averageDailyRatio = averageDailyRatio;
    }

    @Override
    public String toString() {
        return "McTicketsAvgDailyRatio [id=" + id + ", employee=" + employee + ", averageDailyRatio="
                + averageDailyRatio + "]";
    }
}
