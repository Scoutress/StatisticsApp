package lt.scoutress.StatisticsApp.entity.playtime;

import jakarta.persistence.*;

@Entity
@Table(name = "playtime_data_all_events")
public class PlaytimeDataAllEvents {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //ItsVaidas
    @Column(name = "itsvaidas_on")
    private Integer itsVaidasOn;

    @Column(name = "itsvaidas_off")
    private Integer itsVaidasOff;

    @Column(name = "itsvaidas_diff")
    private Integer itsVaidasDiff;

    //Scoutress
    @Column(name = "scoutress_on")
    private Integer scoutressOn;

    @Column(name = "scoutress_off")
    private Integer scoutressOff;

    @Column(name = "scoutress_diff")
    private Integer scoutressDiff;

    //Mboti212
    @Column(name = "mboti212_on")
    private Integer mboti212On;

    @Column(name = "mboti212_off")
    private Integer mboti212Off;

    @Column(name = "mboti212_diff")
    private Integer mboti212Diff;

    //Furija
    @Column(name = "furija_on")
    private Integer furijaOn;

    @Column(name = "furija_off")
    private Integer furijaOff;
    
    @Column(name = "furija_diff")
    private Integer furijaDiff;

    //Ernestasltu12
    @Column(name = "ernestasltu12_on")
    private Integer ernestasltu12On;

    @Column(name = "ernestasltu12_off")
    private Integer ernestasltu12Off;

    @Column(name = "ernestasltu12_diff")
    private Integer ernestasltu12Diff;

    //D0fka
    @Column(name = "d0fka_on")
    private Integer d0fkaOn;

    @Column(name = "d0fka_off")
    private Integer d0fkaOff;

    @Column(name = "d0fka_diff")
    private Integer d0fkaDiff;

    //MelitaLove
    @Column(name = "melitalove_on")
    private Integer melitaLoveOn;

    @Column(name = "melitalove_off")
    private Integer melitaLoveOff;

    @Column(name = "melitalove_diff")
    private Integer melitaloveDiff;

    //Libete
    @Column(name = "libete_on")
    private Integer libeteOn;

    @Column(name = "libete_off")
    private Integer libeteOff;

    @Column(name = "libete_diff")
    private Integer libeteDiff;

    //Ariena
    @Column(name = "ariena_on")
    private Integer arienaOn;

    @Column(name = "ariena_off")
    private Integer arienaOff;

    @Column(name = "ariena_diff")
    private Integer arienaDiff;

    //Sharans
    @Column(name = "sharans_on")
    private Integer sharansOn;

    @Column(name = "sharans_off")
    private Integer sharansOff;

    @Column(name = "sharans_diff")
    private Integer sharansDiff;

    //labashey
    @Column(name = "labashey_on")
    private Integer labasheyOn;

    @Column(name = "labashey_off")
    private Integer labasheyOff;

    @Column(name = "labashey_diff")
    private Integer labasheyDiff;

    //everly
    @Column(name = "everly_on")
    private Integer everlyOn;

    @Column(name = "everly_off")
    private Integer everlyOff;

    @Column(name = "everly_diff")
    private Integer everlyDiff;

    //RichPica
    @Column(name = "richpica_on")
    private Integer richPicaOn;

    @Column(name = "richpica_off")
    private Integer richPicaOff;

    @Column(name = "richpica_diff")
    private Integer richpicaDiff;

    //Shizo
    @Column(name = "shizo_on")
    private Integer shizoOn;

    @Column(name = "shizo_off")
    private Integer shizoOff;

    @Column(name = "shizo_diff")
    private Integer shizoDiff;
    
    //Ievius
    @Column(name = "ievius_on")
    private Integer ieviusOn;

    @Column(name = "ievius_off")
    private Integer ieviusOff;

    @Column(name = "ievius_diff")
    private Integer ieviusDiff;

