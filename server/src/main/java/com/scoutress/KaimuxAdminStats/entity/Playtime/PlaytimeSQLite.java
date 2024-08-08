package com.scoutress.KaimuxAdminStats.entity.Playtime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "playtime_sqlite")
public class PlaytimeSQLite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "time")
    private Integer time;

    @Column(name = "user")
    private String user;

    @Column(name = "action")
    private int action;

    public PlaytimeSQLite(){}

    public PlaytimeSQLite(Integer time, String user, int action) {
        this.time = time;
        this.user = user;
        this.action = action;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
    
}
