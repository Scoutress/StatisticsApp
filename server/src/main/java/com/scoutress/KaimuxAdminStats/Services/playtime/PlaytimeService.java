package com.scoutress.KaimuxAdminStats.Services.Playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.Playtime;

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
