package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.playtime.Playtime;

public interface PlaytimeService {

    List<Playtime> findAll();
    
}
