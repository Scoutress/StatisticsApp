package lt.scoutress.StatisticsApp.entity.McTickets;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "mc_tickets_count")
public class McTicketsCounting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "itsvaidas")
    private Double itsvaidas;

    @Column(name = "scoutress")
    private Double scoutress;

    @Column(name = "mboti212")
    private Double mboti212;

    @Column(name = "furija")
    private Double furija;

    @Column(name = "ernestasltu12")
    private Double ernestasltu12;

    @Column(name = "d0fka")
    private Double d0fka;

    @Column(name = "melitalove")
    private Double melitalove;

    @Column(name = "libete")
    private Double libete;

    @Column(name = "ariena")
    private Double ariena;

    @Column(name = "sharans")
    private Double sharans;

    @Column(name = "labashey")
    private Double labashey;

    @Column(name = "everly")
    private Double everly;

    @Column(name = "richpica")
    private Double richpica;

    @Column(name = "shizo")
    private Double shizo;

    @Column(name = "ievius")
    private Double ievius;

    @Column(name = "bobsbuilder")
    private Double bobsbuilder;

    @Column(name = "plrxq")
    private Double plrxq;

    @Column(name = "emsiukemiau")
    private Double emsiukemiau;

    public McTicketsCounting(){}

    public McTicketsCounting(Integer id, LocalDate date, Double itsvaidas, Double scoutress, Double mboti212,
            Double furija, Double ernestasltu12, Double d0fka, Double melitalove, Double libete, Double ariena,
            Double sharans, Double labashey, Double everly, Double richpica, Double shizo, Double ievius,
            Double bobsbuilder, Double plrxq, Double emsiukemiau) {
        this.id = id;
        this.date = date;
        this.itsvaidas = itsvaidas;
        this.scoutress = scoutress;
        this.mboti212 = mboti212;
        this.furija = furija;
        this.ernestasltu12 = ernestasltu12;
        this.d0fka = d0fka;
        this.melitalove = melitalove;
        this.libete = libete;
        this.ariena = ariena;
        this.sharans = sharans;
        this.labashey = labashey;
        this.everly = everly;
        this.richpica = richpica;
        this.shizo = shizo;
        this.ievius = ievius;
        this.bobsbuilder = bobsbuilder;
        this.plrxq = plrxq;
        this.emsiukemiau = emsiukemiau;
    }

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

    public Double getItsvaidas() {
        return itsvaidas;
    }

    public void setItsvaidas(Double itsvaidas) {
        this.itsvaidas = itsvaidas;
    }

    public Double getScoutress() {
        return scoutress;
    }

    public void setScoutress(Double scoutress) {
        this.scoutress = scoutress;
    }

    public Double getMboti212() {
        return mboti212;
    }

    public void setMboti212(Double mboti212) {
        this.mboti212 = mboti212;
    }

    public Double getFurija() {
        return furija;
    }

    public void setFurija(Double furija) {
        this.furija = furija;
    }

    public Double getErnestasltu12() {
        return ernestasltu12;
    }

    public void setErnestasltu12(Double ernestasltu12) {
        this.ernestasltu12 = ernestasltu12;
    }

    public Double getD0fka() {
        return d0fka;
    }

    public void setD0fka(Double d0fka) {
        this.d0fka = d0fka;
    }

    public Double getMelitalove() {
        return melitalove;
    }

    public void setMelitalove(Double melitalove) {
        this.melitalove = melitalove;
    }

    public Double getLibete() {
        return libete;
    }

    public void setLibete(Double libete) {
        this.libete = libete;
    }

    public Double getAriena() {
        return ariena;
    }

    public void setAriena(Double ariena) {
        this.ariena = ariena;
    }

    public Double getSharans() {
        return sharans;
    }

    public void setSharans(Double sharans) {
        this.sharans = sharans;
    }

    public Double getLabashey() {
        return labashey;
    }

    public void setLabashey(Double labashey) {
        this.labashey = labashey;
    }

    public Double getEverly() {
        return everly;
    }

    public void setEverly(Double everly) {
        this.everly = everly;
    }

    public Double getRichpica() {
        return richpica;
    }

    public void setRichpica(Double richpica) {
        this.richpica = richpica;
    }

    public Double getShizo() {
        return shizo;
    }

    public void setShizo(Double shizo) {
        this.shizo = shizo;
    }

    public Double getIevius() {
        return ievius;
    }

    public void setIevius(Double ievius) {
        this.ievius = ievius;
    }

    public Double getBobsbuilder() {
        return bobsbuilder;
    }

    public void setBobsbuilder(Double bobsbuilder) {
        this.bobsbuilder = bobsbuilder;
    }

    public Double getPlrxq() {
        return plrxq;
    }

    public void setPlrxq(Double plrxq) {
        this.plrxq = plrxq;
    }

    public Double getEmsiukemiau() {
        return emsiukemiau;
    }

    public void setEmsiukemiau(Double emsiukemiau) {
        this.emsiukemiau = emsiukemiau;
    }
}
