package com.scoutress.KaimuxAdminStats.entity.Coefficients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dc_tickets_coeffs")
public class DcTicketsCoef {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "support")
    private Double support;

    @Column(name = "chatmod")
    private Double chatmod;

    @Column(name = "overseer")
    private Double overseer;

    @Column(name = "organizer")
    private Double organizer;

    @Column(name = "manager")
    private Double manager;

    // Values by possible topics

    // Unassigned - 1 topic
    // Support    - 3 topics - can see 4 topics
    // ChatMod    - 1 topic  - can see 5 topics
    // Overseer   - 0 topic  - can see 5 topics
    // Organizer  - 1 topic  - can see 6 topics
    // Manager    - 3 topics - can see 9 topics

    public DcTicketsCoef(){}

    public DcTicketsCoef(Integer id, Double support, Double chatmod, Double overseer, Double organizer,
            Double manager) {
        this.id = id;
        this.support = support;
        this.chatmod = chatmod;
        this.overseer = overseer;
        this.organizer = organizer;
        this.manager = manager;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getSupport() {
        return support;
    }

    public void setSupport(Double support) {
        this.support = support;
    }

    public Double getChatmod() {
        return chatmod;
    }

    public void setChatmod(Double chatmod) {
        this.chatmod = chatmod;
    }

    public Double getOverseer() {
        return overseer;
    }

    public void setOverseer(Double overseer) {
        this.overseer = overseer;
    }

    public Double getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Double organizer) {
        this.organizer = organizer;
    }

    public Double getManager() {
        return manager;
    }

    public void setManager(Double manager) {
        this.manager = manager;
    }
}
