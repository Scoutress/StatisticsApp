package lt.scoutress.StatisticsApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "productivity_calc")
public class ProductivityCalc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "level")
    private String level;

    @Column(name = "is_enough_days_for_promotion")
    private Boolean isEnoughDaysForPromotion;

    @Column(name = "is_playtime_in_last_half_year_ok")
    private Boolean isPlaytimeInLastHalfYearOk;

    public ProductivityCalc(){}

    public ProductivityCalc(Integer id, String username, String level, Boolean isEnoughDaysForPromotion,
            Boolean isPlaytimeInLastHalfYearOk) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.isEnoughDaysForPromotion = isEnoughDaysForPromotion;
        this.isPlaytimeInLastHalfYearOk = isPlaytimeInLastHalfYearOk;
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

    public Boolean getIsEnoughDaysForPromotion() {
        return isEnoughDaysForPromotion;
    }

    public void setIsEnoughDaysForPromotion(Boolean isEnoughDaysForPromotion) {
        this.isEnoughDaysForPromotion = isEnoughDaysForPromotion;
    }

    public Boolean getIsPlaytimeInLastHalfYearOk() {
        return isPlaytimeInLastHalfYearOk;
    }

    public void setIsPlaytimeInLastHalfYearOk(Boolean isPlaytimeInLastHalfYearOk) {
        this.isPlaytimeInLastHalfYearOk = isPlaytimeInLastHalfYearOk;
    }
}