    //BobsBuilder
    @Column(name = "bobsbuilder_on")
    private Integer bobsBuilderOn;

    @Column(name = "bobsbuilder_off")
    private Integer bobsBuilderOff;

    @Column(name = "bobsbuilder_diff")
    private Integer bobsBuilderDiff;

    //plrxq
    @Column(name = "plrxq_on")
    private Integer plrxqOn;

    @Column(name = "plrxq_off")
    private Integer plrxqOff;

    @Column(name = "plrxq_diff")
    private Integer plrxqDiff;

    //Emsiukemiau
    @Column(name = "emsiukemiau_on")
    private Integer emsiukemiauOn;

    @Column(name = "emsiukemiau_off")
    private Integer emsiukemiauOff;

    @Column(name = "emsiukemiau_diff")
    private Integer emsiukemiauDiff;

    public PlaytimeDataAllEvents(){}

    public PlaytimeDataAllEvents(Integer id, Integer itsVaidasOn, Integer itsVaidasOff, Integer itsVaidasDiff,
            Integer scoutressOn, Integer scoutressOff, Integer scoutressDiff, Integer mboti212On, Integer mboti212Off,
            Integer mboti212Diff, Integer furijaOn, Integer furijaOff, Integer furijaDiff, Integer ernestasltu12On,
            Integer ernestasltu12Off, Integer ernestasltu12Diff, Integer d0fkaOn, Integer d0fkaOff, Integer d0fkaDiff,
            Integer melitaLoveOn, Integer melitaLoveOff, Integer melitaloveDiff, Integer libeteOn, Integer libeteOff,
            Integer libeteDiff, Integer arienaOn, Integer arienaOff, Integer arienaDiff, Integer sharansOn,
            Integer sharansOff, Integer sharansDiff, Integer labasheyOn, Integer labasheyOff, Integer labasheyDiff,
            Integer everlyOn, Integer everlyOff, Integer everlyDiff, Integer richPicaOn, Integer richPicaOff,
            Integer richpicaDiff, Integer shizoOn, Integer shizoOff, Integer shizoDiff, Integer ieviusOn,
            Integer ieviusOff, Integer ieviusDiff, Integer bobsBuilderOn, Integer bobsBuilderOff,
            Integer bobsBuilderDiff, Integer plrxqOn, Integer plrxqOff, Integer plrxqDiff, Integer emsiukemiauOn,
            Integer emsiukemiauOff, Integer emsiukemiauDiff) {
        this.id = id;
        this.itsVaidasOn = itsVaidasOn;
        this.itsVaidasOff = itsVaidasOff;
        this.itsVaidasDiff = itsVaidasDiff;
        this.scoutressOn = scoutressOn;
        this.scoutressOff = scoutressOff;
        this.scoutressDiff = scoutressDiff;
        this.mboti212On = mboti212On;
        this.mboti212Off = mboti212Off;
        this.mboti212Diff = mboti212Diff;
        this.furijaOn = furijaOn;
        this.furijaOff = furijaOff;
        this.furijaDiff = furijaDiff;
        this.ernestasltu12On = ernestasltu12On;
        this.ernestasltu12Off = ernestasltu12Off;
        this.ernestasltu12Diff = ernestasltu12Diff;
        this.d0fkaOn = d0fkaOn;
        this.d0fkaOff = d0fkaOff;
        this.d0fkaDiff = d0fkaDiff;
        this.melitaLoveOn = melitaLoveOn;
        this.melitaLoveOff = melitaLoveOff;
        this.melitaloveDiff = melitaloveDiff;
        this.libeteOn = libeteOn;
        this.libeteOff = libeteOff;
        this.libeteDiff = libeteDiff;
        this.arienaOn = arienaOn;
        this.arienaOff = arienaOff;
        this.arienaDiff = arienaDiff;
        this.sharansOn = sharansOn;
        this.sharansOff = sharansOff;
        this.sharansDiff = sharansDiff;
        this.labasheyOn = labasheyOn;
        this.labasheyOff = labasheyOff;
        this.labasheyDiff = labasheyDiff;
        this.everlyOn = everlyOn;
        this.everlyOff = everlyOff;
        this.everlyDiff = everlyDiff;
        this.richPicaOn = richPicaOn;
        this.richPicaOff = richPicaOff;
        this.richpicaDiff = richpicaDiff;
        this.shizoOn = shizoOn;
        this.shizoOff = shizoOff;
        this.shizoDiff = shizoDiff;
        this.ieviusOn = ieviusOn;
        this.ieviusOff = ieviusOff;
        this.ieviusDiff = ieviusDiff;
        this.bobsBuilderOn = bobsBuilderOn;
        this.bobsBuilderOff = bobsBuilderOff;
        this.bobsBuilderDiff = bobsBuilderDiff;
        this.plrxqOn = plrxqOn;
        this.plrxqOff = plrxqOff;
        this.plrxqDiff = plrxqDiff;
        this.emsiukemiauOn = emsiukemiauOn;
        this.emsiukemiauOff = emsiukemiauOff;
        this.emsiukemiauDiff = emsiukemiauDiff;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItsVaidasOn() {
        return itsVaidasOn;
    }

    public void setItsVaidasOn(Integer itsVaidasOn) {
        this.itsVaidasOn = itsVaidasOn;
    }

    public Integer getItsVaidasOff() {
        return itsVaidasOff;
    }

    public void setItsVaidasOff(Integer itsVaidasOff) {
        this.itsVaidasOff = itsVaidasOff;
    }

    public Integer getItsVaidasDiff() {
        return itsVaidasDiff;
    }

    public void setItsVaidasDiff(Integer itsVaidasDiff) {
        this.itsVaidasDiff = itsVaidasDiff;
    }

    public Integer getScoutressOn() {
        return scoutressOn;
    }

    public void setScoutressOn(Integer scoutressOn) {
        this.scoutressOn = scoutressOn;
    }

    public Integer getScoutressOff() {
        return scoutressOff;
    }

    public void setScoutressOff(Integer scoutressOff) {
        this.scoutressOff = scoutressOff;
    }

    public Integer getScoutressDiff() {
        return scoutressDiff;
    }

    public void setScoutressDiff(Integer scoutressDiff) {
        this.scoutressDiff = scoutressDiff;
    }

    public Integer getMboti212On() {
        return mboti212On;
    }

    public void setMboti212On(Integer mboti212On) {
        this.mboti212On = mboti212On;
    }

    public Integer getMboti212Off() {
        return mboti212Off;
    }

    public void setMboti212Off(Integer mboti212Off) {
        this.mboti212Off = mboti212Off;
    }

    public Integer getMboti212Diff() {
        return mboti212Diff;
    }

    public void setMboti212Diff(Integer mboti212Diff) {
        this.mboti212Diff = mboti212Diff;
    }

    public Integer getFurijaOn() {
        return furijaOn;
    }

    public void setFurijaOn(Integer furijaOn) {
        this.furijaOn = furijaOn;
    }

    public Integer getFurijaOff() {
        return furijaOff;
    }

    public void setFurijaOff(Integer furijaOff) {
        this.furijaOff = furijaOff;
    }

    public Integer getFurijaDiff() {
        return furijaDiff;
    }

    public void setFurijaDiff(Integer furijaDiff) {
        this.furijaDiff = furijaDiff;
    }

    public Integer getErnestasltu12On() {
        return ernestasltu12On;
    }

    public void setErnestasltu12On(Integer ernestasltu12On) {
        this.ernestasltu12On = ernestasltu12On;
    }

    public Integer getErnestasltu12Off() {
        return ernestasltu12Off;
    }

    public void setErnestasltu12Off(Integer ernestasltu12Off) {
        this.ernestasltu12Off = ernestasltu12Off;
    }

    public Integer getErnestasltu12Diff() {
        return ernestasltu12Diff;
    }

    public void setErnestasltu12Diff(Integer ernestasltu12Diff) {
        this.ernestasltu12Diff = ernestasltu12Diff;
    }

    public Integer getD0fkaOn() {
        return d0fkaOn;
    }

    public void setD0fkaOn(Integer d0fkaOn) {
        this.d0fkaOn = d0fkaOn;
    }

    public Integer getD0fkaOff() {
        return d0fkaOff;
    }

    public void setD0fkaOff(Integer d0fkaOff) {
        this.d0fkaOff = d0fkaOff;
    }

    public Integer getD0fkaDiff() {
        return d0fkaDiff;
    }

    public void setD0fkaDiff(Integer d0fkaDiff) {
        this.d0fkaDiff = d0fkaDiff;
    }

    public Integer getMelitaLoveOn() {
        return melitaLoveOn;
    }

    public void setMelitaLoveOn(Integer melitaLoveOn) {
        this.melitaLoveOn = melitaLoveOn;
    }

    public Integer getMelitaLoveOff() {
        return melitaLoveOff;
    }

    public void setMelitaLoveOff(Integer melitaLoveOff) {
        this.melitaLoveOff = melitaLoveOff;
    }

    public Integer getMelitaloveDiff() {
        return melitaloveDiff;
    }

    public void setMelitaloveDiff(Integer melitaloveDiff) {
        this.melitaloveDiff = melitaloveDiff;
    }

    public Integer getLibeteOn() {
        return libeteOn;
    }

    public void setLibeteOn(Integer libeteOn) {
        this.libeteOn = libeteOn;
    }

    public Integer getLibeteOff() {
        return libeteOff;
    }

    public void setLibeteOff(Integer libeteOff) {
        this.libeteOff = libeteOff;
    }

    public Integer getLibeteDiff() {
        return libeteDiff;
    }

    public void setLibeteDiff(Integer libeteDiff) {
        this.libeteDiff = libeteDiff;
    }

    public Integer getArienaOn() {
        return arienaOn;
    }

    public void setArienaOn(Integer arienaOn) {
        this.arienaOn = arienaOn;
    }

    public Integer getArienaOff() {
        return arienaOff;
    }

    public void setArienaOff(Integer arienaOff) {
        this.arienaOff = arienaOff;
    }

    public Integer getArienaDiff() {
        return arienaDiff;
    }

    public void setArienaDiff(Integer arienaDiff) {
        this.arienaDiff = arienaDiff;
    }

    public Integer getSharansOn() {
        return sharansOn;
    }

    public void setSharansOn(Integer sharansOn) {
        this.sharansOn = sharansOn;
    }

    public Integer getSharansOff() {
        return sharansOff;
    }

    public void setSharansOff(Integer sharansOff) {
        this.sharansOff = sharansOff;
    }

    public Integer getSharansDiff() {
        return sharansDiff;
    }

    public void setSharansDiff(Integer sharansDiff) {
        this.sharansDiff = sharansDiff;
    }

    public Integer getLabasheyOn() {
        return labasheyOn;
    }

    public void setLabasheyOn(Integer labasheyOn) {
        this.labasheyOn = labasheyOn;
    }

    public Integer getLabasheyOff() {
        return labasheyOff;
    }

    public void setLabasheyOff(Integer labasheyOff) {
        this.labasheyOff = labasheyOff;
    }

    public Integer getLabasheyDiff() {
        return labasheyDiff;
    }

    public void setLabasheyDiff(Integer labasheyDiff) {
        this.labasheyDiff = labasheyDiff;
    }

    public Integer getEverlyOn() {
        return everlyOn;
    }

    public void setEverlyOn(Integer everlyOn) {
        this.everlyOn = everlyOn;
    }

    public Integer getEverlyOff() {
        return everlyOff;
    }

    public void setEverlyOff(Integer everlyOff) {
        this.everlyOff = everlyOff;
    }

    public Integer getEverlyDiff() {
        return everlyDiff;
    }

    public void setEverlyDiff(Integer everlyDiff) {
        this.everlyDiff = everlyDiff;
    }

    public Integer getRichPicaOn() {
        return richPicaOn;
    }

    public void setRichPicaOn(Integer richPicaOn) {
        this.richPicaOn = richPicaOn;
    }

    public Integer getRichPicaOff() {
        return richPicaOff;
    }

    public void setRichPicaOff(Integer richPicaOff) {
        this.richPicaOff = richPicaOff;
    }

    public Integer getRichpicaDiff() {
        return richpicaDiff;
    }

    public void setRichpicaDiff(Integer richpicaDiff) {
        this.richpicaDiff = richpicaDiff;
    }

    public Integer getShizoOn() {
        return shizoOn;
    }

    public void setShizoOn(Integer shizoOn) {
        this.shizoOn = shizoOn;
    }

    public Integer getShizoOff() {
        return shizoOff;
    }

    public void setShizoOff(Integer shizoOff) {
        this.shizoOff = shizoOff;
    }

    public Integer getShizoDiff() {
        return shizoDiff;
    }

    public void setShizoDiff(Integer shizoDiff) {
        this.shizoDiff = shizoDiff;
    }

    public Integer getIeviusOn() {
        return ieviusOn;
    }

    public void setIeviusOn(Integer ieviusOn) {
        this.ieviusOn = ieviusOn;
    }

    public Integer getIeviusOff() {
        return ieviusOff;
    }

    public void setIeviusOff(Integer ieviusOff) {
        this.ieviusOff = ieviusOff;
    }

    public Integer getIeviusDiff() {
        return ieviusDiff;
    }

    public void setIeviusDiff(Integer ieviusDiff) {
        this.ieviusDiff = ieviusDiff;
    }

    public Integer getBobsBuilderOn() {
        return bobsBuilderOn;
    }

    public void setBobsBuilderOn(Integer bobsBuilderOn) {
        this.bobsBuilderOn = bobsBuilderOn;
    }

    public Integer getBobsBuilderOff() {
        return bobsBuilderOff;
    }

    public void setBobsBuilderOff(Integer bobsBuilderOff) {
        this.bobsBuilderOff = bobsBuilderOff;
    }

    public Integer getBobsBuilderDiff() {
        return bobsBuilderDiff;
    }

    public void setBobsBuilderDiff(Integer bobsBuilderDiff) {
        this.bobsBuilderDiff = bobsBuilderDiff;
    }

    public Integer getPlrxqOn() {
        return plrxqOn;
    }

    public void setPlrxqOn(Integer plrxqOn) {
        this.plrxqOn = plrxqOn;
    }

    public Integer getPlrxqOff() {
        return plrxqOff;
    }

    public void setPlrxqOff(Integer plrxqOff) {
        this.plrxqOff = plrxqOff;
    }

    public Integer getPlrxqDiff() {
        return plrxqDiff;
    }

    public void setPlrxqDiff(Integer plrxqDiff) {
        this.plrxqDiff = plrxqDiff;
    }

    public Integer getEmsiukemiauOn() {
        return emsiukemiauOn;
    }

    public void setEmsiukemiauOn(Integer emsiukemiauOn) {
        this.emsiukemiauOn = emsiukemiauOn;
    }

    public Integer getEmsiukemiauOff() {
        return emsiukemiauOff;
    }

    public void setEmsiukemiauOff(Integer emsiukemiauOff) {
        this.emsiukemiauOff = emsiukemiauOff;
    }

    public Integer getEmsiukemiauDiff() {
        return emsiukemiauDiff;
    }

    public void setEmsiukemiauDiff(Integer emsiukemiauDiff) {
        this.emsiukemiauDiff = emsiukemiauDiff;
    }

}
