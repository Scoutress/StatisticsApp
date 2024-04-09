package lt.scoutress.StatisticsApp.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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
    
    @OneToMany(mappedBy = "employee", 
               cascade = {CascadeType.PERSIST, CascadeType.MERGE, 
                          CascadeType.DETACH, CascadeType.REFRESH})
    private List<McTickets> mcTickets;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private McTicketsAvgDaily mcTicketsAvgDaily;

    public Employee() {}

    public Employee(String username, String level, String language, String firstName, String lastName, String email,
            LocalDate joinDate, Integer daysSinceJoin) {
        this.username = username;
        this.level = level;
        this.language = language;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinDate = joinDate;
        this.daysSinceJoin = daysSinceJoin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<McTickets> getMcTickets() {
        return mcTickets;
    }

    public void setMcTickets(List<McTickets> mcTickets) {
        this.mcTickets = mcTickets;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", username=" + username + ", level=" + level + ", language=" + language
                + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", joinDate=" + joinDate
                + ", daysSinceJoin=" + daysSinceJoin + "]";
    }

    public void add(McTickets tempMcTickets){

        if(mcTickets == null){
            mcTickets = new ArrayList<>();
        }
        mcTickets.add(tempMcTickets);

        tempMcTickets.setEmployee(this);
    }
}
