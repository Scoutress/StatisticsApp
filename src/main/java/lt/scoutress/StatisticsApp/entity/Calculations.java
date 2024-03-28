package lt.scoutress.StatisticsApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scoutress_productivity")
public class Calculations {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "days_since_join")
    private Integer daysSinceJoin;

    public Calculations(Integer daysSinceJoin) {
        this.daysSinceJoin = daysSinceJoin;
    }

    public Calculations(){}

    public Integer getDaysSinceJoin() {
        return daysSinceJoin;
    }

    public void setDaysSinceJoin(Integer daysSinceJoin) {
        this.daysSinceJoin = daysSinceJoin;
    }
}
