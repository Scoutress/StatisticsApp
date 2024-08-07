package lt.scoutress.StatisticsApp.entity.Playtime.servers;

import jakarta.persistence.*;

@Entity
@Table(name = "co_session_creative")
public class Creative {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "user")
    private Integer user;

    @Column(name = "time")
    private Integer time;

    @Column(name = "action")
    private Integer action;

    public Creative(){}

    public Creative(Integer id, Integer user, Integer time, Integer action) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.action = action;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
