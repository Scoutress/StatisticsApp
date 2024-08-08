package com.scoutress.KaimuxAdminStats.CC;

import java.sql.Time;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_content")
public class CCContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "platform")
    private String platform;

    @Column(name = "type")
    private String type;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_creator_id")
    private ContentCreator contentCreator;

    @Column(name = "rec_duration")
    private Time recDuration;

    @Column(name = "views")
    private Integer views;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ContentCreator getContentCreator() {
        return contentCreator;
    }

    public void setContentCreator(ContentCreator contentCreator) {
        this.contentCreator = contentCreator;
    }

    public Time getRecDuration() {
        return recDuration;
    }

    public void setRecDuration(Time recDuration) {
        this.recDuration = recDuration;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public CCContent(Integer id, LocalDate date, String platform, String type, ContentCreator contentCreator,
            Time recDuration, Integer views) {
        this.id = id;
        this.date = date;
        this.platform = platform;
        this.type = type;
        this.contentCreator = contentCreator;
        this.recDuration = recDuration;
        this.views = views;
    }

    public CCContent(){}
}
