package lt.scoutress.StatisticsApp.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.playtime.DailyPlaytime;

@Repository
public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Integer> {

    DailyPlaytime findByDate(LocalDate date);

    List<DailyPlaytime> findAllByDate(LocalDate date);    
}
