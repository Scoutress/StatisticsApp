package lt.scoutress.StatisticsApp.Services.playtime;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Playtime.Playtime;

public interface PlaytimeService {

    List<Playtime> findAll();

    public void migrateSurvivalPlaytimeData();

    public void migrateSkyblockPlaytimeData();

    public void migrateCreativePlaytimeData();

    public void migrateBoxpvpPlaytimeData();

    public void migratePrisonPlaytimeData();

    public void migrateEventsPlaytimeData();

    // New methods
    // 1
    //public void migratePlaytimeData();

    // 2
    public void convertTimestampToDate();

    // 3
    public void calculateDailyPlaytimePerServerPerEmployee();
}
