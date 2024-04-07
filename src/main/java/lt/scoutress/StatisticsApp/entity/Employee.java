package lt.scoutress.StatisticsApp.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "username")
    private String username;

    @Column(name = "level")
    private String level;

    @Column(name = "lang")
    private String language;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "days_since_join")
    private Integer daysSinceJoin;

    @Column(name = "survival_code")
    private Integer survivalCode;

    @Column(name = "skyblock_code")
    private Integer skyblockCode;

    @Column(name = "creative_code")
    private Integer creativeCode;

    @Column(name = "boxpvp_code")
    private Integer boxpvpCode;

    @Column(name = "prison_code")
    private Integer prisonCode;

    @Column(name = "events_code")
    private Integer eventsCode;

    @Column(name = "to_support")
    private LocalDate toSupport;

    @Column(name = "to_chatmod")
    private LocalDate toChatmod;

    @Column(name = "to_overseer")
    private LocalDate toOverseer;

    @Column(name = "to_manager")
    private LocalDate toManager;
    
    public Employee() {}

    public Employee(Integer employeeId, String username, String level, String language, String firstName,
            String lastName, String email, LocalDate joinDate, Integer daysSinceJoin, Integer survivalCode,
            Integer skyblockCode, Integer creativeCode, Integer boxpvpCode, Integer prisonCode, Integer eventsCode,
            LocalDate toSupport, LocalDate toChatmod, LocalDate toOverseer, LocalDate toManager) {
        this.employeeId = employeeId;
        this.username = username;
        this.level = level;
        this.language = language;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinDate = joinDate;
        this.daysSinceJoin = daysSinceJoin;
        this.survivalCode = survivalCode;
        this.skyblockCode = skyblockCode;
        this.creativeCode = creativeCode;
        this.boxpvpCode = boxpvpCode;
        this.prisonCode = prisonCode;
        this.eventsCode = eventsCode;
        this.toSupport = toSupport;
        this.toChatmod = toChatmod;
        this.toOverseer = toOverseer;
        this.toManager = toManager;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public Integer getDaysSinceJoin() {
        return daysSinceJoin;
    }

    public void setDaysSinceJoin(Integer daysSinceJoin) {
        this.daysSinceJoin = daysSinceJoin;
    }

    public Integer getSurvivalCode() {
        return survivalCode;
    }

    public void setSurvivalCode(Integer survivalCode) {
        this.survivalCode = survivalCode;
    }

    public Integer getSkyblockCode() {
        return skyblockCode;
    }

    public void setSkyblockCode(Integer skyblockCode) {
        this.skyblockCode = skyblockCode;
    }

    public Integer getCreativeCode() {
        return creativeCode;
    }

    public void setCreativeCode(Integer creativeCode) {
        this.creativeCode = creativeCode;
    }

    public Integer getBoxpvpCode() {
        return boxpvpCode;
    }

    public void setBoxpvpCode(Integer boxpvpCode) {
        this.boxpvpCode = boxpvpCode;
    }

    public Integer getPrisonCode() {
        return prisonCode;
    }

    public void setPrisonCode(Integer prisonCode) {
        this.prisonCode = prisonCode;
    }

    public Integer getEventsCode() {
        return eventsCode;
    }

    public void setEventsCode(Integer eventsCode) {
        this.eventsCode = eventsCode;
    }

    public LocalDate getToSupport() {
        return toSupport;
    }

    public void setToSupport(LocalDate toSupport) {
        this.toSupport = toSupport;
    }

    public LocalDate getToChatmod() {
        return toChatmod;
    }

    public void setToChatmod(LocalDate toChatmod) {
        this.toChatmod = toChatmod;
    }

    public LocalDate getToOverseer() {
        return toOverseer;
    }

    public void setToOverseer(LocalDate toOverseer) {
        this.toOverseer = toOverseer;
    }

    public LocalDate getToManager() {
        return toManager;
    }

    public void setToManager(LocalDate toManager) {
        this.toManager = toManager;
    }
}
