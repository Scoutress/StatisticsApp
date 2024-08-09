package com.scoutress.KaimuxAdminStats.Entity.Playtime;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_playtime")
public class DailyPlaytime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    // Mboti212
    @Column(name = "mboti212_survival")
    private Double mboti212Survival;

    @Column(name = "mboti212_skyblock")
    private Double mboti212Skyblock;

    @Column(name = "mboti212_creative")
    private Double mboti212Creative;

    @Column(name = "mboti212_boxpvp")
    private Double mboti212Boxpvp;

    @Column(name = "mboti212_prison")
    private Double mboti212Prison;

    @Column(name = "mboti212_events")
    private Double mboti212Events;

    // Furija
    @Column(name = "furija_survival")
    private Double furijaSurvival;

    @Column(name = "furija_skyblock")
    private Double furijaSkyblock;

    @Column(name = "furija_creative")
    private Double furijaCreative;

    @Column(name = "furija_boxpvp")
    private Double furijaBoxpvp;

    @Column(name = "furija_prison")
    private Double furijaPrison;

    @Column(name = "furija_events")
    private Double furijaEvents;

    // Ernestasltu12
    @Column(name = "ernestasltu12_survival")
    private Double ernestasltu12Survival;

    @Column(name = "ernestasltu12_skyblock")
    private Double ernestasltu12Skyblock;

    @Column(name = "ernestasltu12_creative")
    private Double ernestasltu12Creative;

    @Column(name = "ernestasltu12_boxpvp")
    private Double ernestasltu12Boxpvp;

    @Column(name = "ernestasltu12_prison")
    private Double ernestasltu12Prison;

    @Column(name = "ernestasltu12_events")
    private Double ernestasltu12Events;

    // D0fka
    @Column(name = "d0fka_survival")
    private Double d0fkaSurvival;

    @Column(name = "d0fka_skyblock")
    private Double d0fkaSkyblock;

    @Column(name = "d0fka_creative")
    private Double d0fkaCreative;

    @Column(name = "d0fka_boxpvp")
    private Double d0fkaBoxpvp;

    @Column(name = "d0fka_prison")
    private Double d0fkaPrison;

    @Column(name = "d0fka_events")
    private Double d0fkaEvents;

    // MelitaLove
    @Column(name = "melitalove_survival")
    private Double melitaloveSurvival;

    @Column(name = "melitalove_skyblock")
    private Double melitaloveSkyblock;

    @Column(name = "melitalove_creative")
    private Double melitaloveCreative;

    @Column(name = "melitalove_boxpvp")
    private Double melitaloveBoxpvp;

    @Column(name = "melitalove_prison")
    private Double melitalovePrison;

    @Column(name = "melitalove_events")
    private Double melitaloveEvents;

    // Libete
    @Column(name = "libete_survival")
    private Double libeteSurvival;

    @Column(name = "libete_skyblock")
    private Double libeteSkyblock;

    @Column(name = "libete_creative")
    private Double libeteCreative;

    @Column(name = "libete_boxpvp")
    private Double libeteBoxpvp;

    @Column(name = "libete_prison")
    private Double libetePrison;

    @Column(name = "libete_events")
    private Double libeteEvents;

    // Ariena
    @Column(name = "ariena_survival")
    private Double arienaSurvival;

    @Column(name = "ariena_skyblock")
    private Double arienaSkyblock;

    @Column(name = "ariena_creative")
    private Double arienaCreative;

    @Column(name = "ariena_boxpvp")
    private Double arienaBoxpvp;

    @Column(name = "ariena_prison")
    private Double arienaPrison;

    @Column(name = "ariena_events")
    private Double arienaEvents;

    // Sharans
    @Column(name = "sharans_survival")
    private Double sharansSurvival;

    @Column(name = "sharans_skyblock")
    private Double sharansSkyblock;

    @Column(name = "sharans_creative")
    private Double sharansCreative;

    @Column(name = "sharans_boxpvp")
    private Double sharansBoxpvp;

    @Column(name = "sharans_prison")
    private Double sharansPrison;

    @Column(name = "sharans_events")
    private Double sharansEvents;

    // labashey
    @Column(name = "labashey_survival")
    private Double labasheySurvival;

    @Column(name = "labashey_skyblock")
    private Double labasheySkyblock;

    @Column(name = "labashey_creative")
    private Double labasheyCreative;

    @Column(name = "labashey_boxpvp")
    private Double labasheyBoxpvp;

    @Column(name = "labashey_prison")
    private Double labasheyPrison;

    @Column(name = "labashey_events")
    private Double labasheyEvents;

    // everly
    @Column(name = "everly_survival")
    private Double everlySurvival;

    @Column(name = "everly_skyblock")
    private Double everlySkyblock;

    @Column(name = "everly_creative")
    private Double everlyCreative;

    @Column(name = "everly_boxpvp")
    private Double everlyBoxpvp;

    @Column(name = "everly_prison")
    private Double everlyPrison;

    @Column(name = "everly_events")
    private Double everlyEvents;

    // RichPica
    @Column(name = "richpica_survival")
    private Double richpicaSurvival;

    @Column(name = "richpica_skyblock")
    private Double richpicaSkyblock;

    @Column(name = "richpica_creative")
    private Double richpicaCreative;

    @Column(name = "richpica_boxpvp")
    private Double richpicaBoxpvp;

    @Column(name = "richpica_prison")
    private Double richpicaPrison;

    @Column(name = "richpica_events")
    private Double richpicaEvents;

    // Shizo
    @Column(name = "shizo_survival")
    private Double shizoSurvival;

    @Column(name = "shizo_skyblock")
    private Double shizoSkyblock;

    @Column(name = "shizo_creative")
    private Double shizoCreative;

    @Column(name = "shizo_boxpvp")
    private Double shizoBoxpvp;

    @Column(name = "shizo_prison")
    private Double shizoPrison;

    @Column(name = "shizo_events")
    private Double shizoEvents;

    // plrxq
    @Column(name = "plrxq_survival")
    private Double plrxqSurvival;

    @Column(name = "plrxq_skyblock")
    private Double plrxqSkyblock;

    @Column(name = "plrxq_creative")
    private Double plrxqCreative;

    @Column(name = "plrxq_boxpvp")
    private Double plrxqBoxpvp;

    @Column(name = "plrxq_prison")
    private Double plrxqPrison;

    @Column(name = "plrxq_events")
    private Double plrxqEvents;

    // BobsBuilder
    @Column(name = "bobsbuilder_survival")
    private Double bobsbuilderSurvival;

    @Column(name = "bobsbuilder_skyblock")
    private Double bobsbuilderSkyblock;

    @Column(name = "bobsbuilder_creative")
    private Double bobsbuilderCreative;

    @Column(name = "bobsbuilder_boxpvp")
    private Double bobsbuilderBoxpvp;

    @Column(name = "bobsbuilder_prison")
    private Double bobsbuilderPrison;

    @Column(name = "bobsbuilder_events")
    private Double bobsbuilderEvents;

    // Emsiukemiau
    @Column(name = "emsiukemiau_survival")
    private Double emsiukemiauSurvival;

    @Column(name = "emsiukemiau_skyblock")
    private Double emsiukemiauSkyblock;

    @Column(name = "emsiukemiau_creative")
    private Double emsiukemiauCreative;

    @Column(name = "emsiukemiau_boxpvp")
    private Double emsiukemiauBoxpvp;

    @Column(name = "emsiukemiau_prison")
    private Double emsiukemiauPrison;

    @Column(name = "emsiukemiau_events")
    private Double emsiukemiauEvents;

    public DailyPlaytime(){}

    public DailyPlaytime(Integer id, LocalDate date, Double mboti212Survival, Double mboti212Skyblock,
            Double mboti212Creative, Double mboti212Boxpvp, Double mboti212Prison, Double mboti212Events,
            Double furijaSurvival, Double furijaSkyblock, Double furijaCreative, Double furijaBoxpvp,
            Double furijaPrison, Double furijaEvents, Double ernestasltu12Survival, Double ernestasltu12Skyblock,
            Double ernestasltu12Creative, Double ernestasltu12Boxpvp, Double ernestasltu12Prison,
            Double ernestasltu12Events, Double d0fkaSurvival, Double d0fkaSkyblock, Double d0fkaCreative,
            Double d0fkaBoxpvp, Double d0fkaPrison, Double d0fkaEvents, Double melitaloveSurvival,
            Double melitaloveSkyblock, Double melitaloveCreative, Double melitaloveBoxpvp, Double melitalovePrison,
            Double melitaloveEvents, Double libeteSurvival, Double libeteSkyblock, Double libeteCreative,
            Double libeteBoxpvp, Double libetePrison, Double libeteEvents, Double arienaSurvival, Double arienaSkyblock,
            Double arienaCreative, Double arienaBoxpvp, Double arienaPrison, Double arienaEvents,
            Double sharansSurvival, Double sharansSkyblock, Double sharansCreative, Double sharansBoxpvp,
            Double sharansPrison, Double sharansEvents, Double labasheySurvival, Double labasheySkyblock,
            Double labasheyCreative, Double labasheyBoxpvp, Double labasheyPrison, Double labasheyEvents,
            Double everlySurvival, Double everlySkyblock, Double everlyCreative, Double everlyBoxpvp,
            Double everlyPrison, Double everlyEvents, Double richpicaSurvival, Double richpicaSkyblock,
            Double richpicaCreative, Double richpicaBoxpvp, Double richpicaPrison, Double richpicaEvents,
            Double shizoSurvival, Double shizoSkyblock, Double shizoCreative, Double shizoBoxpvp, Double shizoPrison,
            Double shizoEvents, Double plrxqSurvival, Double plrxqSkyblock, Double plrxqCreative, Double plrxqBoxpvp,
            Double plrxqPrison, Double plrxqEvents, Double bobsbuilderSurvival, Double bobsbuilderSkyblock,
            Double bobsbuilderCreative, Double bobsbuilderBoxpvp, Double bobsbuilderPrison, Double bobsbuilderEvents,
            Double emsiukemiauSurvival, Double emsiukemiauSkyblock, Double emsiukemiauCreative,
            Double emsiukemiauBoxpvp, Double emsiukemiauPrison, Double emsiukemiauEvents) {
        this.id = id;
        this.date = date;
        this.mboti212Survival = mboti212Survival;
        this.mboti212Skyblock = mboti212Skyblock;
        this.mboti212Creative = mboti212Creative;
        this.mboti212Boxpvp = mboti212Boxpvp;
        this.mboti212Prison = mboti212Prison;
        this.mboti212Events = mboti212Events;
        this.furijaSurvival = furijaSurvival;
        this.furijaSkyblock = furijaSkyblock;
        this.furijaCreative = furijaCreative;
        this.furijaBoxpvp = furijaBoxpvp;
        this.furijaPrison = furijaPrison;
        this.furijaEvents = furijaEvents;
        this.ernestasltu12Survival = ernestasltu12Survival;
        this.ernestasltu12Skyblock = ernestasltu12Skyblock;
        this.ernestasltu12Creative = ernestasltu12Creative;
        this.ernestasltu12Boxpvp = ernestasltu12Boxpvp;
        this.ernestasltu12Prison = ernestasltu12Prison;
        this.ernestasltu12Events = ernestasltu12Events;
        this.d0fkaSurvival = d0fkaSurvival;
        this.d0fkaSkyblock = d0fkaSkyblock;
        this.d0fkaCreative = d0fkaCreative;
        this.d0fkaBoxpvp = d0fkaBoxpvp;
        this.d0fkaPrison = d0fkaPrison;
        this.d0fkaEvents = d0fkaEvents;
        this.melitaloveSurvival = melitaloveSurvival;
        this.melitaloveSkyblock = melitaloveSkyblock;
        this.melitaloveCreative = melitaloveCreative;
        this.melitaloveBoxpvp = melitaloveBoxpvp;
        this.melitalovePrison = melitalovePrison;
        this.melitaloveEvents = melitaloveEvents;
        this.libeteSurvival = libeteSurvival;
        this.libeteSkyblock = libeteSkyblock;
        this.libeteCreative = libeteCreative;
        this.libeteBoxpvp = libeteBoxpvp;
        this.libetePrison = libetePrison;
        this.libeteEvents = libeteEvents;
        this.arienaSurvival = arienaSurvival;
        this.arienaSkyblock = arienaSkyblock;
        this.arienaCreative = arienaCreative;
        this.arienaBoxpvp = arienaBoxpvp;
        this.arienaPrison = arienaPrison;
        this.arienaEvents = arienaEvents;
        this.sharansSurvival = sharansSurvival;
        this.sharansSkyblock = sharansSkyblock;
        this.sharansCreative = sharansCreative;
        this.sharansBoxpvp = sharansBoxpvp;
        this.sharansPrison = sharansPrison;
        this.sharansEvents = sharansEvents;
        this.labasheySurvival = labasheySurvival;
        this.labasheySkyblock = labasheySkyblock;
        this.labasheyCreative = labasheyCreative;
        this.labasheyBoxpvp = labasheyBoxpvp;
        this.labasheyPrison = labasheyPrison;
        this.labasheyEvents = labasheyEvents;
        this.everlySurvival = everlySurvival;
        this.everlySkyblock = everlySkyblock;
        this.everlyCreative = everlyCreative;
        this.everlyBoxpvp = everlyBoxpvp;
        this.everlyPrison = everlyPrison;
        this.everlyEvents = everlyEvents;
        this.richpicaSurvival = richpicaSurvival;
        this.richpicaSkyblock = richpicaSkyblock;
        this.richpicaCreative = richpicaCreative;
        this.richpicaBoxpvp = richpicaBoxpvp;
        this.richpicaPrison = richpicaPrison;
        this.richpicaEvents = richpicaEvents;
        this.shizoSurvival = shizoSurvival;
        this.shizoSkyblock = shizoSkyblock;
        this.shizoCreative = shizoCreative;
        this.shizoBoxpvp = shizoBoxpvp;
        this.shizoPrison = shizoPrison;
        this.shizoEvents = shizoEvents;
        this.plrxqSurvival = plrxqSurvival;
        this.plrxqSkyblock = plrxqSkyblock;
        this.plrxqCreative = plrxqCreative;
        this.plrxqBoxpvp = plrxqBoxpvp;
        this.plrxqPrison = plrxqPrison;
        this.plrxqEvents = plrxqEvents;
        this.bobsbuilderSurvival = bobsbuilderSurvival;
        this.bobsbuilderSkyblock = bobsbuilderSkyblock;
        this.bobsbuilderCreative = bobsbuilderCreative;
        this.bobsbuilderBoxpvp = bobsbuilderBoxpvp;
        this.bobsbuilderPrison = bobsbuilderPrison;
        this.bobsbuilderEvents = bobsbuilderEvents;
        this.emsiukemiauSurvival = emsiukemiauSurvival;
        this.emsiukemiauSkyblock = emsiukemiauSkyblock;
        this.emsiukemiauCreative = emsiukemiauCreative;
        this.emsiukemiauBoxpvp = emsiukemiauBoxpvp;
        this.emsiukemiauPrison = emsiukemiauPrison;
        this.emsiukemiauEvents = emsiukemiauEvents;
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

    public Double getMboti212Survival() {
        return mboti212Survival;
    }

    public void setMboti212Survival(Double mboti212Survival) {
        this.mboti212Survival = mboti212Survival;
    }

    public Double getMboti212Skyblock() {
        return mboti212Skyblock;
    }

    public void setMboti212Skyblock(Double mboti212Skyblock) {
        this.mboti212Skyblock = mboti212Skyblock;
    }

    public Double getMboti212Creative() {
        return mboti212Creative;
    }

    public void setMboti212Creative(Double mboti212Creative) {
        this.mboti212Creative = mboti212Creative;
    }

    public Double getMboti212Boxpvp() {
        return mboti212Boxpvp;
    }

    public void setMboti212Boxpvp(Double mboti212Boxpvp) {
        this.mboti212Boxpvp = mboti212Boxpvp;
    }

    public Double getMboti212Prison() {
        return mboti212Prison;
    }

    public void setMboti212Prison(Double mboti212Prison) {
        this.mboti212Prison = mboti212Prison;
    }

    public Double getMboti212Events() {
        return mboti212Events;
    }

    public void setMboti212Events(Double mboti212Events) {
        this.mboti212Events = mboti212Events;
    }

    public Double getFurijaSurvival() {
        return furijaSurvival;
    }

    public void setFurijaSurvival(Double furijaSurvival) {
        this.furijaSurvival = furijaSurvival;
    }

    public Double getFurijaSkyblock() {
        return furijaSkyblock;
    }

    public void setFurijaSkyblock(Double furijaSkyblock) {
        this.furijaSkyblock = furijaSkyblock;
    }

    public Double getFurijaCreative() {
        return furijaCreative;
    }

    public void setFurijaCreative(Double furijaCreative) {
        this.furijaCreative = furijaCreative;
    }

    public Double getFurijaBoxpvp() {
        return furijaBoxpvp;
    }

    public void setFurijaBoxpvp(Double furijaBoxpvp) {
        this.furijaBoxpvp = furijaBoxpvp;
    }

    public Double getFurijaPrison() {
        return furijaPrison;
    }

    public void setFurijaPrison(Double furijaPrison) {
        this.furijaPrison = furijaPrison;
    }

    public Double getFurijaEvents() {
        return furijaEvents;
    }

    public void setFurijaEvents(Double furijaEvents) {
        this.furijaEvents = furijaEvents;
    }

    public Double getErnestasltu12Survival() {
        return ernestasltu12Survival;
    }

    public void setErnestasltu12Survival(Double ernestasltu12Survival) {
        this.ernestasltu12Survival = ernestasltu12Survival;
    }

    public Double getErnestasltu12Skyblock() {
        return ernestasltu12Skyblock;
    }

    public void setErnestasltu12Skyblock(Double ernestasltu12Skyblock) {
        this.ernestasltu12Skyblock = ernestasltu12Skyblock;
    }

    public Double getErnestasltu12Creative() {
        return ernestasltu12Creative;
    }

    public void setErnestasltu12Creative(Double ernestasltu12Creative) {
        this.ernestasltu12Creative = ernestasltu12Creative;
    }

    public Double getErnestasltu12Boxpvp() {
        return ernestasltu12Boxpvp;
    }

    public void setErnestasltu12Boxpvp(Double ernestasltu12Boxpvp) {
        this.ernestasltu12Boxpvp = ernestasltu12Boxpvp;
    }

    public Double getErnestasltu12Prison() {
        return ernestasltu12Prison;
    }

    public void setErnestasltu12Prison(Double ernestasltu12Prison) {
        this.ernestasltu12Prison = ernestasltu12Prison;
    }

    public Double getErnestasltu12Events() {
        return ernestasltu12Events;
    }

    public void setErnestasltu12Events(Double ernestasltu12Events) {
        this.ernestasltu12Events = ernestasltu12Events;
    }

    public Double getD0fkaSurvival() {
        return d0fkaSurvival;
    }

    public void setD0fkaSurvival(Double d0fkaSurvival) {
        this.d0fkaSurvival = d0fkaSurvival;
    }

    public Double getD0fkaSkyblock() {
        return d0fkaSkyblock;
    }

    public void setD0fkaSkyblock(Double d0fkaSkyblock) {
        this.d0fkaSkyblock = d0fkaSkyblock;
    }

    public Double getD0fkaCreative() {
        return d0fkaCreative;
    }

    public void setD0fkaCreative(Double d0fkaCreative) {
        this.d0fkaCreative = d0fkaCreative;
    }

    public Double getD0fkaBoxpvp() {
        return d0fkaBoxpvp;
    }

    public void setD0fkaBoxpvp(Double d0fkaBoxpvp) {
        this.d0fkaBoxpvp = d0fkaBoxpvp;
    }

    public Double getD0fkaPrison() {
        return d0fkaPrison;
    }

    public void setD0fkaPrison(Double d0fkaPrison) {
        this.d0fkaPrison = d0fkaPrison;
    }

    public Double getD0fkaEvents() {
        return d0fkaEvents;
    }

    public void setD0fkaEvents(Double d0fkaEvents) {
        this.d0fkaEvents = d0fkaEvents;
    }

    public Double getMelitaloveSurvival() {
        return melitaloveSurvival;
    }

    public void setMelitaloveSurvival(Double melitaloveSurvival) {
        this.melitaloveSurvival = melitaloveSurvival;
    }

    public Double getMelitaloveSkyblock() {
        return melitaloveSkyblock;
    }

    public void setMelitaloveSkyblock(Double melitaloveSkyblock) {
        this.melitaloveSkyblock = melitaloveSkyblock;
    }

    public Double getMelitaloveCreative() {
        return melitaloveCreative;
    }

    public void setMelitaloveCreative(Double melitaloveCreative) {
        this.melitaloveCreative = melitaloveCreative;
    }

    public Double getMelitaloveBoxpvp() {
        return melitaloveBoxpvp;
    }

    public void setMelitaloveBoxpvp(Double melitaloveBoxpvp) {
        this.melitaloveBoxpvp = melitaloveBoxpvp;
    }

    public Double getMelitalovePrison() {
        return melitalovePrison;
    }

    public void setMelitalovePrison(Double melitalovePrison) {
        this.melitalovePrison = melitalovePrison;
    }

    public Double getMelitaloveEvents() {
        return melitaloveEvents;
    }

    public void setMelitaloveEvents(Double melitaloveEvents) {
        this.melitaloveEvents = melitaloveEvents;
    }

    public Double getLibeteSurvival() {
        return libeteSurvival;
    }

    public void setLibeteSurvival(Double libeteSurvival) {
        this.libeteSurvival = libeteSurvival;
    }

    public Double getLibeteSkyblock() {
        return libeteSkyblock;
    }

    public void setLibeteSkyblock(Double libeteSkyblock) {
        this.libeteSkyblock = libeteSkyblock;
    }

    public Double getLibeteCreative() {
        return libeteCreative;
    }

    public void setLibeteCreative(Double libeteCreative) {
        this.libeteCreative = libeteCreative;
    }

    public Double getLibeteBoxpvp() {
        return libeteBoxpvp;
    }

    public void setLibeteBoxpvp(Double libeteBoxpvp) {
        this.libeteBoxpvp = libeteBoxpvp;
    }

    public Double getLibetePrison() {
        return libetePrison;
    }

    public void setLibetePrison(Double libetePrison) {
        this.libetePrison = libetePrison;
    }

    public Double getLibeteEvents() {
        return libeteEvents;
    }

    public void setLibeteEvents(Double libeteEvents) {
        this.libeteEvents = libeteEvents;
    }

    public Double getArienaSurvival() {
        return arienaSurvival;
    }

    public void setArienaSurvival(Double arienaSurvival) {
        this.arienaSurvival = arienaSurvival;
    }

    public Double getArienaSkyblock() {
        return arienaSkyblock;
    }

    public void setArienaSkyblock(Double arienaSkyblock) {
        this.arienaSkyblock = arienaSkyblock;
    }

    public Double getArienaCreative() {
        return arienaCreative;
    }

    public void setArienaCreative(Double arienaCreative) {
        this.arienaCreative = arienaCreative;
    }

    public Double getArienaBoxpvp() {
        return arienaBoxpvp;
    }

    public void setArienaBoxpvp(Double arienaBoxpvp) {
        this.arienaBoxpvp = arienaBoxpvp;
    }

    public Double getArienaPrison() {
        return arienaPrison;
    }

    public void setArienaPrison(Double arienaPrison) {
        this.arienaPrison = arienaPrison;
    }

    public Double getArienaEvents() {
        return arienaEvents;
    }

    public void setArienaEvents(Double arienaEvents) {
        this.arienaEvents = arienaEvents;
    }

    public Double getSharansSurvival() {
        return sharansSurvival;
    }

    public void setSharansSurvival(Double sharansSurvival) {
        this.sharansSurvival = sharansSurvival;
    }

    public Double getSharansSkyblock() {
        return sharansSkyblock;
    }

    public void setSharansSkyblock(Double sharansSkyblock) {
        this.sharansSkyblock = sharansSkyblock;
    }

    public Double getSharansCreative() {
        return sharansCreative;
    }

    public void setSharansCreative(Double sharansCreative) {
        this.sharansCreative = sharansCreative;
    }

    public Double getSharansBoxpvp() {
        return sharansBoxpvp;
    }

    public void setSharansBoxpvp(Double sharansBoxpvp) {
        this.sharansBoxpvp = sharansBoxpvp;
    }

    public Double getSharansPrison() {
        return sharansPrison;
    }

    public void setSharansPrison(Double sharansPrison) {
        this.sharansPrison = sharansPrison;
    }

    public Double getSharansEvents() {
        return sharansEvents;
    }

    public void setSharansEvents(Double sharansEvents) {
        this.sharansEvents = sharansEvents;
    }

    public Double getLabasheySurvival() {
        return labasheySurvival;
    }

    public void setLabasheySurvival(Double labasheySurvival) {
        this.labasheySurvival = labasheySurvival;
    }

    public Double getLabasheySkyblock() {
        return labasheySkyblock;
    }

    public void setLabasheySkyblock(Double labasheySkyblock) {
        this.labasheySkyblock = labasheySkyblock;
    }

    public Double getLabasheyCreative() {
        return labasheyCreative;
    }

    public void setLabasheyCreative(Double labasheyCreative) {
        this.labasheyCreative = labasheyCreative;
    }

    public Double getLabasheyBoxpvp() {
        return labasheyBoxpvp;
    }

    public void setLabasheyBoxpvp(Double labasheyBoxpvp) {
        this.labasheyBoxpvp = labasheyBoxpvp;
    }

    public Double getLabasheyPrison() {
        return labasheyPrison;
    }

    public void setLabasheyPrison(Double labasheyPrison) {
        this.labasheyPrison = labasheyPrison;
    }

    public Double getLabasheyEvents() {
        return labasheyEvents;
    }

    public void setLabasheyEvents(Double labasheyEvents) {
        this.labasheyEvents = labasheyEvents;
    }

    public Double getEverlySurvival() {
        return everlySurvival;
    }

    public void setEverlySurvival(Double everlySurvival) {
        this.everlySurvival = everlySurvival;
    }

    public Double getEverlySkyblock() {
        return everlySkyblock;
    }

    public void setEverlySkyblock(Double everlySkyblock) {
        this.everlySkyblock = everlySkyblock;
    }

    public Double getEverlyCreative() {
        return everlyCreative;
    }

    public void setEverlyCreative(Double everlyCreative) {
        this.everlyCreative = everlyCreative;
    }

    public Double getEverlyBoxpvp() {
        return everlyBoxpvp;
    }

    public void setEverlyBoxpvp(Double everlyBoxpvp) {
        this.everlyBoxpvp = everlyBoxpvp;
    }

    public Double getEverlyPrison() {
        return everlyPrison;
    }

    public void setEverlyPrison(Double everlyPrison) {
        this.everlyPrison = everlyPrison;
    }

    public Double getEverlyEvents() {
        return everlyEvents;
    }

    public void setEverlyEvents(Double everlyEvents) {
        this.everlyEvents = everlyEvents;
    }

    public Double getRichpicaSurvival() {
        return richpicaSurvival;
    }

    public void setRichpicaSurvival(Double richpicaSurvival) {
        this.richpicaSurvival = richpicaSurvival;
    }

    public Double getRichpicaSkyblock() {
        return richpicaSkyblock;
    }

    public void setRichpicaSkyblock(Double richpicaSkyblock) {
        this.richpicaSkyblock = richpicaSkyblock;
    }

    public Double getRichpicaCreative() {
        return richpicaCreative;
    }

    public void setRichpicaCreative(Double richpicaCreative) {
        this.richpicaCreative = richpicaCreative;
    }

    public Double getRichpicaBoxpvp() {
        return richpicaBoxpvp;
    }

    public void setRichpicaBoxpvp(Double richpicaBoxpvp) {
        this.richpicaBoxpvp = richpicaBoxpvp;
    }

    public Double getRichpicaPrison() {
        return richpicaPrison;
    }

    public void setRichpicaPrison(Double richpicaPrison) {
        this.richpicaPrison = richpicaPrison;
    }

    public Double getRichpicaEvents() {
        return richpicaEvents;
    }

    public void setRichpicaEvents(Double richpicaEvents) {
        this.richpicaEvents = richpicaEvents;
    }

    public Double getShizoSurvival() {
        return shizoSurvival;
    }

    public void setShizoSurvival(Double shizoSurvival) {
        this.shizoSurvival = shizoSurvival;
    }

    public Double getShizoSkyblock() {
        return shizoSkyblock;
    }

    public void setShizoSkyblock(Double shizoSkyblock) {
        this.shizoSkyblock = shizoSkyblock;
    }

    public Double getShizoCreative() {
        return shizoCreative;
    }

    public void setShizoCreative(Double shizoCreative) {
        this.shizoCreative = shizoCreative;
    }

    public Double getShizoBoxpvp() {
        return shizoBoxpvp;
    }

    public void setShizoBoxpvp(Double shizoBoxpvp) {
        this.shizoBoxpvp = shizoBoxpvp;
    }

    public Double getShizoPrison() {
        return shizoPrison;
    }

    public void setShizoPrison(Double shizoPrison) {
        this.shizoPrison = shizoPrison;
    }

    public Double getShizoEvents() {
        return shizoEvents;
    }

    public void setShizoEvents(Double shizoEvents) {
        this.shizoEvents = shizoEvents;
    }

    public Double getPlrxqSurvival() {
        return plrxqSurvival;
    }

    public void setPlrxqSurvival(Double plrxqSurvival) {
        this.plrxqSurvival = plrxqSurvival;
    }

    public Double getPlrxqSkyblock() {
        return plrxqSkyblock;
    }

    public void setPlrxqSkyblock(Double plrxqSkyblock) {
        this.plrxqSkyblock = plrxqSkyblock;
    }

    public Double getPlrxqCreative() {
        return plrxqCreative;
    }

    public void setPlrxqCreative(Double plrxqCreative) {
        this.plrxqCreative = plrxqCreative;
    }

    public Double getPlrxqBoxpvp() {
        return plrxqBoxpvp;
    }

    public void setPlrxqBoxpvp(Double plrxqBoxpvp) {
        this.plrxqBoxpvp = plrxqBoxpvp;
    }

    public Double getPlrxqPrison() {
        return plrxqPrison;
    }

    public void setPlrxqPrison(Double plrxqPrison) {
        this.plrxqPrison = plrxqPrison;
    }

    public Double getPlrxqEvents() {
        return plrxqEvents;
    }

    public void setPlrxqEvents(Double plrxqEvents) {
        this.plrxqEvents = plrxqEvents;
    }

    public Double getBobsbuilderSurvival() {
        return bobsbuilderSurvival;
    }

    public void setBobsbuilderSurvival(Double bobsbuilderSurvival) {
        this.bobsbuilderSurvival = bobsbuilderSurvival;
    }

    public Double getBobsbuilderSkyblock() {
        return bobsbuilderSkyblock;
    }

    public void setBobsbuilderSkyblock(Double bobsbuilderSkyblock) {
        this.bobsbuilderSkyblock = bobsbuilderSkyblock;
    }

    public Double getBobsbuilderCreative() {
        return bobsbuilderCreative;
    }

    public void setBobsbuilderCreative(Double bobsbuilderCreative) {
        this.bobsbuilderCreative = bobsbuilderCreative;
    }

    public Double getBobsbuilderBoxpvp() {
        return bobsbuilderBoxpvp;
    }

    public void setBobsbuilderBoxpvp(Double bobsbuilderBoxpvp) {
        this.bobsbuilderBoxpvp = bobsbuilderBoxpvp;
    }

    public Double getBobsbuilderPrison() {
        return bobsbuilderPrison;
    }

    public void setBobsbuilderPrison(Double bobsbuilderPrison) {
        this.bobsbuilderPrison = bobsbuilderPrison;
    }

    public Double getBobsbuilderEvents() {
        return bobsbuilderEvents;
    }

    public void setBobsbuilderEvents(Double bobsbuilderEvents) {
        this.bobsbuilderEvents = bobsbuilderEvents;
    }

    public Double getEmsiukemiauSurvival() {
        return emsiukemiauSurvival;
    }

    public void setEmsiukemiauSurvival(Double emsiukemiauSurvival) {
        this.emsiukemiauSurvival = emsiukemiauSurvival;
    }

    public Double getEmsiukemiauSkyblock() {
        return emsiukemiauSkyblock;
    }

    public void setEmsiukemiauSkyblock(Double emsiukemiauSkyblock) {
        this.emsiukemiauSkyblock = emsiukemiauSkyblock;
    }

    public Double getEmsiukemiauCreative() {
        return emsiukemiauCreative;
    }

    public void setEmsiukemiauCreative(Double emsiukemiauCreative) {
        this.emsiukemiauCreative = emsiukemiauCreative;
    }

    public Double getEmsiukemiauBoxpvp() {
        return emsiukemiauBoxpvp;
    }

    public void setEmsiukemiauBoxpvp(Double emsiukemiauBoxpvp) {
        this.emsiukemiauBoxpvp = emsiukemiauBoxpvp;
    }

    public Double getEmsiukemiauPrison() {
        return emsiukemiauPrison;
    }

    public void setEmsiukemiauPrison(Double emsiukemiauPrison) {
        this.emsiukemiauPrison = emsiukemiauPrison;
    }

    public Double getEmsiukemiauEvents() {
        return emsiukemiauEvents;
    }

    public void setEmsiukemiauEvents(Double emsiukemiauEvents) {
        this.emsiukemiauEvents = emsiukemiauEvents;
    }
}
