package lt.scoutress.StatisticsApp.entity;

import jakarta.persistence.*;
import lt.scoutress.StatisticsApp.entity.Employees.Employee;

@Entity
@Table(name = "productivity")
public class Productivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "activity_per_half_year")
    private Double activityPerHalfYear;

    @Column(name = "playtime")
    private Double playtime;

    @Column(name = "playtime_afk")
    private Double playtimeAfk;

    @Column(name = "mc_tickets")
    private Double mcTickets;

    @Column(name = "mc_tickets_ratio")
    private Double mcTicketsRatio;

    @Column(name = "mc_tickets_per_playtime_hour")
    private Double mcTicketsPerPlaytimeHour;

    @Column(name = "dc_tickets")
    private Double dcTickets;

    @Column(name = "dc_tickets_ratio")
    private Double dcTicketsRatio;

    @Column(name = "productivity")
    private Double productivity;

    @Column(name = "recommendation")
    private Double recommendation;

    public Productivity() {
    }

    public Productivity(Employee employee, Double activityPerHalfYear, Double playtime, Double playtimeAfk,
            Double mcTickets, Double mcTicketsRatio, Double mcTicketsPerPlaytimeHour, Double dcTickets,
            Double dcTicketsRatio, Double productivity, Double recommendation) {
        this.employee = employee;
        this.activityPerHalfYear = activityPerHalfYear;
        this.playtime = playtime;
        this.playtimeAfk = playtimeAfk;
        this.mcTickets = mcTickets;
        this.mcTicketsRatio = mcTicketsRatio;
        this.mcTicketsPerPlaytimeHour = mcTicketsPerPlaytimeHour;
        this.dcTickets = dcTickets;
        this.dcTicketsRatio = dcTicketsRatio;
        this.productivity = productivity;
        this.recommendation = recommendation;
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

    public Double getActivityPerHalfYear() {
        return activityPerHalfYear;
    }

    public void setActivityPerHalfYear(Double activityPerHalfYear) {
        this.activityPerHalfYear = activityPerHalfYear;
    }

    public Double getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Double playtime) {
        this.playtime = playtime;
    }

    public Double getPlaytimeAfk() {
        return playtimeAfk;
    }

    public void setPlaytimeAfk(Double playtimeAfk) {
        this.playtimeAfk = playtimeAfk;
    }

    public Double getMcTickets() {
        return mcTickets;
    }

    public void setMcTickets(Double mcTickets) {
        this.mcTickets = mcTickets;
    }

    public Double getMcTicketsRatio() {
        return mcTicketsRatio;
    }

    public void setMcTicketsRatio(Double mcTicketsRatio) {
        this.mcTicketsRatio = mcTicketsRatio;
    }

    public Double getMcTicketsPerPlaytimeHour() {
        return mcTicketsPerPlaytimeHour;
    }

    public void setMcTicketsPerPlaytimeHour(Double mcTicketsPerPlaytimeHour) {
        this.mcTicketsPerPlaytimeHour = mcTicketsPerPlaytimeHour;
    }

    public Double getDcTickets() {
        return dcTickets;
    }

    public void setDcTickets(Double dcTickets) {
        this.dcTickets = dcTickets;
    }

    public Double getDcTicketsRatio() {
        return dcTicketsRatio;
    }

    public void setDcTicketsRatio(Double dcTicketsRatio) {
        this.dcTicketsRatio = dcTicketsRatio;
    }

    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }

    public Double getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Double recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "Productivity [id=" + id + ", employee=" + employee + ", activityPerHalfYear=" + activityPerHalfYear
                + ", playtime=" + playtime + ", playtimeAfk=" + playtimeAfk + ", mcTickets=" + mcTickets
                + ", mcTicketsRatio=" + mcTicketsRatio + ", mcTicketsPerPlaytimeHour=" + mcTicketsPerPlaytimeHour
                + ", dcTickets=" + dcTickets + ", dcTicketsRatio=" + dcTicketsRatio + ", productivity=" + productivity
                + ", recommendation=" + recommendation + "]";
    }
}
