package lt.scoutress.StatisticsApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "productivity")
public class Productivity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "level")
    private String level;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "activity_per_half_year")
    private Double activityPerHalfYear;

    @Column(name = "playtime")
    private Double playtime;

    @Column(name = "playtime_afk")
    private Double playtimeAfk;

    @Column(name = "mc_tickets")
    private Double mcTickets;

    @Column(name = "mc_tickets_comp")
    private Double mcTicketsComp;

    @Column(name = "dc_messages")
    private Double dcMessages;

    @Column(name = "dc_messages_comp")
    private Double dcMessagesComp;

    @Column(name = "dc_tickets")
    private Double dcTickets;

    @Column(name = "dc_tickets_comp")
    private Double dcTicketsComp;

    @Column(name = "productivity")
    private Double productivity;

    @Column(name = "recommendation")
    private Double recommendation;

    public Productivity(){}

    public Productivity(Integer id, String level, String username, Double activityPerHalfYear, Double playtime,
            Double playtimeAfk, Double mcTickets, Double mcTicketsComp, Double dcMessages, Double dcMessagesComp,
            Double dcTickets, Double dcTicketsComp, Double productivity, Double recommendation) {
        this.id = id;
        this.level = level;
        this.username = username;
        this.activityPerHalfYear = activityPerHalfYear;
        this.playtime = playtime;
        this.playtimeAfk = playtimeAfk;
        this.mcTickets = mcTickets;
        this.mcTicketsComp = mcTicketsComp;
        this.dcMessages = dcMessages;
        this.dcMessagesComp = dcMessagesComp;
        this.dcTickets = dcTickets;
        this.dcTicketsComp = dcTicketsComp;
        this.productivity = productivity;
        this.recommendation = recommendation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Double getMcTicketsComp() {
        return mcTicketsComp;
    }

    public void setMcTicketsComp(Double mcTicketsComp) {
        this.mcTicketsComp = mcTicketsComp;
    }

    public Double getDcMessages() {
        return dcMessages;
    }

    public void setDcMessages(Double dcMessages) {
        this.dcMessages = dcMessages;
    }

    public Double getDcMessagesComp() {
        return dcMessagesComp;
    }

    public void setDcMessagesComp(Double dcMessagesComp) {
        this.dcMessagesComp = dcMessagesComp;
    }

    public Double getDcTickets() {
        return dcTickets;
    }

    public void setDcTickets(Double dcTickets) {
        this.dcTickets = dcTickets;
    }

    public Double getDcTicketsComp() {
        return dcTicketsComp;
    }

    public void setDcTicketsComp(Double dcTicketsComp) {
        this.dcTicketsComp = dcTicketsComp;
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
}
