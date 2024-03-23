package lt.scoutress.StatisticsApp.services.playtime;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.playtime.Playtime;

public interface PlaytimeService {

    List<Playtime> findAll();

    public void migrateSurvivalPlaytimeData();

    public void migrateSkyblockPlaytimeData();

    public void migrateCreativePlaytimeData();

    public void migrateBoxpvpPlaytimeData();

    public void migratePrisonPlaytimeData();

    public void migrateEventsPlaytimeData();
}
