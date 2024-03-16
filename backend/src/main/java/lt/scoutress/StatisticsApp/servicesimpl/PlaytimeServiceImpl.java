package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.playtime.Playtime;
import lt.scoutress.StatisticsApp.repositories.PlaytimeRepository;
import lt.scoutress.StatisticsApp.services.PlaytimeService;

@Service
public class PlaytimeServiceImpl implements PlaytimeService{

    public final PlaytimeRepository playtimeRepository;

    public PlaytimeServiceImpl(PlaytimeRepository playtimeRepository) {
        this.playtimeRepository = playtimeRepository;
    }

    @Override
    public List<Playtime> findAll() {
        return playtimeRepository.findAll();
    }
    
}
