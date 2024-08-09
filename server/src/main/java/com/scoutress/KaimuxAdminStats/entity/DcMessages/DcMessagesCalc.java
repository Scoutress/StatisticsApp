package com.scoutress.KaimuxAdminStats.Entity.DcMessages;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dc_messages_calc")
public class DcMessagesCalc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "daily_msg_sum")
    private Double dailyMsgSum;

    @Column(name = "mboti212_dc_msg_calc")
    private Double mboti212DcMsgCalc;

    @Column(name = "furija_dc_msg_calc")
    private Double furijaDcMsgCalc;

    @Column(name = "ernestasltu12_dc_msg_calc")
    private Double ernestasltu12DcMsgCalc;

    @Column(name = "d0fka_dc_msg_calc")
    private Double d0fkaDcMsgCalc;

    @Column(name = "melitalove_dc_msg_calc")
    private Double melitaLoveDcMsgCalc;

    @Column(name = "libete_dc_msg_calc")
    private Double libeteDcMsgCalc;

    @Column(name = "ariena_dc_msg_calc")
    private Double arienaDcMsgCalc;

    @Column(name = "sharans_dc_msg_calc")
    private Double sharansDcMsgCalc;

    @Column(name = "labashey_dc_msg_calc")
    private Double labasheyDcMsgCalc;

    @Column(name = "everly_dc_msg_calc")
    private Double everlyDcMsgCalc;

    @Column(name = "richpica_dc_msg_calc")
    private Double richPicaDcMsgCalc;

    @Column(name = "shizo_dc_msg_calc")
    private Double shizoDcMsgCalc;

    @Column(name = "ievius_dc_msg_calc")
    private Double ieviusDcMsgCalc;

    @Column(name = "bobsbuilder_dc_msg_calc")
    private Double bobsBuilderDcMsgCalc;

    @Column(name = "plrxq_dc_msg_calc")
    private Double plrxqDcMsgCalc;

    @Column(name = "emsiukemiau_dc_msg_calc")
    private Double emsiukemiauDcMsgCalc;

    public DcMessagesCalc(){}

    public DcMessagesCalc(LocalDate date, Double dailyMsgSum, Double mboti212DcMsgCalc, Double furijaDcMsgCalc,
            Double ernestasltu12DcMsgCalc, Double d0fkaDcMsgCalc, Double melitaLoveDcMsgCalc, Double libeteDcMsgCalc,
            Double arienaDcMsgCalc, Double sharansDcMsgCalc, Double labasheyDcMsgCalc, Double everlyDcMsgCalc,
            Double richPicaDcMsgCalc, Double shizoDcMsgCalc, Double ieviusDcMsgCalc, Double bobsBuilderDcMsgCalc,
            Double plrxqDcMsgCalc, Double emsiukemiauDcMsgCalc) {
        this.date = date;
        this.dailyMsgSum = dailyMsgSum;
        this.mboti212DcMsgCalc = mboti212DcMsgCalc;
        this.furijaDcMsgCalc = furijaDcMsgCalc;
        this.ernestasltu12DcMsgCalc = ernestasltu12DcMsgCalc;
        this.d0fkaDcMsgCalc = d0fkaDcMsgCalc;
        this.melitaLoveDcMsgCalc = melitaLoveDcMsgCalc;
        this.libeteDcMsgCalc = libeteDcMsgCalc;
        this.arienaDcMsgCalc = arienaDcMsgCalc;
        this.sharansDcMsgCalc = sharansDcMsgCalc;
        this.labasheyDcMsgCalc = labasheyDcMsgCalc;
        this.everlyDcMsgCalc = everlyDcMsgCalc;
        this.richPicaDcMsgCalc = richPicaDcMsgCalc;
        this.shizoDcMsgCalc = shizoDcMsgCalc;
        this.ieviusDcMsgCalc = ieviusDcMsgCalc;
        this.bobsBuilderDcMsgCalc = bobsBuilderDcMsgCalc;
        this.plrxqDcMsgCalc = plrxqDcMsgCalc;
        this.emsiukemiauDcMsgCalc = emsiukemiauDcMsgCalc;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getDailyMsgSum() {
        return dailyMsgSum;
    }

    public void setDailyMsgSum(Double dailyMsgSum) {
        this.dailyMsgSum = dailyMsgSum;
    }

    public Double getMboti212DcMsgCalc() {
        return mboti212DcMsgCalc;
    }

    public void setMboti212DcMsgCalc(Double mboti212DcMsgCalc) {
        this.mboti212DcMsgCalc = mboti212DcMsgCalc;
    }

    public Double getFurijaDcMsgCalc() {
        return furijaDcMsgCalc;
    }

    public void setFurijaDcMsgCalc(Double furijaDcMsgCalc) {
        this.furijaDcMsgCalc = furijaDcMsgCalc;
    }

    public Double getErnestasltu12DcMsgCalc() {
        return ernestasltu12DcMsgCalc;
    }

    public void setErnestasltu12DcMsgCalc(Double ernestasltu12DcMsgCalc) {
        this.ernestasltu12DcMsgCalc = ernestasltu12DcMsgCalc;
    }

    public Double getD0fkaDcMsgCalc() {
        return d0fkaDcMsgCalc;
    }

    public void setD0fkaDcMsgCalc(Double d0fkaDcMsgCalc) {
        this.d0fkaDcMsgCalc = d0fkaDcMsgCalc;
    }

    public Double getMelitaLoveDcMsgCalc() {
        return melitaLoveDcMsgCalc;
    }

    public void setMelitaLoveDcMsgCalc(Double melitaLoveDcMsgCalc) {
        this.melitaLoveDcMsgCalc = melitaLoveDcMsgCalc;
    }

    public Double getLibeteDcMsgCalc() {
        return libeteDcMsgCalc;
    }

    public void setLibeteDcMsgCalc(Double libeteDcMsgCalc) {
        this.libeteDcMsgCalc = libeteDcMsgCalc;
    }

    public Double getArienaDcMsgCalc() {
        return arienaDcMsgCalc;
    }

    public void setArienaDcMsgCalc(Double arienaDcMsgCalc) {
        this.arienaDcMsgCalc = arienaDcMsgCalc;
    }

    public Double getSharansDcMsgCalc() {
        return sharansDcMsgCalc;
    }

    public void setSharansDcMsgCalc(Double sharansDcMsgCalc) {
        this.sharansDcMsgCalc = sharansDcMsgCalc;
    }

    public Double getLabasheyDcMsgCalc() {
        return labasheyDcMsgCalc;
    }

    public void setLabasheyDcMsgCalc(Double labasheyDcMsgCalc) {
        this.labasheyDcMsgCalc = labasheyDcMsgCalc;
    }

    public Double getEverlyDcMsgCalc() {
        return everlyDcMsgCalc;
    }

    public void setEverlyDcMsgCalc(Double everlyDcMsgCalc) {
        this.everlyDcMsgCalc = everlyDcMsgCalc;
    }

    public Double getRichPicaDcMsgCalc() {
        return richPicaDcMsgCalc;
    }

    public void setRichPicaDcMsgCalc(Double richPicaDcMsgCalc) {
        this.richPicaDcMsgCalc = richPicaDcMsgCalc;
    }

    public Double getShizoDcMsgCalc() {
        return shizoDcMsgCalc;
    }

    public void setShizoDcMsgCalc(Double shizoDcMsgCalc) {
        this.shizoDcMsgCalc = shizoDcMsgCalc;
    }

    public Double getIeviusDcMsgCalc() {
        return ieviusDcMsgCalc;
    }

    public void setIeviusDcMsgCalc(Double ieviusDcMsgCalc) {
        this.ieviusDcMsgCalc = ieviusDcMsgCalc;
    }

    public Double getBobsBuilderDcMsgCalc() {
        return bobsBuilderDcMsgCalc;
    }

    public void setBobsBuilderDcMsgCalc(Double bobsBuilderDcMsgCalc) {
        this.bobsBuilderDcMsgCalc = bobsBuilderDcMsgCalc;
    }

    public Double getPlrxqDcMsgCalc() {
        return plrxqDcMsgCalc;
    }

    public void setPlrxqDcMsgCalc(Double plrxqDcMsgCalc) {
        this.plrxqDcMsgCalc = plrxqDcMsgCalc;
    }

    public Double getEmsiukemiauDcMsgCalc() {
        return emsiukemiauDcMsgCalc;
    }

    public void setEmsiukemiauDcMsgCalc(Double emsiukemiauDcMsgCalc) {
        this.emsiukemiauDcMsgCalc = emsiukemiauDcMsgCalc;
    }

        
}
