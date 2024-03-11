package lt.scoutress.StatisticsApp.entity.McTickets;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "help_answered")
public class McTicketsAnswered {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "mc_tickets_sum")
    private Integer mcTicketsSum;

    @Column(name = "itsvaidas_mc_tickets")
    private Integer itsVaidasMcTickets;

    @Column(name = "scoutress_mc_tickets")
    private Integer scoutressMcTickets;

    @Column(name = "mboti212_mc_tickets")
    private Integer mboti212McTickets;

    @Column(name = "furija_mc_tickets")
    private Integer furijaMcTickets;

    @Column(name = "ernestasltu12_mc_tickets")
    private Integer ernestasltu12McTickets;

    @Column(name = "d0fka_mc_tickets")
    private Integer d0fkaMcTickets;

    @Column(name = "melita_mc_tickets")
    private Integer melitaLoveMcTickets;

    @Column(name = "libete_mc_tickets")
    private Integer libeteMcTickets;

    @Column(name = "ariena_mc_tickets")
    private Integer arienaMcTickets;

    @Column(name = "sharans_mc_tickets")
    private Integer sharansMcTickets;

    @Column(name = "labashey_mc_tickets")
    private Integer labasheyMcTickets;

    @Column(name = "everly_mc_tickets")
    private Integer everlyMcTickets;

    @Column(name = "richpica_mc_tickets")
    private Integer richPicaMcTickets;

    @Column(name = "shizo_mc_tickets")
    private Integer shizoMcTickets;

    @Column(name = "ievius_mc_tickets")
    private Integer ieviusMcTickets;

    @Column(name = "bobsbuilder_mc_tickets")
    private Integer bobsBuilderMcTickets;

    @Column(name = "plrxq_mc_tickets")
    private Integer plrxqMcTickets;

    @Column(name = "emsiukemiau_mc_tickets")
    private Integer emsiukemiauMcTickets;
    
    public McTicketsAnswered(LocalDate date, Integer mcTicketsSum, Integer itsVaidasMcTickets,
            Integer scoutressMcTickets, Integer mboti212McTickets, Integer furijaMcTickets,
            Integer ernestasltu12McTickets, Integer d0fkaMcTickets, Integer melitaLoveMcTickets,
            Integer libeteMcTickets, Integer arienaMcTickets, Integer sharansMcTickets, Integer labasheyMcTickets,
            Integer everlyMcTickets, Integer richPicaMcTickets, Integer shizoMcTickets, Integer ieviusMcTickets,
            Integer bobsBuilderMcTickets, Integer plrxqMcTickets, Integer emsiukemiauMcTickets) {
        this.date = date;
        this.mcTicketsSum = mcTicketsSum;
        this.itsVaidasMcTickets = itsVaidasMcTickets;
        this.scoutressMcTickets = scoutressMcTickets;
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

    public McTicketsAnswered(){}

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getMcTicketsSum() {
        return mcTicketsSum;
    }

    public void setMcTicketsSum(Integer mcTicketsSum) {
        this.mcTicketsSum = mcTicketsSum;
    }

    public Integer getItsVaidasMcTickets() {
        return itsVaidasMcTickets;
    }

    public void setItsVaidasMcTickets(Integer itsVaidasMcTickets) {
        this.itsVaidasMcTickets = itsVaidasMcTickets;
    }

    public Integer getScoutressMcTickets() {
        return scoutressMcTickets;
    }

    public void setScoutressMcTickets(Integer scoutressMcTickets) {
        this.scoutressMcTickets = scoutressMcTickets;
    }

    public Integer getMboti212McTickets() {
        return mboti212McTickets;
    }

    public void setMboti212McTickets(Integer mboti212McTickets) {
        this.mboti212McTickets = mboti212McTickets;
    }

    public Integer getFurijaMcTickets() {
        return furijaMcTickets;
    }

    public void setFurijaMcTickets(Integer furijaMcTickets) {
        this.furijaMcTickets = furijaMcTickets;
    }

    public Integer getErnestasltu12McTickets() {
        return ernestasltu12McTickets;
    }

    public void setErnestasltu12McTickets(Integer ernestasltu12McTickets) {
        this.ernestasltu12McTickets = ernestasltu12McTickets;
    }

    public Integer getD0fkaMcTickets() {
        return d0fkaMcTickets;
    }

    public void setD0fkaMcTickets(Integer d0fkaMcTickets) {
        this.d0fkaMcTickets = d0fkaMcTickets;
    }

    public Integer getMelitaLoveMcTickets() {
        return melitaLoveMcTickets;
    }

    public void setMelitaLoveMcTickets(Integer melitaLoveMcTickets) {
        this.melitaLoveMcTickets = melitaLoveMcTickets;
    }

    public Integer getLibeteMcTickets() {
        return libeteMcTickets;
    }

    public void setLibeteMcTickets(Integer libeteMcTickets) {
        this.libeteMcTickets = libeteMcTickets;
    }

    public Integer getArienaMcTickets() {
        return arienaMcTickets;
    }

    public void setArienaMcTickets(Integer arienaMcTickets) {
        this.arienaMcTickets = arienaMcTickets;
    }

    public Integer getSharansMcTickets() {
        return sharansMcTickets;
    }

    public void setSharansMcTickets(Integer sharansMcTickets) {
        this.sharansMcTickets = sharansMcTickets;
    }

    public Integer getLabasheyMcTickets() {
        return labasheyMcTickets;
    }

    public void setLabasheyMcTickets(Integer labasheyMcTickets) {
        this.labasheyMcTickets = labasheyMcTickets;
    }

    public Integer getEverlyMcTickets() {
        return everlyMcTickets;
    }

    public void setEverlyMcTickets(Integer everlyMcTickets) {
        this.everlyMcTickets = everlyMcTickets;
    }

    public Integer getRichPicaMcTickets() {
        return richPicaMcTickets;
    }

    public void setRichPicaMcTickets(Integer richPicaMcTickets) {
        this.richPicaMcTickets = richPicaMcTickets;
    }

    public Integer getShizoMcTickets() {
        return shizoMcTickets;
    }

    public void setShizoMcTickets(Integer shizoMcTickets) {
        this.shizoMcTickets = shizoMcTickets;
    }

    public Integer getIeviusMcTickets() {
        return ieviusMcTickets;
    }

    public void setIeviusMcTickets(Integer ieviusMcTickets) {
        this.ieviusMcTickets = ieviusMcTickets;
    }

    public Integer getBobsBuilderMcTickets() {
        return bobsBuilderMcTickets;
    }

    public void setBobsBuilderMcTickets(Integer bobsBuilderMcTickets) {
        this.bobsBuilderMcTickets = bobsBuilderMcTickets;
    }

    public Integer getPlrxqMcTickets() {
        return plrxqMcTickets;
    }

    public void setPlrxqMcTickets(Integer plrxqMcTickets) {
        this.plrxqMcTickets = plrxqMcTickets;
    }

    public Integer getEmsiukemiauMcTickets() {
        return emsiukemiauMcTickets;
    }

    public void setEmsiukemiauMcTickets(Integer emsiukemiauMcTickets) {
        this.emsiukemiauMcTickets = emsiukemiauMcTickets;
    }

}
