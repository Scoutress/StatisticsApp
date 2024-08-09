package com.scoutress.KaimuxAdminStats.Entity.DcMessages;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dc_messages_texted")
public class DcMessagesTexted {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "mboti212_dc_messages")
    private Double mboti212DcMessages;

    @Column(name = "furija_dc_messages")
    private Double furijaDcMessages;

    @Column(name = "ernestasltu12_dc_messages")
    private Double ernestasltu12DcMessages;

    @Column(name = "d0fka_dc_messages")
    private Double d0fkaDcMessages;

    @Column(name = "melitalove_dc_messages")
    private Double melitaLoveDcMessages;

    @Column(name = "libete_dc_messages")
    private Double libeteDcMessages;

    @Column(name = "ariena_dc_messages")
    private Double arienaDcMessages;

    @Column(name = "sharans_dc_messages")
    private Double sharansDcMessages;

    @Column(name = "labashey_dc_messages")
    private Double labasheyDcMessages;

    @Column(name = "everly_dc_messages")
    private Double everlyDcMessages;

    @Column(name = "richpica_dc_messages")
    private Double richPicaDcMessages;

    @Column(name = "shizo_dc_messages")
    private Double shizoDcMessages;

    @Column(name = "ievius_dc_messages")
    private Double ieviusDcMessages;

    @Column(name = "bobsbuilder_dc_messages")
    private Double bobsBuilderDcMessages;

    @Column(name = "plrxq_dc_messages")
    private Double plrxqDcMessages;

    @Column(name = "emsiukemiau_dc_messages")
    private Double emsiukemiauDcMessages;

    public DcMessagesTexted(){}

    public DcMessagesTexted(LocalDate date, Double mboti212DcMessages, Double furijaDcMessages,
            Double ernestasltu12DcMessages, Double d0fkaDcMessages, Double melitaLoveDcMessages,
            Double libeteDcMessages, Double arienaDcMessages, Double sharansDcMessages, Double labasheyDcMessages,
            Double everlyDcMessages, Double richPicaDcMessages, Double shizoDcMessages, Double ieviusDcMessages,
            Double bobsBuilderDcMessages, Double plrxqDcMessages, Double emsiukemiauDcMessages) {
        this.date = date;
        this.mboti212DcMessages = mboti212DcMessages;
        this.furijaDcMessages = furijaDcMessages;
        this.ernestasltu12DcMessages = ernestasltu12DcMessages;
        this.d0fkaDcMessages = d0fkaDcMessages;
        this.melitaLoveDcMessages = melitaLoveDcMessages;
        this.libeteDcMessages = libeteDcMessages;
        this.arienaDcMessages = arienaDcMessages;
        this.sharansDcMessages = sharansDcMessages;
        this.labasheyDcMessages = labasheyDcMessages;
        this.everlyDcMessages = everlyDcMessages;
        this.richPicaDcMessages = richPicaDcMessages;
        this.shizoDcMessages = shizoDcMessages;
        this.ieviusDcMessages = ieviusDcMessages;
        this.bobsBuilderDcMessages = bobsBuilderDcMessages;
        this.plrxqDcMessages = plrxqDcMessages;
        this.emsiukemiauDcMessages = emsiukemiauDcMessages;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getMboti212DcMessages() {
        return mboti212DcMessages;
    }

    public void setMboti212DcMessages(Double mboti212DcMessages) {
        this.mboti212DcMessages = mboti212DcMessages;
    }

    public Double getFurijaDcMessages() {
        return furijaDcMessages;
    }

    public void setFurijaDcMessages(Double furijaDcMessages) {
        this.furijaDcMessages = furijaDcMessages;
    }

    public Double getErnestasltu12DcMessages() {
        return ernestasltu12DcMessages;
    }

    public void setErnestasltu12DcMessages(Double ernestasltu12DcMessages) {
        this.ernestasltu12DcMessages = ernestasltu12DcMessages;
    }

    public Double getD0fkaDcMessages() {
        return d0fkaDcMessages;
    }

    public void setD0fkaDcMessages(Double d0fkaDcMessages) {
        this.d0fkaDcMessages = d0fkaDcMessages;
    }

    public Double getMelitaLoveDcMessages() {
        return melitaLoveDcMessages;
    }

    public void setMelitaLoveDcMessages(Double melitaLoveDcMessages) {
        this.melitaLoveDcMessages = melitaLoveDcMessages;
    }

    public Double getLibeteDcMessages() {
        return libeteDcMessages;
    }

    public void setLibeteDcMessages(Double libeteDcMessages) {
        this.libeteDcMessages = libeteDcMessages;
    }

    public Double getArienaDcMessages() {
        return arienaDcMessages;
    }

    public void setArienaDcMessages(Double arienaDcMessages) {
        this.arienaDcMessages = arienaDcMessages;
    }

    public Double getSharansDcMessages() {
        return sharansDcMessages;
    }

    public void setSharansDcMessages(Double sharansDcMessages) {
        this.sharansDcMessages = sharansDcMessages;
    }

    public Double getLabasheyDcMessages() {
        return labasheyDcMessages;
    }

    public void setLabasheyDcMessages(Double labasheyDcMessages) {
        this.labasheyDcMessages = labasheyDcMessages;
    }

    public Double getEverlyDcMessages() {
        return everlyDcMessages;
    }

    public void setEverlyDcMessages(Double everlyDcMessages) {
        this.everlyDcMessages = everlyDcMessages;
    }

    public Double getRichPicaDcMessages() {
        return richPicaDcMessages;
    }

    public void setRichPicaDcMessages(Double richPicaDcMessages) {
        this.richPicaDcMessages = richPicaDcMessages;
    }

    public Double getShizoDcMessages() {
        return shizoDcMessages;
    }

    public void setShizoDcMessages(Double shizoDcMessages) {
        this.shizoDcMessages = shizoDcMessages;
    }

    public Double getIeviusDcMessages() {
        return ieviusDcMessages;
    }

    public void setIeviusDcMessages(Double ieviusDcMessages) {
        this.ieviusDcMessages = ieviusDcMessages;
    }

    public Double getBobsBuilderDcMessages() {
        return bobsBuilderDcMessages;
    }

    public void setBobsBuilderDcMessages(Double bobsBuilderDcMessages) {
        this.bobsBuilderDcMessages = bobsBuilderDcMessages;
    }

    public Double getPlrxqDcMessages() {
        return plrxqDcMessages;
    }

    public void setPlrxqDcMessages(Double plrxqDcMessages) {
        this.plrxqDcMessages = plrxqDcMessages;
    }

    public Double getEmsiukemiauDcMessages() {
        return emsiukemiauDcMessages;
    }

    public void setEmsiukemiauDcMessages(Double emsiukemiauDcMessages) {
        this.emsiukemiauDcMessages = emsiukemiauDcMessages;
    }

    
}
