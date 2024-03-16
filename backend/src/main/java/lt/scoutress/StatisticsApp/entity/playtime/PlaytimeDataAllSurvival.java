package lt.scoutress.StatisticsApp.entity.playtime;

import jakarta.persistence.*;

@Entity
@Table(name = "playtime_data_all_survival")
public class PlaytimeDataAllSurvival {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //ItsVaidas
    @Column(name = "itsvaidas_on")
    private Integer itsVaidasOn;

    @Column(name = "itsvaidas_off")
    private Integer itsVaidasOff;

    //Scoutress
    @Column(name = "scoutress_on")
    private Integer scoutressOn;

    @Column(name = "scoutress_off")
    private Integer scoutressOff;

    //Mboti212
    @Column(name = "mboti212_on")
    private Integer mboti212On;

    @Column(name = "mboti212_off")
    private Integer mboti212Off;

    //Furija
    @Column(name = "furija_on")
    private Integer furijaOn;

    @Column(name = "furija_off")
    private Integer furijaOff;

    //Ernestasltu12
    @Column(name = "ernestasltu12_on")
    private Integer ernestasltu12On;

    @Column(name = "ernestasltu12_off")
    private Integer ernestasltu12Off;

    //D0fka
    @Column(name = "d0fka_on")
    private Integer d0fkaOn;

    @Column(name = "d0fka_off")
    private Integer d0fkaOff;

    //MelitaLove
    @Column(name = "melitalove_on")
    private Integer melitaLoveOn;

    @Column(name = "melitalove_off")
    private Integer melitaLoveOff;

    //Libete
    @Column(name = "libete_on")
    private Integer libeteOn;

    @Column(name = "libete_off")
    private Integer libeteOff;

    //Ariena
    @Column(name = "ariena_on")
    private Integer arienaOn;

    @Column(name = "ariena_off")
    private Integer arienaOff;

    //Sharans
    @Column(name = "sharans_on")
    private Integer sharansOn;

    @Column(name = "sharans_off")
    private Integer sharansOff;

    //labashey
    @Column(name = "labashey_on")
    private Integer labasheyOn;

    @Column(name = "labashey_off")
    private Integer labasheyOff;

    //everly
    @Column(name = "everly_on")
    private Integer everlyOn;

    @Column(name = "everly_off")
    private Integer everlyOff;

    //RichPica
    @Column(name = "richpica_on")
    private Integer richPicaOn;

    @Column(name = "richpica_off")
    private Integer richPicaOff;

    //Shizo
    @Column(name = "shizo_on")
    private Integer shizoOn;

    @Column(name = "shizo_off")
    private Integer shizoOff;

    
    //Ievius
    @Column(name = "ievius_on")
    private Integer ieviusOn;

    @Column(name = "ievius_off")
    private Integer ieviusOff;

    //BobsBuilder
    @Column(name = "bobsbuilder_on")
    private Integer bobsBuilderOn;

    @Column(name = "bobsbuilder_off")
    private Integer bobsBuilderOff;

    //plrxq
    @Column(name = "plrxq_on")
    private Integer plrxqOn;

    @Column(name = "plrxq_off")
    private Integer plrxqOff;

    //Emsiukemiau
    @Column(name = "emsiukemiau_on")
    private Integer emsiukemiauOn;

    @Column(name = "emsiukemiau_off")
    private Integer emsiukemiauOff;

    public PlaytimeDataAllSurvival(){}

    public PlaytimeDataAllSurvival(Integer itsVaidasOn, Integer itsVaidasOff, Integer scoutressOn, Integer scoutressOff,
            Integer mboti212On, Integer mboti212Off, Integer furijaOn, Integer furijaOff, Integer ernestasltu12On,
            Integer ernestasltu12Off, Integer d0fkaOn, Integer d0fkaOff, Integer melitaLoveOn, Integer melitaLoveOff,
            Integer libeteOn, Integer libeteOff, Integer arienaOn, Integer arienaOff, Integer sharansOn,
            Integer sharansOff, Integer labasheyOn, Integer labasheyOff, Integer everlyOn, Integer everlyOff,
            Integer richPicaOn, Integer richPicaOff, Integer shizoOn, Integer shizoOff, Integer ieviusOn,
            Integer ieviusOff, Integer bobsBuilderOn, Integer bobsBuilderOff, Integer plrxqOn, Integer plrxqOff,
            Integer emsiukemiauOn, Integer emsiukemiauOff) {
        this.itsVaidasOn = itsVaidasOn;
        this.itsVaidasOff = itsVaidasOff;
        this.scoutressOn = scoutressOn;
        this.scoutressOff = scoutressOff;
        this.mboti212On = mboti212On;
        this.mboti212Off = mboti212Off;
        this.furijaOn = furijaOn;
        this.furijaOff = furijaOff;
        this.ernestasltu12On = ernestasltu12On;
        this.ernestasltu12Off = ernestasltu12Off;
        this.d0fkaOn = d0fkaOn;
        this.d0fkaOff = d0fkaOff;
        this.melitaLoveOn = melitaLoveOn;
        this.melitaLoveOff = melitaLoveOff;
        this.libeteOn = libeteOn;
        this.libeteOff = libeteOff;
        this.arienaOn = arienaOn;
        this.arienaOff = arienaOff;
        this.sharansOn = sharansOn;
        this.sharansOff = sharansOff;
        this.labasheyOn = labasheyOn;
        this.labasheyOff = labasheyOff;
        this.everlyOn = everlyOn;
        this.everlyOff = everlyOff;
        this.richPicaOn = richPicaOn;
        this.richPicaOff = richPicaOff;
        this.shizoOn = shizoOn;
        this.shizoOff = shizoOff;
        this.ieviusOn = ieviusOn;
        this.ieviusOff = ieviusOff;
        this.bobsBuilderOn = bobsBuilderOn;
        this.bobsBuilderOff = bobsBuilderOff;
        this.plrxqOn = plrxqOn;
        this.plrxqOff = plrxqOff;
        this.emsiukemiauOn = emsiukemiauOn;
        this.emsiukemiauOff = emsiukemiauOff;
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
    
}
