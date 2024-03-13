package lt.scoutress.StatisticsApp.entity.McTickets;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "mc_tickets_answered")
public class McTicketsAnswered {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "mboti212_mc_tickets")
    private Double mboti212McTickets;

    @Column(name = "furija_mc_tickets")
    private Double furijaMcTickets;

    @Column(name = "ernestasltu12_mc_tickets")
    private Double ernestasltu12McTickets;

    @Column(name = "d0fka_mc_tickets")
    private Double d0fkaMcTickets;

    @Column(name = "melitalove_mc_tickets")
    private Double melitaLoveMcTickets;

    @Column(name = "libete_mc_tickets")
    private Double libeteMcTickets;

    @Column(name = "ariena_mc_tickets")
    private Double arienaMcTickets;

    @Column(name = "sharans_mc_tickets")
    private Double sharansMcTickets;

    @Column(name = "labashey_mc_tickets")
    private Double labasheyMcTickets;

    @Column(name = "everly_mc_tickets")
    private Double everlyMcTickets;

    @Column(name = "richpica_mc_tickets")
    private Double richPicaMcTickets;

    @Column(name = "shizo_mc_tickets")
    private Double shizoMcTickets;

    @Column(name = "ievius_mc_tickets")
    private Double ieviusMcTickets;

    @Column(name = "bobsbuilder_mc_tickets")
    private Double bobsBuilderMcTickets;

    @Column(name = "plrxq_mc_tickets")
    private Double plrxqMcTickets;

    @Column(name = "emsiukemiau_mc_tickets")
    private Double emsiukemiauMcTickets;

    public McTicketsAnswered(LocalDate date, Double mboti212McTickets, Double furijaMcTickets,
            Double ernestasltu12McTickets, Double d0fkaMcTickets, Double melitaLoveMcTickets, Double libeteMcTickets,
            Double arienaMcTickets, Double sharansMcTickets, Double labasheyMcTickets, Double everlyMcTickets,
            Double richPicaMcTickets, Double shizoMcTickets, Double ieviusMcTickets, Double bobsBuilderMcTickets,
            Double plrxqMcTickets, Double emsiukemiauMcTickets) {
        this.date = date;
        this.mboti212McTickets = mboti212McTickets;
        this.furijaMcTickets = furijaMcTickets;
        this.ernestasltu12McTickets = ernestasltu12McTickets;
        this.d0fkaMcTickets = d0fkaMcTickets;
        this.melitaLoveMcTickets = melitaLoveMcTickets;
        this.libeteMcTickets = libeteMcTickets;
        this.arienaMcTickets = arienaMcTickets;
        this.sharansMcTickets = sharansMcTickets;
        this.labasheyMcTickets = labasheyMcTickets;
        this.everlyMcTickets = everlyMcTickets;
        this.richPicaMcTickets = richPicaMcTickets;
        this.shizoMcTickets = shizoMcTickets;
        this.ieviusMcTickets = ieviusMcTickets;
        this.bobsBuilderMcTickets = bobsBuilderMcTickets;
        this.plrxqMcTickets = plrxqMcTickets;
        this.emsiukemiauMcTickets = emsiukemiauMcTickets;
    }

    public McTicketsAnswered() {}

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getMboti212McTickets() {
        return mboti212McTickets;
    }

    public void setMboti212McTickets(Double mboti212McTickets) {
        this.mboti212McTickets = mboti212McTickets;
    }

    public Double getFurijaMcTickets() {
        return furijaMcTickets;
    }

    public void setFurijaMcTickets(Double furijaMcTickets) {
        this.furijaMcTickets = furijaMcTickets;
    }

    public Double getErnestasltu12McTickets() {
        return ernestasltu12McTickets;
    }

    public void setErnestasltu12McTickets(Double ernestasltu12McTickets) {
        this.ernestasltu12McTickets = ernestasltu12McTickets;
    }

    public Double getD0fkaMcTickets() {
        return d0fkaMcTickets;
    }

    public void setD0fkaMcTickets(Double d0fkaMcTickets) {
        this.d0fkaMcTickets = d0fkaMcTickets;
    }

    public Double getMelitaLoveMcTickets() {
        return melitaLoveMcTickets;
    }

    public void setMelitaLoveMcTickets(Double melitaLoveMcTickets) {
        this.melitaLoveMcTickets = melitaLoveMcTickets;
    }

    public Double getLibeteMcTickets() {
        return libeteMcTickets;
    }

    public void setLibeteMcTickets(Double libeteMcTickets) {
        this.libeteMcTickets = libeteMcTickets;
    }

    public Double getArienaMcTickets() {
        return arienaMcTickets;
    }

    public void setArienaMcTickets(Double arienaMcTickets) {
        this.arienaMcTickets = arienaMcTickets;
    }

    public Double getSharansMcTickets() {
        return sharansMcTickets;
    }

    public void setSharansMcTickets(Double sharansMcTickets) {
        this.sharansMcTickets = sharansMcTickets;
    }

    public Double getLabasheyMcTickets() {
        return labasheyMcTickets;
    }

    public void setLabasheyMcTickets(Double labasheyMcTickets) {
        this.labasheyMcTickets = labasheyMcTickets;
    }

    public Double getEverlyMcTickets() {
        return everlyMcTickets;
    }

    public void setEverlyMcTickets(Double everlyMcTickets) {
        this.everlyMcTickets = everlyMcTickets;
    }

    public Double getRichPicaMcTickets() {
        return richPicaMcTickets;
    }

    public void setRichPicaMcTickets(Double richPicaMcTickets) {
        this.richPicaMcTickets = richPicaMcTickets;
    }

    public Double getShizoMcTickets() {
        return shizoMcTickets;
    }

    public void setShizoMcTickets(Double shizoMcTickets) {
        this.shizoMcTickets = shizoMcTickets;
    }

    public Double getIeviusMcTickets() {
        return ieviusMcTickets;
    }

    public void setIeviusMcTickets(Double ieviusMcTickets) {
        this.ieviusMcTickets = ieviusMcTickets;
    }

    public Double getBobsBuilderMcTickets() {
        return bobsBuilderMcTickets;
    }

    public void setBobsBuilderMcTickets(Double bobsBuilderMcTickets) {
        this.bobsBuilderMcTickets = bobsBuilderMcTickets;
    }

    public Double getPlrxqMcTickets() {
        return plrxqMcTickets;
    }

    public void setPlrxqMcTickets(Double plrxqMcTickets) {
        this.plrxqMcTickets = plrxqMcTickets;
    }

    public Double getEmsiukemiauMcTickets() {
        return emsiukemiauMcTickets;
    }

    public void setEmsiukemiauMcTickets(Double emsiukemiauMcTickets) {
        this.emsiukemiauMcTickets = emsiukemiauMcTickets;
    }
    
}
