package com.scoutress.KaimuxAdminStats.entity.Playtime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "playtime_db_codes")
public class PlaytimeDBCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;
    
    @Column(name = "survival")
    private String survival;
    
    @Column(name = "skyblock")
    private String skyblock;
    
    @Column(name = "creative")
    private String creative;
    
    @Column(name = "boxpvp")
    private String boxpvp;
    
    @Column(name = "prison")
    private String prison;
    
    @Column(name = "events")
    private String events;

    public PlaytimeDBCodes(){}

    public PlaytimeDBCodes(Integer id, String username, String survival, String skyblock, String creative,
            String boxpvp, String prison, String events) {
        this.id = id;
        this.username = username;
        this.survival = survival;
        this.skyblock = skyblock;
        this.creative = creative;
        this.boxpvp = boxpvp;
        this.prison = prison;
        this.events = events;
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

    public String getSurvival() {
        return survival;
    }

    public void setSurvival(String survival) {
        this.survival = survival;
    }

    public String getSkyblock() {
        return skyblock;
    }

    public void setSkyblock(String skyblock) {
        this.skyblock = skyblock;
    }

    public String getCreative() {
        return creative;
    }

    public void setCreative(String creative) {
        this.creative = creative;
    }

    public String getBoxpvp() {
        return boxpvp;
    }

    public void setBoxpvp(String boxpvp) {
        this.boxpvp = boxpvp;
    }

    public String getPrison() {
        return prison;
    }

    public void setPrison(String prison) {
        this.prison = prison;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

}