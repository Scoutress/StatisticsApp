package lt.scoutress.StatisticsApp.entity.McTickets;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lt.scoutress.StatisticsApp.entity.Employee;

@Entity
public class McTicketsAvgDaily {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "average_values")
    private Double averageValues;

    public McTicketsAvgDaily() {}

    public McTicketsAvgDaily(Employee employee, Double averageValues) {
        this.employee = employee;
        this.averageValues = averageValues;
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

    public Double getAverageValues() {
        return averageValues;
    }

    public void setAverageValues(Double averageValues) {
        this.averageValues = averageValues;
    }

    @Override
    public String toString() {
        return "McTicketsAvgDaily [id=" + id + ", averageValues=" + averageValues + "]";
    }    
}
