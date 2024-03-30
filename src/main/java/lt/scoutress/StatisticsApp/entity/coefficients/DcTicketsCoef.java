package lt.scoutress.StatisticsApp.entity.coefficients;

import jakarta.persistence.*;

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
