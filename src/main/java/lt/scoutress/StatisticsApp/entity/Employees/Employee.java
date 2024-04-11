package lt.scoutress.StatisticsApp.entity.Employees;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.*;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "level")
    private String level;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<McTickets> mcTickets;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private McTicketsAvgDaily mcTicketsAvgDaily;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private McTicketsAvgDailyRatio mcTicketsAvgDailyRatio;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EmployeeCodes> employeeCodes = new ArrayList<>();

    public Employee() {}

    public Employee(String username, String level, String language, String firstName, String lastName, String email,
            LocalDate joinDate) {
        this.username = username;
        this.level = level;
        this.language = language;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinDate = joinDate;
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

    public List<McTickets> getMcTickets() {
        return mcTickets;
    }

    public void setMcTickets(List<McTickets> mcTickets) {
        this.mcTickets = mcTickets;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", username=" + username + ", level=" + level + ", language=" + language
                + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", joinDate=" + joinDate + "]";
    }

    public void add(McTickets tempMcTickets){

        if(mcTickets == null){
            mcTickets = new ArrayList<>();
        }
        mcTickets.add(tempMcTickets);

        tempMcTickets.setEmployee(this);
    }

    public void addEmployeeCodes(EmployeeCodes tempEmployeeCodes){
        if(employeeCodes == null){
            employeeCodes = new ArrayList<>();
        }
        employeeCodes.add(tempEmployeeCodes);
        tempEmployeeCodes.setEmployee(this);
    }

    public EmployeeCodes getCodeByServerName(String serverName) {
        for (EmployeeCodes code : employeeCodes) {
            if (code.getServerName().equals(serverName)) {
                return code;
            }
        }
        return null;
    }

    @PostPersist
    public void addDefaultEmployeeCodes() {
        List<String> serverNames = Arrays.asList("Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events");
        for (String serverName : serverNames) {
            EmployeeCodes employeeCodes = new EmployeeCodes(this.getId(), serverName, null);
            this.employeeCodes.add(employeeCodes);
        }
    }

    public List<EmployeeCodes> getEmployeeCodes() {
        return employeeCodes;
    }

    public void setEmployeeCodes(List<EmployeeCodes> employeeCodes) {
        this.employeeCodes = employeeCodes;
    }
}
