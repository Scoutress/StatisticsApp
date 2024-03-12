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
    private Integer dailyTicketsSum;

    //Mboti212
    @Column(name = "mboti212_daily")
    private Integer mboti212Daily;

    @Column(name = "mboti212_ratio")
    private Double mboti212Ratio;

    @Column(name = "mboti212_ratio_percent")
    private Double mboti212RatioPercent;

    //Furija
    @Column(name = "furija_daily")
    private Integer furijaDaily;

    @Column(name = "furija_ratio")
    private Double furijaRatio;

    @Column(name = "furija_ratio_percent")
    private Double furijaRatioPercent;

    //Ernestasltu12
    @Column(name = "ernestasltu12_daily")
    private Integer ernestasltu12Daily;

    @Column(name = "ernestasltu12_ratio")
    private Double ernestasltu12Ratio;

    @Column(name = "ernestasltu12_ratio_percent")
    private Double ernestasltu12RatioPercent;

    //D0fka
    @Column(name = "d0fka_daily")
    private Integer d0fkaDaily;

    @Column(name = "d0fka_ratio")
    private Double d0fkaRatio;

    @Column(name = "d0fka_ratio_percent")
    private Double d0fkaRatioPercent;

    //MelitaLove
    @Column(name = "melitalove_daily")
    private Integer melitaLoveDaily;

    @Column(name = "melitalove_ratio")
    private Double melitaLoveRatio;

    @Column(name = "melitalove_ratio_percent")
    private Double melitaLoveRatioPercent;

    //Libete
    @Column(name = "libete_daily")
    private Integer libeteDaily;

    @Column(name = "libete_ratio")
    private Double libeteRatio;

    @Column(name = "libete_ratio_percent")
    private Double libeteRatioPercent;

    //Ariena
    @Column(name = "ariena_daily")
    private Integer arienaDaily;

    @Column(name = "ariena_ratio")
    private Double arienaRatio;

    @Column(name = "ariena_ratio_percent")
    private Double arienaRatioPercent;

    //Sharans
    @Column(name = "sharans_daily")
    private Integer sharansDaily;

    @Column(name = "sharans_ratio")
    private Double sharansRatio;

    @Column(name = "sharans_ratio_percent")
    private Double sharansRatioPercent;

    //labashey
    @Column(name = "labashey_daily")
    private Integer labasheyDaily;

    @Column(name = "labashey_ratio")
    private Double labasheyRatio;

    @Column(name = "labashey_ratio_percent")
    private Double labasheyRatioPercent;

    //everly
    @Column(name = "everly_daily")
    private Integer everlyDaily;

    @Column(name = "everly_ratio")
    private Double everlyRatio;

    @Column(name = "everly_ratio_percent")
    private Double everlyRatioPercent;

    //RichPica
    @Column(name = "richpica_daily")
    private Integer richPicaDaily;

    @Column(name = "richpica_ratio")
    private Double richPicaRatio;

    @Column(name = "richpica_ratio_percent")
    private Double richPicaRatioPercent;

    //Shizo
    @Column(name = "shizo_daily")
    private Integer shizoDaily;

    @Column(name = "shizo_ratio")
    private Double shizoRatio;

    @Column(name = "shizo_ratio_percent")
    private Double shizoRatioPercent;

    //Ievius
    @Column(name = "ievius_daily")
    private Integer ieviusDaily;

    @Column(name = "ievius_ratio")
    private Double ieviusRatio;

    @Column(name = "ievius_ratio_percent")
    private Double ieviusRatioPercent;

    //BobsBuilder
    @Column(name = "bobsbuilder_daily")
    private Integer bobsBuilderDaily;

    @Column(name = "bobsbuilder_ratio")
    private Double bobsBuilderRatio;

    @Column(name = "bobsbuilder_ratio_percent")
    private Double bobsBuilderRatioPercent;

    //plrxq
    @Column(name = "plrxq_daily")
    private Integer plrxqDaily;

    @Column(name = "plrxq_ratio")
    private Double plrxqRatio;

    @Column(name = "plrxq_ratio_percent")
    private Double plrxqRatioPercent;

    //Emsiukemiau
    @Column(name = "emsiukemiau_daily")
    private Integer emsiukemiauDaily;

    @Column(name = "emsiukemiau_ratio")
    private Double emsiukemiauRatio;

    @Column(name = "emsiukemiau_ratio_percent")
    private Double emsiukemiauRatioPercent;

    public McTicketsCalculations(){}

    public McTicketsCalculations(LocalDate date, Integer dailyTicketsSum, Integer mboti212Daily, Double mboti212Ratio,
            Double mboti212RatioPercent, Integer furijaDaily, Double furijaRatio, Double furijaRatioPercent,
            Integer ernestasltu12Daily, Double ernestasltu12Ratio, Double ernestasltu12RatioPercent, Integer d0fkaDaily,
            Double d0fkaRatio, Double d0fkaRatioPercent, Integer melitaLoveDaily, Double melitaLoveRatio,
            Double melitaLoveRatioPercent, Integer libeteDaily, Double libeteRatio, Double libeteRatioPercent,
            Integer arienaDaily, Double arienaRatio, Double arienaRatioPercent, Integer sharansDaily,
            Double sharansRatio, Double sharansRatioPercent, Integer labasheyDaily, Double labasheyRatio,
            Double labasheyRatioPercent, Integer everlyDaily, Double everlyRatio, Double everlyRatioPercent,
            Integer richPicaDaily, Double richPicaRatio, Double richPicaRatioPercent, Integer shizoDaily,
            Double shizoRatio, Double shizoRatioPercent, Integer ieviusDaily, Double ieviusRatio,
            Double ieviusRatioPercent, Integer bobsBuilderDaily, Double bobsBuilderRatio,
            Double bobsBuilderRatioPercent, Integer plrxqDaily, Double plrxqRatio, Double plrxqRatioPercent,
            Integer emsiukemiauDaily, Double emsiukemiauRatio, Double emsiukemiauRatioPercent) {
        this.date = date;
        this.dailyTicketsSum = dailyTicketsSum;
        this.mboti212Daily = mboti212Daily;
        this.mboti212Ratio = mboti212Ratio;
        this.mboti212RatioPercent = mboti212RatioPercent;
        this.furijaDaily = furijaDaily;
        this.furijaRatio = furijaRatio;
        this.furijaRatioPercent = furijaRatioPercent;
        this.ernestasltu12Daily = ernestasltu12Daily;
        this.ernestasltu12Ratio = ernestasltu12Ratio;
        this.ernestasltu12RatioPercent = ernestasltu12RatioPercent;
        this.d0fkaDaily = d0fkaDaily;
        this.d0fkaRatio = d0fkaRatio;
        this.d0fkaRatioPercent = d0fkaRatioPercent;
        this.melitaLoveDaily = melitaLoveDaily;
        this.melitaLoveRatio = melitaLoveRatio;
        this.melitaLoveRatioPercent = melitaLoveRatioPercent;
        this.libeteDaily = libeteDaily;
        this.libeteRatio = libeteRatio;
        this.libeteRatioPercent = libeteRatioPercent;
        this.arienaDaily = arienaDaily;
        this.arienaRatio = arienaRatio;
        this.arienaRatioPercent = arienaRatioPercent;
        this.sharansDaily = sharansDaily;
        this.sharansRatio = sharansRatio;
        this.sharansRatioPercent = sharansRatioPercent;
        this.labasheyDaily = labasheyDaily;
        this.labasheyRatio = labasheyRatio;
        this.labasheyRatioPercent = labasheyRatioPercent;
        this.everlyDaily = everlyDaily;
        this.everlyRatio = everlyRatio;
        this.everlyRatioPercent = everlyRatioPercent;
        this.richPicaDaily = richPicaDaily;
        this.richPicaRatio = richPicaRatio;
        this.richPicaRatioPercent = richPicaRatioPercent;
        this.shizoDaily = shizoDaily;
        this.shizoRatio = shizoRatio;
        this.shizoRatioPercent = shizoRatioPercent;
        this.ieviusDaily = ieviusDaily;
        this.ieviusRatio = ieviusRatio;
        this.ieviusRatioPercent = ieviusRatioPercent;
        this.bobsBuilderDaily = bobsBuilderDaily;
        this.bobsBuilderRatio = bobsBuilderRatio;
        this.bobsBuilderRatioPercent = bobsBuilderRatioPercent;
        this.plrxqDaily = plrxqDaily;
        this.plrxqRatio = plrxqRatio;
        this.plrxqRatioPercent = plrxqRatioPercent;
        this.emsiukemiauDaily = emsiukemiauDaily;
        this.emsiukemiauRatio = emsiukemiauRatio;
        this.emsiukemiauRatioPercent = emsiukemiauRatioPercent;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getDailyTicketsSum() {
        return dailyTicketsSum;
    }

    public void setDailyTicketsSum(Integer dailyTicketsSum) {
        this.dailyTicketsSum = dailyTicketsSum;
    }

    public Integer getMboti212Daily() {
        return mboti212Daily;
    }

    public void setMboti212Daily(Integer mboti212Daily) {
        this.mboti212Daily = mboti212Daily;
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

    public Integer getFurijaDaily() {
        return furijaDaily;
    }

    public void setFurijaDaily(Integer furijaDaily) {
        this.furijaDaily = furijaDaily;
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

    public Integer getErnestasltu12Daily() {
        return ernestasltu12Daily;
    }

    public void setErnestasltu12Daily(Integer ernestasltu12Daily) {
        this.ernestasltu12Daily = ernestasltu12Daily;
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

    public Integer getD0fkaDaily() {
        return d0fkaDaily;
    }

    public void setD0fkaDaily(Integer d0fkaDaily) {
        this.d0fkaDaily = d0fkaDaily;
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

    public Integer getMelitaLoveDaily() {
        return melitaLoveDaily;
    }

    public void setMelitaLoveDaily(Integer melitaLoveDaily) {
        this.melitaLoveDaily = melitaLoveDaily;
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

    public Integer getLibeteDaily() {
        return libeteDaily;
    }

    public void setLibeteDaily(Integer libeteDaily) {
        this.libeteDaily = libeteDaily;
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

    public Integer getArienaDaily() {
        return arienaDaily;
    }

    public void setArienaDaily(Integer arienaDaily) {
        this.arienaDaily = arienaDaily;
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

    public Integer getSharansDaily() {
        return sharansDaily;
    }

    public void setSharansDaily(Integer sharansDaily) {
        this.sharansDaily = sharansDaily;
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

    public Integer getLabasheyDaily() {
        return labasheyDaily;
    }

    public void setLabasheyDaily(Integer labasheyDaily) {
        this.labasheyDaily = labasheyDaily;
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

    public Integer getEverlyDaily() {
        return everlyDaily;
    }

    public void setEverlyDaily(Integer everlyDaily) {
        this.everlyDaily = everlyDaily;
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

    public Integer getRichPicaDaily() {
        return richPicaDaily;
    }

    public void setRichPicaDaily(Integer richPicaDaily) {
        this.richPicaDaily = richPicaDaily;
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

    public Integer getShizoDaily() {
        return shizoDaily;
    }

    public void setShizoDaily(Integer shizoDaily) {
        this.shizoDaily = shizoDaily;
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

    public Integer getIeviusDaily() {
        return ieviusDaily;
    }

    public void setIeviusDaily(Integer ieviusDaily) {
        this.ieviusDaily = ieviusDaily;
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

    public Integer getBobsBuilderDaily() {
        return bobsBuilderDaily;
    }

    public void setBobsBuilderDaily(Integer bobsBuilderDaily) {
        this.bobsBuilderDaily = bobsBuilderDaily;
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

    public Integer getPlrxqDaily() {
        return plrxqDaily;
    }

    public void setPlrxqDaily(Integer plrxqDaily) {
        this.plrxqDaily = plrxqDaily;
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

    public Integer getEmsiukemiauDaily() {
        return emsiukemiauDaily;
    }

    public void setEmsiukemiauDaily(Integer emsiukemiauDaily) {
        this.emsiukemiauDaily = emsiukemiauDaily;
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
