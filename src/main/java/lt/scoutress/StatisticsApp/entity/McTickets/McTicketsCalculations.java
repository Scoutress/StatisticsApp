package lt.scoutress.StatisticsApp.entity.McTickets;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "mc_tickets_calculations")
public class McTicketsCalculations {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "daily_tickets_sum")
    private Double dailyTicketsSum;

    //ItsVaidas
    @Column(name = "itsvaidas_ratio")
    private Double itsVaidasRatio;

    @Column(name = "itsvaidas_ratio_percent")
    private Double itsVaidasRatioPercent;

    //Scoutress
    @Column(name = "scoutress_ratio")
    private Double scoutressRatio;

    @Column(name = "scoutress_ratio_percent")
    private Double scoutressRatioPercent;

    //Mboti212
    @Column(name = "mboti212_ratio")
    private Double mboti212Ratio;

    @Column(name = "mboti212_ratio_percent")
    private Double mboti212RatioPercent;

    //Furija
    @Column(name = "furija_ratio")
    private Double furijaRatio;

    @Column(name = "furija_ratio_percent")
    private Double furijaRatioPercent;

    //Ernestasltu12
    @Column(name = "ernestasltu12_ratio")
    private Double ernestasltu12Ratio;

    @Column(name = "ernestasltu12_ratio_percent")
    private Double ernestasltu12RatioPercent;

    //D0fka
    @Column(name = "d0fka_ratio")
    private Double d0fkaRatio;

    @Column(name = "d0fka_ratio_percent")
    private Double d0fkaRatioPercent;

    //MelitaLove
    @Column(name = "melitalove_ratio")
    private Double melitaLoveRatio;

    @Column(name = "melitalove_ratio_percent")
    private Double melitaLoveRatioPercent;

    //Libete
    @Column(name = "libete_ratio")
    private Double libeteRatio;

    @Column(name = "libete_ratio_percent")
    private Double libeteRatioPercent;

    //Ariena
    @Column(name = "ariena_ratio")
    private Double arienaRatio;

    @Column(name = "ariena_ratio_percent")
    private Double arienaRatioPercent;

    //Sharans
    @Column(name = "sharans_ratio")
    private Double sharansRatio;

    @Column(name = "sharans_ratio_percent")
    private Double sharansRatioPercent;

    //labashey
    @Column(name = "labashey_ratio")
    private Double labasheyRatio;

    @Column(name = "labashey_ratio_percent")
    private Double labasheyRatioPercent;

    //everly
    @Column(name = "everly_ratio")
    private Double everlyRatio;

    @Column(name = "everly_ratio_percent")
    private Double everlyRatioPercent;

    //RichPica
    @Column(name = "richpica_ratio")
    private Double richPicaRatio;

    @Column(name = "richpica_ratio_percent")
    private Double richPicaRatioPercent;

    //Shizo
    @Column(name = "shizo_ratio")
    private Double shizoRatio;

    @Column(name = "shizo_ratio_percent")
    private Double shizoRatioPercent;

    //Ievius
    @Column(name = "ievius_ratio")
    private Double ieviusRatio;

    @Column(name = "ievius_ratio_percent")
    private Double ieviusRatioPercent;

    //BobsBuilder
    @Column(name = "bobsbuilder_ratio")
    private Double bobsBuilderRatio;

    @Column(name = "bobsbuilder_ratio_percent")
    private Double bobsBuilderRatioPercent;

    //plrxq
    @Column(name = "plrxq_ratio")
    private Double plrxqRatio;

    @Column(name = "plrxq_ratio_percent")
    private Double plrxqRatioPercent;

    //Emsiukemiau
    @Column(name = "emsiukemiau_ratio")
    private Double emsiukemiauRatio;

    @Column(name = "emsiukemiau_ratio_percent")
    private Double emsiukemiauRatioPercent;

    public McTicketsCalculations(){}

    public McTicketsCalculations(Integer id, LocalDate date, Double dailyTicketsSum, Double itsVaidasRatio,
            Double itsVaidasRatioPercent, Double scoutressRatio, Double scoutressRatioPercent, Double mboti212Ratio,
            Double mboti212RatioPercent, Double furijaRatio, Double furijaRatioPercent, Double ernestasltu12Ratio,
            Double ernestasltu12RatioPercent, Double d0fkaRatio, Double d0fkaRatioPercent, Double melitaLoveRatio,
            Double melitaLoveRatioPercent, Double libeteRatio, Double libeteRatioPercent, Double arienaRatio,
            Double arienaRatioPercent, Double sharansRatio, Double sharansRatioPercent, Double labasheyRatio,
            Double labasheyRatioPercent, Double everlyRatio, Double everlyRatioPercent, Double richPicaRatio,
            Double richPicaRatioPercent, Double shizoRatio, Double shizoRatioPercent, Double ieviusRatio,
            Double ieviusRatioPercent, Double bobsBuilderRatio, Double bobsBuilderRatioPercent, Double plrxqRatio,
            Double plrxqRatioPercent, Double emsiukemiauRatio, Double emsiukemiauRatioPercent) {
        this.id = id;
        this.date = date;
        this.dailyTicketsSum = dailyTicketsSum;
        this.itsVaidasRatio = itsVaidasRatio;
        this.itsVaidasRatioPercent = itsVaidasRatioPercent;
        this.scoutressRatio = scoutressRatio;
        this.scoutressRatioPercent = scoutressRatioPercent;
        this.mboti212Ratio = mboti212Ratio;
        this.mboti212RatioPercent = mboti212RatioPercent;
        this.furijaRatio = furijaRatio;
        this.furijaRatioPercent = furijaRatioPercent;
        this.ernestasltu12Ratio = ernestasltu12Ratio;
        this.ernestasltu12RatioPercent = ernestasltu12RatioPercent;
        this.d0fkaRatio = d0fkaRatio;
        this.d0fkaRatioPercent = d0fkaRatioPercent;
        this.melitaLoveRatio = melitaLoveRatio;
        this.melitaLoveRatioPercent = melitaLoveRatioPercent;
        this.libeteRatio = libeteRatio;
        this.libeteRatioPercent = libeteRatioPercent;
        this.arienaRatio = arienaRatio;
        this.arienaRatioPercent = arienaRatioPercent;
        this.sharansRatio = sharansRatio;
        this.sharansRatioPercent = sharansRatioPercent;
        this.labasheyRatio = labasheyRatio;
        this.labasheyRatioPercent = labasheyRatioPercent;
        this.everlyRatio = everlyRatio;
        this.everlyRatioPercent = everlyRatioPercent;
        this.richPicaRatio = richPicaRatio;
        this.richPicaRatioPercent = richPicaRatioPercent;
        this.shizoRatio = shizoRatio;
        this.shizoRatioPercent = shizoRatioPercent;
        this.ieviusRatio = ieviusRatio;
        this.ieviusRatioPercent = ieviusRatioPercent;
        this.bobsBuilderRatio = bobsBuilderRatio;
        this.bobsBuilderRatioPercent = bobsBuilderRatioPercent;
        this.plrxqRatio = plrxqRatio;
        this.plrxqRatioPercent = plrxqRatioPercent;
        this.emsiukemiauRatio = emsiukemiauRatio;
        this.emsiukemiauRatioPercent = emsiukemiauRatioPercent;
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

    public Double getDailyTicketsSum() {
        return dailyTicketsSum;
    }

    public void setDailyTicketsSum(Double dailyTicketsSum) {
        this.dailyTicketsSum = dailyTicketsSum;
    }

    public Double getItsVaidasRatio() {
        return itsVaidasRatio;
    }

    public void setItsVaidasRatio(Double itsVaidasRatio) {
        this.itsVaidasRatio = itsVaidasRatio;
    }

    public Double getItsVaidasRatioPercent() {
        return itsVaidasRatioPercent;
    }

    public void setItsVaidasRatioPercent(Double itsVaidasRatioPercent) {
        this.itsVaidasRatioPercent = itsVaidasRatioPercent;
    }

    public Double getScoutressRatio() {
        return scoutressRatio;
    }

    public void setScoutressRatio(Double scoutressRatio) {
        this.scoutressRatio = scoutressRatio;
    }

    public Double getScoutressRatioPercent() {
        return scoutressRatioPercent;
    }

    public void setScoutressRatioPercent(Double scoutressRatioPercent) {
        this.scoutressRatioPercent = scoutressRatioPercent;
    }

    public Double getMboti212Ratio() {
        return mboti212Ratio;
    }

    public void setMboti212Ratio(Double mboti212Ratio) {
        this.mboti212Ratio = mboti212Ratio;
    }

    public Double getMboti212RatioPercent() {
        return mboti212RatioPercent;
    }

    public void setMboti212RatioPercent(Double mboti212RatioPercent) {
        this.mboti212RatioPercent = mboti212RatioPercent;
    }

    public Double getFurijaRatio() {
        return furijaRatio;
    }

    public void setFurijaRatio(Double furijaRatio) {
        this.furijaRatio = furijaRatio;
    }

    public Double getFurijaRatioPercent() {
        return furijaRatioPercent;
    }

    public void setFurijaRatioPercent(Double furijaRatioPercent) {
        this.furijaRatioPercent = furijaRatioPercent;
    }

    public Double getErnestasltu12Ratio() {
        return ernestasltu12Ratio;
    }

    public void setErnestasltu12Ratio(Double ernestasltu12Ratio) {
        this.ernestasltu12Ratio = ernestasltu12Ratio;
    }

    public Double getErnestasltu12RatioPercent() {
        return ernestasltu12RatioPercent;
    }

    public void setErnestasltu12RatioPercent(Double ernestasltu12RatioPercent) {
        this.ernestasltu12RatioPercent = ernestasltu12RatioPercent;
    }

    public Double getD0fkaRatio() {
        return d0fkaRatio;
    }

    public void setD0fkaRatio(Double d0fkaRatio) {
        this.d0fkaRatio = d0fkaRatio;
    }

    public Double getD0fkaRatioPercent() {
        return d0fkaRatioPercent;
    }

    public void setD0fkaRatioPercent(Double d0fkaRatioPercent) {
        this.d0fkaRatioPercent = d0fkaRatioPercent;
    }

    public Double getMelitaLoveRatio() {
        return melitaLoveRatio;
    }

    public void setMelitaLoveRatio(Double melitaLoveRatio) {
        this.melitaLoveRatio = melitaLoveRatio;
    }

    public Double getMelitaLoveRatioPercent() {
        return melitaLoveRatioPercent;
    }

    public void setMelitaLoveRatioPercent(Double melitaLoveRatioPercent) {
        this.melitaLoveRatioPercent = melitaLoveRatioPercent;
    }

    public Double getLibeteRatio() {
        return libeteRatio;
    }

    public void setLibeteRatio(Double libeteRatio) {
        this.libeteRatio = libeteRatio;
    }

    public Double getLibeteRatioPercent() {
        return libeteRatioPercent;
    }

    public void setLibeteRatioPercent(Double libeteRatioPercent) {
        this.libeteRatioPercent = libeteRatioPercent;
    }

    public Double getArienaRatio() {
        return arienaRatio;
    }

    public void setArienaRatio(Double arienaRatio) {
        this.arienaRatio = arienaRatio;
    }

    public Double getArienaRatioPercent() {
        return arienaRatioPercent;
    }

    public void setArienaRatioPercent(Double arienaRatioPercent) {
        this.arienaRatioPercent = arienaRatioPercent;
    }

    public Double getSharansRatio() {
        return sharansRatio;
    }

    public void setSharansRatio(Double sharansRatio) {
        this.sharansRatio = sharansRatio;
    }

    public Double getSharansRatioPercent() {
        return sharansRatioPercent;
    }

    public void setSharansRatioPercent(Double sharansRatioPercent) {
        this.sharansRatioPercent = sharansRatioPercent;
    }

    public Double getLabasheyRatio() {
        return labasheyRatio;
    }

    public void setLabasheyRatio(Double labasheyRatio) {
        this.labasheyRatio = labasheyRatio;
    }

    public Double getLabasheyRatioPercent() {
        return labasheyRatioPercent;
    }

    public void setLabasheyRatioPercent(Double labasheyRatioPercent) {
        this.labasheyRatioPercent = labasheyRatioPercent;
    }

    public Double getEverlyRatio() {
        return everlyRatio;
    }

    public void setEverlyRatio(Double everlyRatio) {
        this.everlyRatio = everlyRatio;
    }

    public Double getEverlyRatioPercent() {
        return everlyRatioPercent;
    }

    public void setEverlyRatioPercent(Double everlyRatioPercent) {
        this.everlyRatioPercent = everlyRatioPercent;
    }

    public Double getRichPicaRatio() {
        return richPicaRatio;
    }

    public void setRichPicaRatio(Double richPicaRatio) {
        this.richPicaRatio = richPicaRatio;
    }

    public Double getRichPicaRatioPercent() {
        return richPicaRatioPercent;
    }

    public void setRichPicaRatioPercent(Double richPicaRatioPercent) {
        this.richPicaRatioPercent = richPicaRatioPercent;
    }

    public Double getShizoRatio() {
        return shizoRatio;
    }

    public void setShizoRatio(Double shizoRatio) {
        this.shizoRatio = shizoRatio;
    }

    public Double getShizoRatioPercent() {
        return shizoRatioPercent;
    }

    public void setShizoRatioPercent(Double shizoRatioPercent) {
        this.shizoRatioPercent = shizoRatioPercent;
    }

    public Double getIeviusRatio() {
        return ieviusRatio;
    }

    public void setIeviusRatio(Double ieviusRatio) {
        this.ieviusRatio = ieviusRatio;
    }

    public Double getIeviusRatioPercent() {
        return ieviusRatioPercent;
    }

    public void setIeviusRatioPercent(Double ieviusRatioPercent) {
        this.ieviusRatioPercent = ieviusRatioPercent;
    }

    public Double getBobsBuilderRatio() {
        return bobsBuilderRatio;
    }

    public void setBobsBuilderRatio(Double bobsBuilderRatio) {
        this.bobsBuilderRatio = bobsBuilderRatio;
    }

    public Double getBobsBuilderRatioPercent() {
        return bobsBuilderRatioPercent;
    }

    public void setBobsBuilderRatioPercent(Double bobsBuilderRatioPercent) {
        this.bobsBuilderRatioPercent = bobsBuilderRatioPercent;
    }

    public Double getPlrxqRatio() {
        return plrxqRatio;
    }

    public void setPlrxqRatio(Double plrxqRatio) {
        this.plrxqRatio = plrxqRatio;
    }

    public Double getPlrxqRatioPercent() {
        return plrxqRatioPercent;
    }

    public void setPlrxqRatioPercent(Double plrxqRatioPercent) {
        this.plrxqRatioPercent = plrxqRatioPercent;
    }

    public Double getEmsiukemiauRatio() {
        return emsiukemiauRatio;
    }

    public void setEmsiukemiauRatio(Double emsiukemiauRatio) {
        this.emsiukemiauRatio = emsiukemiauRatio;
    }

    public Double getEmsiukemiauRatioPercent() {
        return emsiukemiauRatioPercent;
    }

    public void setEmsiukemiauRatioPercent(Double emsiukemiauRatioPercent) {
        this.emsiukemiauRatioPercent = emsiukemiauRatioPercent;
    }

}