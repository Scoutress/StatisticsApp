package lt.scoutress.StatisticsApp.entity.playtime;

import jakarta.persistence.*;

@Entity
@Table(name = "playtime")
public class Playtime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "number_of_week")
    private Integer numberOfWeek;

    @Column(name = "mboti212_playtime")
    private Integer mboti212Playtime;

    @Column(name = "furija_playtime")
    private Integer furijaPlaytime;

    @Column(name = "ernestasltu12_playtime")
    private Integer ernestasltu12Playtime;

    @Column(name = "d0fka_playtime")
    private Integer d0fkaPlaytime;

    @Column(name = "melitalove_playtime")
    private Integer melitaLovePlaytime;

    @Column(name = "libete_playtime")
    private Integer libetePlaytime;

    @Column(name = "ariena_playtime")
    private Integer arienaPlaytime;

    @Column(name = "sharans_playtime")
    private Integer sharansPlaytime;

    @Column(name = "labashey_playtime")
    private Integer labasheyPlaytime;

    @Column(name = "everly_playtime")
    private Integer everlyPlaytime;

    @Column(name = "richpica_playtime")
    private Integer richPicaPlaytime;

    @Column(name = "shizo_playtime")
    private Integer shizoPlaytime;

    @Column(name = "ievius_playtime")
    private Integer ieviusPlaytime;

    @Column(name = "bobsbuilder_playtime")
    private Integer bobsBuilderPlaytime;

    @Column(name = "plrxq_playtime")
    private Integer plrxqPlaytime;

    @Column(name = "emsiukemiau_playtime")
    private Integer emsiukemiauPlaytime;

    public Playtime(){}

    public Playtime(Integer year, Integer numberOfWeek, Integer mboti212Playtime, Integer furijaPlaytime,
            Integer ernestasltu12Playtime, Integer d0fkaPlaytime, Integer melitaLovePlaytime, Integer libetePlaytime,
            Integer arienaPlaytime, Integer sharansPlaytime, Integer labasheyPlaytime, Integer everlyPlaytime,
            Integer richPicaPlaytime, Integer shizoPlaytime, Integer ieviusPlaytime, Integer bobsBuilderPlaytime,
            Integer plrxqPlaytime, Integer emsiukemiauPlaytime) {
        this.year = year;
        this.numberOfWeek = numberOfWeek;
        this.mboti212Playtime = mboti212Playtime;
        this.furijaPlaytime = furijaPlaytime;
        this.ernestasltu12Playtime = ernestasltu12Playtime;
        this.d0fkaPlaytime = d0fkaPlaytime;
        this.melitaLovePlaytime = melitaLovePlaytime;
        this.libetePlaytime = libetePlaytime;
        this.arienaPlaytime = arienaPlaytime;
        this.sharansPlaytime = sharansPlaytime;
        this.labasheyPlaytime = labasheyPlaytime;
        this.everlyPlaytime = everlyPlaytime;
        this.richPicaPlaytime = richPicaPlaytime;
        this.shizoPlaytime = shizoPlaytime;
        this.ieviusPlaytime = ieviusPlaytime;
        this.bobsBuilderPlaytime = bobsBuilderPlaytime;
        this.plrxqPlaytime = plrxqPlaytime;
        this.emsiukemiauPlaytime = emsiukemiauPlaytime;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getNumberOfWeek() {
        return numberOfWeek;
    }

    public void setNumberOfWeek(Integer numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public Integer getMboti212Playtime() {
        return mboti212Playtime;
    }

    public void setMboti212Playtime(Integer mboti212Playtime) {
        this.mboti212Playtime = mboti212Playtime;
    }

    public Integer getFurijaPlaytime() {
        return furijaPlaytime;
    }

    public void setFurijaPlaytime(Integer furijaPlaytime) {
        this.furijaPlaytime = furijaPlaytime;
    }

    public Integer getErnestasltu12Playtime() {
        return ernestasltu12Playtime;
    }

    public void setErnestasltu12Playtime(Integer ernestasltu12Playtime) {
        this.ernestasltu12Playtime = ernestasltu12Playtime;
    }

    public Integer getD0fkaPlaytime() {
        return d0fkaPlaytime;
    }

    public void setD0fkaPlaytime(Integer d0fkaPlaytime) {
        this.d0fkaPlaytime = d0fkaPlaytime;
    }

    public Integer getMelitaLovePlaytime() {
        return melitaLovePlaytime;
    }

    public void setMelitaLovePlaytime(Integer melitaLovePlaytime) {
        this.melitaLovePlaytime = melitaLovePlaytime;
    }

    public Integer getLibetePlaytime() {
        return libetePlaytime;
    }

    public void setLibetePlaytime(Integer libetePlaytime) {
        this.libetePlaytime = libetePlaytime;
    }

    public Integer getArienaPlaytime() {
        return arienaPlaytime;
    }

    public void setArienaPlaytime(Integer arienaPlaytime) {
        this.arienaPlaytime = arienaPlaytime;
    }

    public Integer getSharansPlaytime() {
        return sharansPlaytime;
    }

    public void setSharansPlaytime(Integer sharansPlaytime) {
        this.sharansPlaytime = sharansPlaytime;
    }

    public Integer getLabasheyPlaytime() {
        return labasheyPlaytime;
    }

    public void setLabasheyPlaytime(Integer labasheyPlaytime) {
        this.labasheyPlaytime = labasheyPlaytime;
    }

    public Integer getEverlyPlaytime() {
        return everlyPlaytime;
    }

    public void setEverlyPlaytime(Integer everlyPlaytime) {
        this.everlyPlaytime = everlyPlaytime;
    }

    public Integer getRichPicaPlaytime() {
        return richPicaPlaytime;
    }

    public void setRichPicaPlaytime(Integer richPicaPlaytime) {
        this.richPicaPlaytime = richPicaPlaytime;
    }

    public Integer getShizoPlaytime() {
        return shizoPlaytime;
    }

    public void setShizoPlaytime(Integer shizoPlaytime) {
        this.shizoPlaytime = shizoPlaytime;
    }

    public Integer getIeviusPlaytime() {
        return ieviusPlaytime;
    }

    public void setIeviusPlaytime(Integer ieviusPlaytime) {
        this.ieviusPlaytime = ieviusPlaytime;
    }

    public Integer getBobsBuilderPlaytime() {
        return bobsBuilderPlaytime;
    }

    public void setBobsBuilderPlaytime(Integer bobsBuilderPlaytime) {
        this.bobsBuilderPlaytime = bobsBuilderPlaytime;
    }

    public Integer getPlrxqPlaytime() {
        return plrxqPlaytime;
    }

    public void setPlrxqPlaytime(Integer plrxqPlaytime) {
        this.plrxqPlaytime = plrxqPlaytime;
    }

    public Integer getEmsiukemiauPlaytime() {
        return emsiukemiauPlaytime;
    }

    public void setEmsiukemiauPlaytime(Integer emsiukemiauPlaytime) {
        this.emsiukemiauPlaytime = emsiukemiauPlaytime;
    }

}
